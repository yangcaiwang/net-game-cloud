package com.ycw.core.network.grpc;

import com.game.proto.CommonProto;
import com.game.proto.RouteServiceGrpc;
import com.google.protobuf.ByteString;
import com.ycw.core.cluster.entity.AddressInfo;
import com.ycw.core.cluster.entity.ServerEntity;
import com.ycw.core.internal.thread.task.linked.AbstractLinkedTask;
import com.ycw.core.network.netty.handler.ControllerHandler;
import com.ycw.core.network.netty.handler.RouterHandler;
import com.ycw.core.network.netty.message.IMessage;
import com.ycw.core.network.netty.message.SocketChannelManage;
import com.ycw.core.network.netty.message.SocketMessage;
import com.ycw.core.util.SerializationUtils;
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
 * ps: Grpc双向流模式
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class GrpcManage {
    public static final Logger logger = LoggerFactory.getLogger(GrpcManage.class);
    private StreamObserver<CommonProto.RouteRequest> routeRequestStreamObserver;
    private Map<String, GrpcClient> grpcClientMap = new HashMap<>();
    private long heartbeatTime;
    private long heartbeatTimeout;
    private Server server;
    private static GrpcManage grpcManage = new GrpcManage();

    public static GrpcManage getInstance() {
        return grpcManage;
    }

    /**
     * 开启grpc服务端
     *
     * @param port 端口号
     */
    public void startGrpcServer(int port, long heartbeatTime, long heartbeatTimeout) {
        GrpcServer grpcServer = new GrpcServer();
        this.heartbeatTime = heartbeatTime;
        this.heartbeatTimeout = heartbeatTimeout;
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
     * 发送到路由器
     *
     * @param socketMessage proto消息
     */
    public void sentRouter(SocketMessage socketMessage) {
        CommonProto.RouteResponse build = CommonProto.RouteResponse.newBuilder()
                .setMsg(ByteString.copyFrom(SerializationUtils.toByteArrayByH2(socketMessage)))
                .build();
        GrpcRouter.routeResponseObserver.onNext(build);
        GrpcRouter.routeResponseObserver.onCompleted();
    }

    /**
     * 路由消息发送到控制器
     *
     * @param socketMessage proto消息
     */
    public void sentController(SocketMessage socketMessage) {
        CommonProto.RouteRequest build = CommonProto.RouteRequest.newBuilder()
                .setMsg(ByteString.copyFrom(SerializationUtils.toByteArrayByH2(socketMessage)))
                .build();
        routeRequestStreamObserver.onNext(build);
        routeRequestStreamObserver.onCompleted();
    }

    /**
     * <p>grpc 服务端
     *
     * @author yangcaiwang
     */
    public class GrpcServer {
        /**
         * 启动服务端
         *
         * @param port 服务端端口号
         */
        private void start(int port) {
            try {

                server = ServerBuilder.forPort(port)
                        .addService(new GrpcRouter())
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
         * 停止服务
         */
        private void shutdown() {
            try {
                if (null != server) {
                    // 等待5秒钟，不关闭也会强制退出
                    server.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                    ControllerHandler.actorExecutor.shutdown();
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
                        IMessage iMessage = SerializationUtils.toObjectByH2(routeResponse.getMsg().toByteArray());
                        try {
                            // 处理接收到的消息
                            RouterHandler.actorExecutor.execute(new AbstractLinkedTask() {
                                @Override
                                public Object getIdentity() {
                                    return iMessage.getPlayerId();
                                }

                                @Override
                                protected void exec() {
                                    SocketChannelManage.getInstance().sent(iMessage.getPlayerId(), iMessage.getCmd(), iMessage.getBytes());
                                }
                            });
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
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
         * 客户端关闭
         */
        private void shutdown() {
            try {
                if (null != channel) {
                    // 等待5秒钟，不关闭也会强制退出
                    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
                    RouterHandler.actorExecutor.shutdown();
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
