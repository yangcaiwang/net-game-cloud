package com.ycw.gm.admin.service.impl;

import com.ycw.core.cluster.ClusterService;
import com.ycw.core.cluster.ClusterServiceImpl;
import com.ycw.core.cluster.entity.ServerEntity;
import com.ycw.core.cluster.property.PropertyConfig;
import com.ycw.core.cluster.template.*;
import com.ycw.core.internal.loader.service.ServiceContext;
import com.ycw.core.network.jetty.HttpClient;
import com.ycw.core.network.jetty.constant.HttpCmd;
import com.ycw.core.network.jetty.http.HttpCode;
import com.ycw.gm.admin.domain.GmServer;
import com.ycw.gm.admin.mapper.GmServerMapper;
import com.ycw.gm.admin.service.IServerService;
import com.ycw.gm.common.constant.Constants;
import com.ycw.gm.common.core.redis.RedisCache;
import com.ycw.gm.common.utils.ExecuteShellUtil;
import com.ycw.gm.common.utils.ParamParseUtils;
import com.ycw.gm.common.utils.SerializationUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Validator;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 服务器 业务层处理
 *
 * @author gamer
 */
@Service
public class ServerServiceImpl implements IServerService {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private GmServerMapper serverMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Autowired
    protected Validator validator;

    private ScheduledFuture<?> scheduledFuture;

    /**
     * 查询所有服务器
     *
     * @return 服务器列表
     */
    @Override
    public List<ServerEntity> selectServerAll() {
        ClusterService clusterService = ServiceContext.getInstance().get(ClusterServiceImpl.class);
        return clusterService.getAllServerEntity();
    }

    /**
     * 通过服务器ID查询服务器
     *
     * @param sid 服务器ID
     * @return 服务器对象信息
     */
    @Override
    public GmServer selectServerById(Long sid) {
        return serverMapper.selectServerById(sid);
    }

    @Override
    public List<GmServer> selectServerByIds(Long[] sids) {
        return serverMapper.selectServerByIds(sids);
    }

    @Override
    public long countServer() {
        return serverMapper.countServer();
    }

    private String genServerKey(String pid, String serverId) {
        return String.format("%s%s", pid, serverId);
    }

    private String getServerRedisKey(String sid) {
        return Constants.SERVER_INFO + sid;
    }

    /**
     * 修改保存服务器信息
     *
     * @param t 服务器信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateServer(ServerYmlTemplate serverYmlTemplate) {

        ClusterService clusterService = ServiceContext.getInstance().get(ClusterServiceImpl.class);
        StringBuilder url = new StringBuilder();
        Map<String, String> paramMap = new HashMap<>();
        try {
            NodeYmlTemplate node = serverYmlTemplate.getNode();
            if (node != null) {
                ServerEntity serverEntity = clusterService.getServerEntity(node.getServerId());
                serverEntity.setServerName(node.getServerName());
                serverEntity.setWeight(node.getWeight());
                serverEntity.setGroupId(node.getGroupId());
                serverEntity.setOpenTime(node.getOpenTime());

                BaseYmlTemplate grpc = serverYmlTemplate.getGrpc();
                if (grpc != null) {
                    serverEntity.getGrpcServerAddr().setPort(grpc.getPort());
                }
                BaseYmlTemplate jetty = serverYmlTemplate.getJetty();
                if (jetty != null) {
                    serverEntity.getJettyServerAddr().setPort(jetty.getPort());
                }
                BaseYmlTemplate netty = serverYmlTemplate.getNetty();
                if (netty != null) {
                   serverEntity.getNettyServerAddr().setPort(netty.getPort());
                }
                // 更新配置中心
                clusterService.saveServerEntity(serverEntity);
                // 更新目标服务器的serverYml
                paramMap.put("serverYmlTemplate", SerializationUtils.beanToJson(serverYmlTemplate));
                HttpClient.HttpResponse httpResponse = HttpClient.getInstance().sendGet(serverEntity.getJettyServerAddr().getAddress(), HttpCmd.MODIFY_SERVER_YML_CMD, paramMap,null);
                if (httpResponse != null && httpResponse.getCode() == HttpCode.SUCCESS.getIndex()) {
                    return 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

        return 0;
    }

    /**
     * 修改服务器状态
     *
     * @param server 服务器信息
     * @return 结果
     */
    @Override
    public int updateServerStatus(GmServer server) {
        return serverMapper.updateServer(server);
    }

