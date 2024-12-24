package com.common.module.network.grpc;

import com.common.module.internal.thread.pool.actor.ActorThreadPoolExecutor;
import com.common.module.internal.thread.task.linked.AbstractLinkedTask;
import com.game.proto.CommonProto;
import com.game.proto.RouteServiceGrpc;
import io.grpc.stub.StreamObserver;

public class RouteServiceImpl extends RouteServiceGrpc.RouteServiceImplBase {
    public ActorThreadPoolExecutor executor = new ActorThreadPoolExecutor("grpc-server-thread", Runtime.getRuntime().availableProcessors() * 2 + 1);
    public static StreamObserver<CommonProto.RouteResponse> routeResponseObserver;

    @Override
    public StreamObserver<CommonProto.RouteRequest> routeStream(StreamObserver<CommonProto.RouteResponse> responseObserver) {
        return new StreamObserver<CommonProto.RouteRequest>() {
            @Override
            public void onNext(CommonProto.RouteRequest routeRequest) {
                CommonProto.RouteResponse.newBuilder().build();
                executor.execute(new AbstractLinkedTask() {
                    @Override
                    public Object getIdentity() {
                        return routeRequest.getMsg().getPlayerId();
                    }

                    @Override
                    protected void exec() throws Exception {
                        // 游戏服处理路由消息
                    }
                });
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                if (routeResponseObserver == null) {
                    routeResponseObserver = responseObserver;
                }
            }
        };
    }
}
