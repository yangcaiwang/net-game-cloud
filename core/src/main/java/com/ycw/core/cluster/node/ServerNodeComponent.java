package com.ycw.core.cluster.node;

import com.ycw.core.cluster.ClusterService;
import com.ycw.core.cluster.ClusterServiceImpl;
import com.ycw.core.cluster.entity.AddressInfo;
import com.ycw.core.cluster.entity.DataSourceInfo;
import com.ycw.core.cluster.entity.ServerEntity;
import com.ycw.core.cluster.enums.Begin;
import com.ycw.core.cluster.enums.ServerShowState;
import com.ycw.core.cluster.enums.ServerState;
import com.ycw.core.cluster.enums.ServerType;
import com.ycw.core.cluster.property.PropertyConfig;
import com.ycw.core.cluster.template.BaseYmlTemplate;
import com.ycw.core.cluster.template.DbYmlTemplate;
import com.ycw.core.cluster.template.NodeYmlTemplate;
import com.ycw.core.internal.cache.redission.RedissonClient;
import com.ycw.core.internal.cache.redission.constant.ConstTopic;
import com.ycw.core.internal.db.Mysql;
import com.ycw.core.internal.loader.service.ServiceContext;
import com.ycw.core.network.grpc.GrpcManager;
import com.ycw.core.network.jetty.HttpClient;
import com.ycw.core.network.jetty.JettyHttpServer;
import com.ycw.core.network.jetty.constant.HttpCmd;
import com.ycw.core.network.jetty.handler.JettyHttpHandler;
import com.ycw.core.network.netty.server.NettyServer;
import com.ycw.core.util.SerializationUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <服务器组件实现类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class ServerNodeComponent extends AbstractServerNode {

    public static <T extends AbstractServerNode> T valueOf(ServerType serverType) {
        serverNodeComponent = new ServerNodeComponent();
        serverNodeComponent.setServerType(serverType);
        return (T) serverNodeComponent;
    }

    private static ServerNodeComponent serverNodeComponent;

    public static ServerNodeComponent getInstance() {
        return serverNodeComponent;
    }

    @Override
    public void registerRedission() {
        try {
            NodeYmlTemplate nodeYml = serverYmlTemplate.getNode(); // 解析node配置
            BaseYmlTemplate grpcYml = serverYmlTemplate.getGrpc();  // 解析grpc配置
            BaseYmlTemplate jettyYml = serverYmlTemplate.getJetty(); // 解析jetty配置
            BaseYmlTemplate nettyYml = serverYmlTemplate.getNetty(); // 解析netty配置
            DbYmlTemplate dbGameYml = serverYmlTemplate.getDbGame(); // 解析dbGame配置
            DbYmlTemplate dbLogYml = serverYmlTemplate.getDbLog();// 解析dbLog配置
            if (nodeYml != null) {
                // 构建服务器实体对象
                ServerEntity serverEntity = new ServerEntity.Builder()
                        .serverId(nodeYml.getServerId())
                        .serverName(nodeYml.getServerName())
                        .serverType(this.serverType().value)
                        .serverState(ServerState.NORMAL.state)
                        .serverShowState(ServerType.isGameServer(this.serverType.value) ? ServerShowState.WHITE.state : ServerShowState.NONE.state)
                        .groupId(nodeYml.getGroupId())
                        .weight(nodeYml.getWeight())
                        .serverAddr(AddressInfo.valueOf(nodeYml.getHost(), nodeYml.getPort()))
                        .grpcServerAddr(grpcYml == null ? AddressInfo.valueOf() : AddressInfo.valueOf(nodeYml.getHost(), grpcYml.getPort(), grpcYml.getHeartbeatTime(), grpcYml.getHeartbeatTimeout()))
                        .jettyServerAddr(jettyYml == null ? AddressInfo.valueOf() : AddressInfo.valueOf(nodeYml.getHost(), jettyYml.getPort()))
                        .nettyServerAddr(nettyYml == null ? AddressInfo.valueOf() : AddressInfo.valueOf(nodeYml.getHost(), nettyYml.getPort()))
                        .dbGameSourceInfo(dbGameYml == null ? DataSourceInfo.valueOf() : DataSourceInfo.valueOf(dbGameYml.getUrl(), dbGameYml.getUsername(), dbGameYml.getPassword()))
                        .dbLogSourceInfo(dbLogYml == null ? DataSourceInfo.valueOf() : DataSourceInfo.valueOf(dbLogYml.getUrl(), dbLogYml.getUsername(), dbLogYml.getPassword()))
                        .openTime(nodeYml.getOpenTime())
                        .registerTime(System.currentTimeMillis())
                        .build();

                // 将服务器实体写入注册中心(redission)
                ClusterService clusterService = ServiceContext.getInstance().get(ClusterServiceImpl.class);
                clusterService.saveServerEntity(serverEntity);
                log.info("======================= [{}] server registered [{}] =======================", getServerId(), serverEntity.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    protected void startRedission() {
        try {
            if (Begin.getInstance(Begin.REDISSION).isBegin(this.serverType)) {
                RedissonClient.getInstance().start(PropertyConfig.getAbsolutePath("redission.yml", serverType));
                log.info("======================= [{}] redission client started =======================", getServerId());
                RedissonClient.getInstance().subscribeTopic(ConstTopic.TOPIC_GRPC_CLIENT);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    protected void startDatabase() {
        if (Begin.getInstance(Begin.DB_GAME).isBegin(this.serverType)) {
            try {
                DbYmlTemplate dbGameTemplate = serverYmlTemplate.getDbLog();
                Mysql.getInstance().createPools(dbGameTemplate);
                log.info("======================= [{}] db_game client started =======================", getServerId());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        if (Begin.getInstance(Begin.DB_LOG).isBegin(this.serverType)) {
            try {
                DbYmlTemplate dbLogTemplate = serverYmlTemplate.getDbLog();

                Mysql.getInstance().createPools(dbLogTemplate);
                log.info("======================= [{}] db_log client started =======================", getServerId());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public void connectGrpcServer() {
        if (Begin.getInstance(Begin.GRPC_CLI).isBegin(this.serverType)) {
            try {
                ClusterService clusterService = ServiceContext.getInstance().get(ClusterServiceImpl.class);
                ServerEntity serverEntity = clusterService.getServerEntity(getServerId());
                if (serverEntity == null) {
                    return;
                }

                // 连接当前组所有Grpc服务器
                BaseYmlTemplate grpcYmlTemplate = serverYmlTemplate.getGrpc();
                if (grpcYmlTemplate != null) {
                    List<ServerEntity> serverEntityList = clusterService.getAllGrpcServerEntityByGroup(serverEntity.getGroupId());
                    for (ServerEntity entity : serverEntityList) {
                        if (serverEntity.getConnectGrpcServerIds().contains(entity.getServerId()) || ServerState.isError(entity.getServerState())) {
                            continue;
                        }
                        // 更新网关服务器Grpc服务器id列表
                        serverEntity.getConnectGrpcServerIds().add(entity.getServerId());
                        clusterService.saveServerEntity(serverEntity);
                        // 连接Grpc服务器
                        GrpcManager.getInstance().connectGrpcServer(entity);
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Override
    protected void startGrpcServer() {
        if (Begin.getInstance(Begin.GRPC_SER).isBegin(this.serverType)) {
            try {
                NodeYmlTemplate nodeYml = serverYmlTemplate.getNode();
                BaseYmlTemplate grpcYml = serverYmlTemplate.getGrpc();
                if (grpcYml != null) {
                    // 开启grpc服务端
                    GrpcManager.getInstance().startGrpcServer(grpcYml.getPort(), grpcYml.getHeartbeatTime(), grpcYml.getHeartbeatTimeout());
                    log.info("======================= [{}] grpc server started ip:{} port:{} =======================", getServerId(), nodeYml.getHost(), grpcYml.getPort());

                    ClusterService clusterService = ServiceContext.getInstance().get(ClusterServiceImpl.class);
                    List<ServerEntity> serverEntityList = clusterService.getGateServerEntity(nodeYml.getGroupId());
                    if (CollectionUtils.isEmpty(serverEntityList)) {
                        return;
                    }

                    for (ServerEntity serverEntity : serverEntityList) {
                        // 如果不存在 则添加
                        if (serverEntity.getConnectGrpcServerIds().contains(nodeYml.getServerId()) || ServerState.isError(serverEntity.getServerState())) {
                            continue;
                        }
                        // 更新网关服务器Grpc服务器id列表
                        serverEntity.getConnectGrpcServerIds().add(nodeYml.getServerId());
                        clusterService.saveServerEntity(serverEntity);
                        // 通过Jetty发布连接当前组Grpc服务器事件
                        Map<String, String> params = new HashMap<>();
                        params.put("serverEntity", SerializationUtils.beanToJson(clusterService.getServerEntity(nodeYml.getServerId())));
                        HttpClient.getInstance().sendGet(serverEntity.getServerAddr().getAddress(), HttpCmd.CONNECT_GRPC_SERVER_CMD, params, null);
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Override
    protected void startNettyServer() {
        if (Begin.getInstance(Begin.NETTY).isBegin(this.serverType)) {
            try {
                NodeYmlTemplate nodeYml = serverYmlTemplate.getNode();
                BaseYmlTemplate nettyYml = serverYmlTemplate.getNetty();
                if (nettyYml != null) {
                    NettyServer.getInstance().start(nodeYml.getHost(), nettyYml.getPort(), nettyYml.getHeartbeatTime(), nettyYml.getHeartbeatTimeout());
                    log.info("======================= [{}] netty server started ip:{} port:{} =======================", getServerId(), nodeYml.getHost(), nettyYml.getPort());
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Override
    protected void startJettyServer() {
        if (Begin.getInstance(Begin.JETTY).isBegin(this.serverType)) {
            try {
                NodeYmlTemplate nodeYml = serverYmlTemplate.getNode();
                BaseYmlTemplate jettyYml = serverYmlTemplate.getJetty();
                if (jettyYml != null) {
                    JettyHttpServer.getInstance().start(new JettyHttpHandler(), jettyYml, this.serverType);
                    log.info("======================= [{}] jetty server started ip:{} port:{} =======================", getServerId(), nodeYml.getHost(), jettyYml.getPort());
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
