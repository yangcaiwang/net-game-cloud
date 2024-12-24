package com.common.module.network.grpc;

import com.common.module.cluster.entity.ServerEntity;
import com.common.module.internal.thread.pool.actor.ActorThreadPoolExecutor;
import com.common.module.internal.thread.task.linked.AbstractLinkedTask;
import com.common.module.network.netty.message.MsgManager;
import com.game.proto.CommonProto;
import com.game.proto.RouteServiceGrpc;
import io.grpc.*;
import io.grpc.stub.StreamObserver;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class GrpcManager {

    private Map<String, GrpcClient> grpcClientMap = new HashMap<>();

    public static GrpcManager grpcManager = new GrpcManager();

    public static GrpcManager getInstance() {
        return grpcManager;
    }

    public void startGrpcClient(ServerEntity gateServerEntity) {

        CopyOnWriteArrayList<String> serverIds = gateServerEntity.getGrpcServerId();
        CopyOnWriteArrayList<String> hosts = gateServerEntity.getGrpcClientHost();
        CopyOnWriteArrayList<Integer> ports = gateServerEntity.getGrpcClientPort();
        if (CollectionUtils.isEmpty(serverIds) || CollectionUtils.isEmpty(hosts) || CollectionUtils.isEmpty(ports)) {
            return;
        }

        if (serverIds.size() == hosts.size() && serverIds.size() == ports.size()) {
            for (int i = 0; i < hosts.size(); i++) {
                String serverId = serverIds.get(i);
                String host = hosts.get(i);
                int port = ports.get(i);
                if (grpcClientMap.containsKey(serverId)) {
                    GrpcClient client = grpcClientMap.get(serverId);
                    if (client.getChannel().getState(true) != ConnectivityState.READY) {
                        client.start(host, port);
                    }
                } else {
                    GrpcClient grpcClient = new GrpcClient();
                    grpcClient.start(host, port);
                    grpcClientMap.put(serverId, grpcClient);
                }
            }
        }
    }

    public void startGrpcServer(int port) {
        GrpcServer grpcServer = new GrpcServer();
        grpcServer.start(port);
    }

    public GrpcClient getGrpcClient(String serverId) {
        return grpcClientMap.get(serverId);
    }

    public class GrpcServer {
        private ActorThreadPoolExecutor executor = new ActorThreadPoolExecutor("grpc-server-thread", Runtime.getRuntime().availableProcessors() * 2 + 1);

        private Server server;

        /**
         * 启动服务端
         *
         * @param port 服务端端口号
         */
        private void start(int port) {
            try {

                server = ServerBuilder.forPort(port)
                        .addService(new RouteServiceImpl())
                        .build();

                server.start();
//            server.awaitTermination();
            } catch (Exception e) {
                e.printStackTrace();
                server.shutdown();
                executor.shutdown();
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
                    executor.shutdown();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * <p>grpc 客户端
     *
     * @author yangcaiwang
     */
    public class GrpcClient {
        private ActorThreadPoolExecutor executor = new ActorThreadPoolExecutor("grpc-client-thread", Runtime.getRuntime().availableProcessors() * 2 + 1);

        private ManagedChannel channel;

        /**
         * 异步存根，支持一元流、服务端流、客户端流、双向流四种模式
         */
        private RouteServiceGrpc.RouteServiceStub asyncStub;
        private StreamObserver<CommonProto.RouteRequest> routeRequestStreamObserver;

        /**
         * 启动客户端
         *
         * @param host 服务端host
         * @param port 服务端端口号
         */
        private GrpcClient start(String host, int port) {
            try {
                // 绑定服务端的ip和端口号，并使用明文传输
                channel = ManagedChannelBuilder
                        .forAddress(host, port)
                        .usePlaintext()
                        .build();
                RouteServiceGrpc.RouteServiceStub asyncStub = RouteServiceGrpc.newStub(channel);

                routeRequestStreamObserver = asyncStub.routeStream(new StreamObserver<CommonProto.RouteResponse>() {
                    @Override
                    public void onNext(CommonProto.RouteResponse routeResponse) {
                        CommonProto.RouteResponse.newBuilder().build();

                        // 处理接收到的消息
                        executor.execute(new AbstractLinkedTask() {
                            @Override
                            public Object getIdentity() {
                                return routeResponse.getMsg().getPlayerId();
                            }

                            @Override
                            protected void exec() throws Exception {
                                MsgManager.sent(routeResponse.getMsg().getPlayerId(), routeResponse.getMsg().getCmd(), MsgManager.messageToAny(routeResponse.getMsg()));
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
                e.printStackTrace();
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
                if (null != asyncStub) {
                    ManagedChannel channel = (ManagedChannel) asyncStub.getChannel();
                    channel.shutdown().awaitTermination(3, TimeUnit.SECONDS);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public ManagedChannel getChannel() {
            return channel;
        }

        public void setChannel(ManagedChannel channel) {
            this.channel = channel;
        }

        public RouteServiceGrpc.RouteServiceStub getAsyncStub() {
            return asyncStub;
        }

        public void setAsyncStub(RouteServiceGrpc.RouteServiceStub asyncStub) {
            this.asyncStub = asyncStub;
        }
    }
}