    /**
     * 通过服务器ID删除服务器
     *
     * @param sid 服务器ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteServerById(Long sid) {
        GmServer server = serverMapper.selectServerById(sid);
        if (server != null) {
            redisCache.deleteObject(getServerRedisKey(String.valueOf(server.getServerKeyId())));
        }
        return serverMapper.deleteServerById(sid);
    }

    @Override
    public boolean serverOpenByTime(GmServer server, int openType, boolean create) {
        String key = Constants.SERVER_OPEN_BY_TIME + server.getServerKeyId();
        if (!create) {
            if (redisCache.getCacheObject(key) == null) {
                return false;
            }
        }
        if (openType == 1) {
            long openTime = server.getOpenTime().getTime() / 1000L;
            long curr = System.currentTimeMillis() / 1000L;
            if (openTime > curr) {
                redisCache.setCacheObject(key, openTime, (int) (openTime - curr + 10), TimeUnit.SECONDS);

                if (scheduledFuture == null) {
                    scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(this::checkServerOpenTime, 10, 10, TimeUnit.SECONDS);
                }
            }
        } else {
            redisCache.deleteObject(key);
        }
        return true;
    }

    public void checkServerOpenTime() {
        Collection<String> keys = redisCache.keys(Constants.SERVER_OPEN_BY_TIME + "*");
        Boolean aBoolean = redisCache.getCacheObject(Constants.SERVER_OPEN_TIME_LOCK);
        if (aBoolean != null) {
            return;
        }
        redisCache.setCacheObject(Constants.SERVER_OPEN_TIME_LOCK, true);
        boolean expire = redisCache.expire(Constants.SERVER_OPEN_TIME_LOCK, 30);
        if (expire) {
            try {
                long now = System.currentTimeMillis() / 1000L;
                for (String key : keys) {
                    Long cacheObject = redisCache.getCacheObject(key);
                    if (cacheObject != null && now >= cacheObject) {
                        changeStatus();
                        String substring = key.substring(Constants.SERVER_OPEN_BY_TIME.length());
                        long serverId = Long.parseLong(substring);
                        GmServer server = selectServerById(serverId);
                        if (server != null) {
                            GmServer tmpServer = new GmServer();
                            tmpServer.setServerKeyId(server.getServerKeyId());
                            tmpServer.setServerStatus("1");
                            tmpServer.setShowOut("1");
//                            updateServer(tmpServer);

                            updateGameServerOpenTime(server, cacheObject, true);
                            logger.info("open server:{} {} success", server.getServerKeyId(), server.getServerName());
                        }
                        redisCache.deleteObject(key);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                redisCache.deleteObject(Constants.SERVER_OPEN_TIME_LOCK);
            }
        }
    }

    @Override
    public void updateGameServerOpenTime(GmServer server, long openTime, boolean closeOpen) {
        Map<String, Object> map = new HashMap<>();
        map.put("serverOpenTime", openTime);
        map.put("cmd", "UpdateServerOpenTime");
        map.put("onlyOnce", "false");
        map.put("closeOpen", closeOpen);
        String url = ParamParseUtils.makeURL(server.getInHost(), server.getInPort(), "script");
        try {
            ParamParseUtils.sendSyncTokenPost(url, map);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void changeStatus() {
        List<ServerEntity> list = selectServerAll();
        for (ServerEntity serverEntity : list) {
            if (serverEntity.getServerState() == 1) {
                GmServer tmpServer = new GmServer();
//                tmpServer.setServerKeyId(serverEntity.getServerKeyId());
                tmpServer.setServerStatus(String.valueOf(2));
                updateServerStatus(tmpServer);
            }
        }
    }

    @Override
    public String stopServer(GmServer gmServer) {
        String homePath = gmServer.getHomePath();
        if (Strings.isEmpty(homePath)) {
            return null;
        }
        if (!homePath.endsWith("/")) {
            homePath += "/";
        }
        String bootName = PropertyConfig.getString("boot.name", "boot.sh");
        String stopCmd = "nohup " + homePath + "/" + bootName + " stop > /dev/null 2>&1 &";
        logger.info("执行服务器【{}】停服操作，命令：{}", gmServer.getServerId(), stopCmd);
        // nohup ./stop.sh > /dev/null 2>&1 &
        return executeShell(gmServer.getInHost(), stopCmd);
    }

    @Override
    public String startServer(GmServer gmServer) {
        String homePath = gmServer.getHomePath();
        if (Strings.isEmpty(homePath)) {
            return null;
        }
        if (!homePath.endsWith("/")) {
            homePath += "/";
        }
        String bootName = PropertyConfig.getString("boot.name", "boot.sh");
        String startCmd = "sh " + homePath + "/" + bootName + " start";
        logger.info("执行服务器【{}】启动操作，命令：{}", gmServer.getServerId(), startCmd);

        return executeShell(gmServer.getInHost(), startCmd);
    }

    @Override
    public String executeShell(String host, String... cmd) {
        try {
            return ExecuteShellUtil.execCmd(host, PropertyConfig.getString("inner.password", "123456"), cmd);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
