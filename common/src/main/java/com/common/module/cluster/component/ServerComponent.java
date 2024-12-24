package com.common.module.cluster.component;

import com.common.module.cluster.ClusterService;
import com.common.module.cluster.ClusterServiceImpl;
import com.common.module.cluster.constant.ClusterConstant;
import com.common.module.cluster.entity.Address;
import com.common.module.cluster.entity.ServerEntity;
import com.common.module.cluster.enums.Begin;
import com.common.module.cluster.enums.ServerType;
import com.common.module.cluster.property.PropertyConfig;
import com.common.module.internal.cache.redission.RedissonClient;
import com.common.module.internal.cache.redission.topic.ConstTopic;
import com.common.module.internal.cache.redission.topic.TopicMessage;
import com.common.module.internal.db.Mysql;
import com.common.module.internal.loader.service.ServiceContext;
import com.common.module.network.grpc.GrpcManager;
import com.common.module.network.grpc.GrpcTopicMessage;
import com.common.module.network.jetty.JettyHttpServer;
import com.common.module.network.jetty.handler.JettyHttpHandler;
import com.common.module.network.netty.server.NettyServer;
import com.common.module.util.SerializationUtils;

import java.util.Map;
import java.util.Properties;

public class ServerComponent extends AbstractServerComponent {

    public static <T extends AbstractServerComponent> T valueOf(ServerType serverType) {
        ServerComponent serverComponent = new ServerComponent();
        serverComponent.setServerType(serverType);
        return (T) serverComponent;
    }

    @Override
    public void startRegister() {
        // 初始化服务器对象
        Object cluster = clusterMap.get(ClusterConstant.CLUSTER_PREFIX);
        ServerEntity serverEntity = SerializationUtils.jsonToBean(SerializationUtils.beanToJson(cluster), ServerEntity.class);
        serverEntity.setServerType(this.serverType());

        Object grpc = clusterMap.get(Begin.GRPC_S.key);
        Address address = SerializationUtils.jsonToBean(SerializationUtils.beanToJson(grpc), Address.class);

        serverEntity.setGrpcServerHost(serverEntity.getHost());
        serverEntity.setGrpcServerPort(address.getPort());

        // 把服务器注册到Redission
        ClusterService clusterService = ServiceContext.getInstance().get(ClusterService.class);
        boolean b = clusterService.saveServerEntity(serverEntity);
        if (b) {
            if (!Begin.getInstance(Begin.GRPC_C).isBegin(this.serverType)) {
                return;
            }

            GrpcManager.getInstance().startGrpcClient(serverEntity);
            log.info("======================= [{}] server registered =======================", this.serverType().getServerId());
        }
    }

    @Override
    protected void startRedission() {
        try {
            String pathname = PropertyConfig.getPrefixPath() + System.getProperty(ClusterConstant.REDISSION_PATH);
            RedissonClient.start(pathname);
            log.info("======================= [{}] redission client started =======================", this.serverType().getServerId());

            if (!Begin.getInstance(Begin.GRPC_C).isBegin(this.serverType)) {
                return;
            }

            RedissonClient.subscribeTopic(ConstTopic.TOPIC_GRPC_CLIENT);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(-1);
        }
//        try {
//            Map<String, Object> redisMap = (Map<String, Object>) clusterMap.get("redis");
//            String host = (String) redisMap.get("host");
//            int port = (int) redisMap.get("port");
//            String password = (String) redisMap.get("password");
//            int database = (int) redisMap.get("database");
//            Map<String, Integer> poolMap = (Map<String, Integer>) redisMap.get("pool");
//            int maxActive = poolMap.get("max-active");
//            int maxWait = poolMap.get("max-wait");
//            int maxIdle = poolMap.get("max-idle");
//            int minIdle = poolMap.get("min-idle");
//            RedisUtil.createRedisCli(host, port, password, database, maxActive, maxWait, maxIdle, minIdle);
//            log.info("已启动redis:{} " + ":{}", host, port);
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            System.exit(-1);
//        }
    }

