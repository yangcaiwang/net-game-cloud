package com.ycw.core.network.grpc;

import com.ycw.core.network.netty.handler.ControllerHandler;
import com.ycw.proto.CommonProto;
import com.ycw.proto.RouteServiceGrpc;
import io.grpc.stub.StreamObserver;

/**
 * <Grpc路由器实现类>
 * <p>
 * ps: 双向流模式
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class RouteServiceImpl extends RouteServiceGrpc.RouteServiceImplBase {
    private final ControllerHandler routeProtobufMsgListener = new ControllerHandler();
    public static StreamObserver<CommonProto.RouteResponse> routeResponseObserver;

    @Override
    public StreamObserver<CommonProto.RouteRequest> routeStream(StreamObserver<CommonProto.RouteResponse> responseObserver) {
        return new StreamObserver<CommonProto.RouteRequest>() {
            @Override
            public void onNext(CommonProto.RouteRequest routeRequest) {
                routeProtobufMsgListener.handle(GrpcManager.getInstance().serverExecutor,routeRequest.getMsg());
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
