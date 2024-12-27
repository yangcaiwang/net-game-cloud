package com.ycw.core.cluster.node;

import com.ycw.core.cluster.ClusterService;
import com.ycw.core.cluster.entity.AddressInfo;
import com.ycw.core.cluster.entity.DataSourceInfo;
import com.ycw.core.cluster.entity.ServerEntity;
import com.ycw.core.cluster.enums.Begin;
import com.ycw.core.cluster.enums.ServerState;
import com.ycw.core.cluster.enums.ServerType;
import com.ycw.core.cluster.property.PropertyConfig;
import com.ycw.core.cluster.template.*;
import com.ycw.core.internal.cache.redission.RedissonClient;
import com.ycw.core.internal.cache.redission.constant.ConstTopic;
import com.ycw.core.internal.cache.redission.event.TopicMessage;
import com.ycw.core.internal.db.Mysql;
import com.ycw.core.internal.loader.service.ServiceContext;
import com.ycw.core.network.grpc.GrpcManager;
import com.ycw.core.network.grpc.GrpcTopicMessage;
import com.ycw.core.network.jetty.JettyHttpServer;
import com.ycw.core.network.jetty.handler.JettyHttpHandler;
import com.ycw.core.network.netty.server.NettyServer;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <服务器组件实现类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class ServerNode extends AbstractServerNode {

    public static <T extends AbstractServerNode> T valueOf(ServerType serverType) {
        ServerNode serverComponent = new ServerNode();
        serverComponent.setServerType(serverType);
        return (T) serverComponent;
    }

    @Override
    public void startRegister() {
        try {
            NodeYmlTemplate nodeYml = serverYmlTemplate.getNode(); // 解析node配置
            GrpcYmlTemplate grpcYml = serverYmlTemplate.getGrpc();  // 解析grpc配置
            JettyYmlTemplate jettyYml = serverYmlTemplate.getJetty(); // 解析jetty配置
            NettyYmlTemplate nettyYml = serverYmlTemplate.getNetty(); // 解析netty配置
            DbYmlTemplate dbGameYml = serverYmlTemplate.getDbGame(); // 解析dbGame配置
            DbYmlTemplate dbLogYml = serverYmlTemplate.getDbLog();// 解析dbLog配置
            if (nodeYml != null) {
                // 构建服务器实体对象
                ServerEntity serverEntity = new ServerEntity.Builder()
                        .serverId(nodeYml.getServerId())
                        .serverType(this.serverType())
                        .serverState(ServerState.REGISTER)
                        .groupId(nodeYml.getGroupId())
                        .weight(nodeYml.getWeight())
                        .serverAddr(AddressInfo.valueOf(nodeYml.getHost(), nodeYml.getPort()))
                        .grpcServerAddr(grpcYml == null ? AddressInfo.valueOf() : AddressInfo.valueOf(nodeYml.getHost(), grpcYml.getPort()))
                        .jettyServerAddr(jettyYml == null ? AddressInfo.valueOf() : AddressInfo.valueOf(nodeYml.getHost(), jettyYml.getPort()))
                        .nettyServerAddr(nettyYml == null ? AddressInfo.valueOf() : AddressInfo.valueOf(nodeYml.getHost(), nettyYml.getPort()))
                        .dbGameSourceInfo(dbGameYml == null ? DataSourceInfo.valueOf() : DataSourceInfo.valueOf(dbGameYml.getUrl(), dbGameYml.getUsername(), dbGameYml.getPassword()))
                        .dbLogSourceInfo(dbLogYml == null ? DataSourceInfo.valueOf() : DataSourceInfo.valueOf(dbLogYml.getUrl(), dbLogYml.getUsername(), dbLogYml.getPassword()))
                        .build();


                // 将服务器实体写入注册中心(redission)
                ClusterService clusterService = ServiceContext.getInstance().get(ClusterService.class);
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
    public void startGrpcClient() {
        if (Begin.getInstance(Begin.GRPC_CLI).isBegin(this.serverType)) {
            try {
                ClusterService clusterService = ServiceContext.getInstance().get(ClusterService.class);
                ServerEntity serverEntity = clusterService.getServerEntity(getServerId());
                if (serverEntity == null) {
                    return;
                }

                // 开启grpc客户端
                GrpcYmlTemplate grpcYmlTemplate = serverYmlTemplate.getGrpc();
                if (grpcYmlTemplate != null) {
                    GrpcManager.getInstance().startGrpcClient(serverEntity, grpcYmlTemplate.getHeartbeatTime(), grpcYmlTemplate.getHeartbeatTimeout());
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
                GrpcYmlTemplate grpcYml = serverYmlTemplate.getGrpc();
                if (grpcYml != null) {
                    // 开启grpc服务端
                    GrpcManager.getInstance().startGrpcServer(grpcYml.getPort(), grpcYml.getHeartbeatTime(), grpcYml.getHeartbeatTimeout());
                    log.info("======================= [{}] grpc server started ip:{} port:{} =======================", getServerId(), nodeYml.getHost(), grpcYml.getPort());

                    // 发布开启grpc客户端话题事件
                    ClusterService clusterService = ServiceContext.getInstance().get(ClusterService.class);
                    ServerEntity gateServerEntity = clusterService.getGateServerEntity(nodeYml.getGroupId());
                    if (gateServerEntity == null) {
                        return;
                    }

                    // 如果不存在 则添加
                    ConcurrentHashMap<String, AddressInfo> grpcClientAddr = gateServerEntity.getGrpcClientAddr();
                    if (!grpcClientAddr.contains(nodeYml.getServerId())) {
                        grpcClientAddr.put(nodeYml.getServerId(), AddressInfo.valueOf(nodeYml.getHost(), grpcYml.getPort()));
                        clusterService.saveServerEntity(gateServerEntity);
                    }

                    TopicMessage grpcTopicMsg = new GrpcTopicMessage(ConstTopic.TOPIC_GRPC_CLIENT, gateServerEntity);
                    RedissonClient.getInstance().publishTopic(grpcTopicMsg);
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
                NettyYmlTemplate nettyYml = serverYmlTemplate.getNetty();
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
                JettyYmlTemplate jettyYml = serverYmlTemplate.getJetty();
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