    @Override
    protected void startDb() {
        try {
            String gamePath = PropertyConfig.getPrefixPath() + System.getProperty(ClusterConstant.DB_GAME_PATH);
            String logPath = PropertyConfig.getPrefixPath() + System.getProperty(ClusterConstant.DB_LOG_PATH);
            Properties[] properties = PropertyConfig.loadProperties(gamePath, logPath);
            Mysql.getInstance().createPools(properties);
            log.info("======================= [{}] mysql client started =======================", this.serverType().getServerId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(-1);
        }
    }

    @Override
    protected void startGrpcServer() {
        if (!Begin.getInstance(Begin.GRPC_S).isBegin(this.serverType)) {
            return;
        }

        try {
            ClusterService clusterService = ServiceContext.getInstance().get(ClusterService.class);
            ServerEntity serverEntity = clusterService.getServerEntity(this.serverType().getServerId());
            if (serverEntity == null) {
                return;
            }

            // 开启grpc服务端
            GrpcManager.getInstance().startGrpcServer(serverEntity.getGrpcServerPort());
            log.info("======================= [{}] grpc server started port:{} =======================", this.serverType().getServerId(), serverEntity.getGrpcServerPort());

            // 发布创建网关grpc客户端事件
            ServerEntity gateServerEntity = clusterService.getGateServerEntity(serverEntity.getGroupId());
            if (gateServerEntity == null) {
                return;
            }

            if (gateServerEntity.getGrpcServerId().contains(serverEntity.getServerId())
                    && gateServerEntity.getGrpcClientHost().contains(serverEntity.getGrpcServerHost())
                    && gateServerEntity.getGrpcClientPort().contains(serverEntity.getGrpcServerPort())) {
                TopicMessage grpcTopicMsg = new GrpcTopicMessage(ConstTopic.TOPIC_GRPC_CLIENT, gateServerEntity);
                RedissonClient.publishTopic(grpcTopicMsg);
                return;
            }
            gateServerEntity.getGrpcServerId().add(serverEntity.getServerId());
            gateServerEntity.getGrpcClientHost().add(serverEntity.getGrpcServerHost());
            gateServerEntity.getGrpcClientPort().add(serverEntity.getGrpcServerPort());
            TopicMessage grpcTopicMsg = new GrpcTopicMessage(ConstTopic.TOPIC_GRPC_CLIENT, gateServerEntity);
            RedissonClient.publishTopic(grpcTopicMsg);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(-1);
        }
    }

    @Override
    protected void startNettyServer() {
        if (!Begin.getInstance(Begin.NETTY).isBegin(this.serverType)) {
            return;
        }

        try {
            Object netty = clusterMap.get(Begin.NETTY.key);
            Address address = SerializationUtils.jsonToBean(SerializationUtils.beanToJson(netty), Address.class);
            NettyServer.bind(address.getHost(), address.getPort());
            log.info("======================= [{}] websocket server started ip:{} port:{} =======================", this.serverType().getServerId(), address.getHost(), address.getPort());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(-1);
        }
    }

    @Override
    protected void startJettyServer() {
        if (!Begin.getInstance(Begin.JETTY).isBegin(this.serverType)) {
            return;
        }

        try {
            ClusterServiceImpl clusterService = ServiceContext.getInstance().get(ClusterService.class);
            ServerEntity serverEntity = clusterService.getServerEntity(this.serverType().getServerId());
            if (serverEntity == null) {
                return;
            }

            Object jetty = clusterMap.get(Begin.JETTY.key);
            Map<String, Integer> map = SerializationUtils.jsonToBean(SerializationUtils.beanToJson(jetty), Map.class);
            JettyHttpServer.start(new JettyHttpHandler(), map);
            serverEntity.setJettyHost(serverEntity.getHost());
            serverEntity.setJettyPort(map.get("port"));
            clusterService.saveServerEntity(serverEntity);
            log.info("======================= [{}] jetty server started port:{} =======================", this.serverType().getServerId(), serverEntity.getJettyPort());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
