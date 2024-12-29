package com.ycw.core.network.grpc;

import com.ycw.core.cluster.entity.AddressInfo;
import com.ycw.core.cluster.entity.ServerEntity;
import com.ycw.core.internal.thread.pool.actor.ActorThreadPoolExecutor;
import com.ycw.core.internal.thread.task.linked.AbstractLinkedTask;
import com.ycw.core.network.netty.message.MessageProcess;
import com.ycw.proto.CommonProto;
import com.ycw.proto.RouteServiceGrpc;
import io.grpc.*;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <Grpc管理器实现类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class GrpcManager {
    public static final Logger logger = LoggerFactory.getLogger(GrpcManager.class);
    public final ActorThreadPoolExecutor serverExecutor = new ActorThreadPoolExecutor("grpc-server-thread", Runtime.getRuntime().availableProcessors() * 2 + 1);
    public final ActorThreadPoolExecutor clientExecutor = new ActorThreadPoolExecutor("grpc-client-thread", Runtime.getRuntime().availableProcessors() * 2 + 1);
    private Map<String, GrpcClient> grpcClientMap = new HashMap<>();

    private static GrpcManager grpcManager = new GrpcManager();

    public static GrpcManager getInstance() {
        return grpcManager;
    }

    /**
     * 开启grpc服务端
     *
     * @param port 端口号
     */
    public void startGrpcServer(int port, long keepAliveTime, long keepAliveTimeout) {
        GrpcServer grpcServer = new GrpcServer(keepAliveTime, keepAliveTimeout);
        grpcServer.start(port);
    }

    /**
     * 连接grpc服务器
     *
     * @param serverEntity 服务器对象
     */
    public void connectGrpcServer(ServerEntity serverEntity) {
        String targetServerId = serverEntity.getServerId();
        if (grpcClientMap.containsKey(serverEntity.getServerId())) {
            GrpcClient client = grpcClientMap.get(targetServerId);
            if (client.getChannel().getState(true) != ConnectivityState.READY) {
                client.start(serverEntity.getServerId(), serverEntity.getGrpcServerAddr());
            }
        } else {
            GrpcClient grpcClient = new GrpcClient();
            grpcClient.start(serverEntity.getServerId(), serverEntity.getGrpcServerAddr());
            grpcClientMap.put(targetServerId, grpcClient);
        }
    }

    public GrpcClient getGrpcClient(String serverId) {
        return grpcClientMap.get(serverId);
    }

    /**
     * <p>grpc 服务端
     *
     * @author yangcaiwang
     */
    public class GrpcServer {
        private long heartbeatTime;
        private long heartbeatTimeout;
        private Server server;

        public GrpcServer(long heartbeatTime, long heartbeatTimeout) {
            this.heartbeatTime = heartbeatTime;
            this.heartbeatTimeout = heartbeatTimeout;
        }

        /**
         * 启动服务端
         *
         * @param port 服务端端口号
         */
        private void start(int port) {
            try {

                server = ServerBuilder.forPort(port)
                        .addService(new RouteServiceImpl())
                        .permitKeepAliveWithoutCalls(true)
                        .keepAliveTime(heartbeatTime, TimeUnit.MILLISECONDS)
                        .keepAliveTimeout(heartbeatTimeout, TimeUnit.MILLISECONDS)
                        .build();

                server.start();
                Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                shutdown();
            }
        }

        /**
         * 发送路由消息
         *
         * @param routeResponse 路由信息
         */
        private void sentClient(CommonProto.RouteResponse routeResponse) {
            RouteServiceImpl.routeResponseObserver.onNext(routeResponse);
            RouteServiceImpl.routeResponseObserver.onCompleted();
        }

        /**
         * 停止服务
         */
        private void shutdown() {
            try {
                if (null != server) {
                    // 等待5秒钟，不关闭也会强制退出
                    server.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                    serverExecutor.shutdown();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * <p>grpc 客户端
     *
     * @author yangcaiwang
     */
    public class GrpcClient {
        private ManagedChannel channel;

        /**
         * 双向流模式
         */
        private StreamObserver<CommonProto.RouteRequest> routeRequestStreamObserver;

        /**
         * 启动客户端
         *
         * @param serverId    服务端id
         * @param addressInfo 服务器地址
         */
        private GrpcClient start(String serverId, AddressInfo addressInfo) {
            try {
                // 绑定服务端的ip和端口号，并使用明文传输
                channel = ManagedChannelBuilder
                        .forAddress(addressInfo.getHost(), addressInfo.getPort())
                        .usePlaintext()
                        .keepAliveWithoutCalls(true)
                        .keepAliveTime(addressInfo.getHeartbeatTime(), TimeUnit.MILLISECONDS)
                        .keepAliveTimeout(addressInfo.getHeartbeatTimeout(), TimeUnit.MILLISECONDS)
                        .build();
                logger.info("======================= [{}] grpc client connected ip:{} port:{} =======================", serverId, addressInfo.getHost(), addressInfo.getPort());
                Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
                RouteServiceGrpc.RouteServiceStub asyncStub = RouteServiceGrpc.newStub(channel);
                routeRequestStreamObserver = asyncStub.routeStream(new StreamObserver<CommonProto.RouteResponse>() {
                    @Override
                    public void onNext(CommonProto.RouteResponse routeResponse) {
                        CommonProto.RouteResponse.newBuilder().build();

                        // 处理接收到的消息
                        clientExecutor.execute(new AbstractLinkedTask() {
                            @Override
                            public Object getIdentity() {
                                return routeResponse.getMsg().getPlayerId();
                            }

                            @Override
                            protected void exec() throws Exception {
                                MessageProcess.getInstance().sent(routeResponse.getMsg().getPlayerId(), routeResponse.getMsg().getCmd(), MessageProcess.getInstance().messageToAny(routeResponse.getMsg()));
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onCompleted() {

                    }
                });
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                shutdown();
            }

            return this;
        }

        /**
         * 发送路由消息
         *
         * @param routeRequest 路由信息
         */
        public void sentServer(CommonProto.RouteRequest routeRequest) {
            routeRequestStreamObserver.onNext(routeRequest);
            routeRequestStreamObserver.onCompleted();
        }

        /**
         * 客户端关闭
         */
        private void shutdown() {
            try {
                if (null != channel) {
                    // 等待5秒钟，不关闭也会强制退出
                    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                    clientExecutor.shutdown();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        public ManagedChannel getChannel() {
            return channel;
        }

        public void setChannel(ManagedChannel channel) {
            this.channel = channel;
        }
    }
}
