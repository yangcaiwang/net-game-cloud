package com.common.module.internal.heart.jetty;

import com.common.module.cluster.ClusterService;
import com.common.module.cluster.entity.ServerEntity;
import com.common.module.cluster.enums.ServerState;
import com.common.module.cluster.enums.ServerType;
import com.common.module.internal.heart.HeartbeatProcess;
import com.common.module.internal.loader.service.ServiceContext;
import com.common.module.internal.thread.pool.actor.TimerActorThread;
import com.common.module.network.jetty.HttpClient;
import com.common.module.network.jetty.constant.HttpCommands;
import org.apache.commons.collections.MapUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <jetty心跳机制处理器实现类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class JettyHeartbeatProcess implements HeartbeatProcess {
    private long heartbeatTime;
    private long heartbeatTimeout;
    private Map<String, Long> heartbeatMap = new HashMap<>();
    private TimerActorThread threadSender;
    private TimerActorThread threadMonitor;

    public JettyHeartbeatProcess(long heartbeatTime, long heartbeatTimeout) {
        this.heartbeatTime = heartbeatTime;
        this.heartbeatTimeout = heartbeatTimeout;
    }

    @Override
    public void sent() {
        threadSender = new TimerActorThread("jettyHeartbeat-threadSender", heartbeatTime, () -> {
            try {
                ClusterService clusterService = ServiceContext.getInstance().get(ClusterService.class);
                ServerEntity serverEntity = clusterService.getServerEntity(ServerType.GM_SERVER);
                if (serverEntity == null) {
                    return;
                }

                String jettyHost = serverEntity.getJettyHost();
                int jettyPort = serverEntity.getJettyPort();
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("serverId", serverEntity.getServerId());
                StringBuffer url = new StringBuffer();
                url.append(HttpCommands.HTTP_PREFIX).append(jettyHost).append(":").append(jettyPort).append(HttpCommands.HEARTBEAT);
                HttpClient.getInstance().sendGet(url.toString(), paramMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void monitor() {
        threadMonitor = new TimerActorThread("jettyHeartbeat-threadSender", heartbeatTime, () -> {
            try {
                if (MapUtils.isNotEmpty(heartbeatMap)) {
                    for (Map.Entry<String, Long> entry : heartbeatMap.entrySet()) {
                        String serverId = entry.getKey();
                        long lastHeartbeatTime = entry.getValue();
                        // 心跳超时 改变服务器状态
                        ClusterService clusterService = ServiceContext.getInstance().get(ClusterService.class);
                        ServerEntity serverEntity = clusterService.getServerEntity(serverId);
                        if (isTimeOut(lastHeartbeatTime)) {
                            serverEntity.setServerState(ServerState.ERROR);
                            clusterService.saveServerEntity(serverEntity);
                        } else {
                            if (serverEntity.getServerState() == ServerState.ERROR) {
                                serverEntity.setServerState(ServerState.NORMAL);
                                clusterService.saveServerEntity(serverEntity);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public boolean isTimeOut(long lastHeartbeatTime) {
        return System.currentTimeMillis() - lastHeartbeatTime >= heartbeatTimeout;
    }

    @Override
    public void showdown() {
        try {
            if (threadSender != null) {
                threadSender.shutdown();
            }
            if (threadMonitor != null) {
                threadMonitor.shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Long> getHeartbeatMap() {
        return heartbeatMap;
    }
}
