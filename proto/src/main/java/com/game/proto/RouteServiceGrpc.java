package com.game.proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * 双向流服务间调用接口(客户端不断向服务端写，服务端不断向客户端写)
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.51.0)",
    comments = "Source: common.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class RouteServiceGrpc {

  private RouteServiceGrpc() {}

  public static final String SERVICE_NAME = "common.RouteService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.game.proto.CommonProto.RouteRequest,
      com.game.proto.CommonProto.RouteResponse> getRouteStreamMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "routeStream",
      requestType = com.game.proto.CommonProto.RouteRequest.class,
      responseType = com.game.proto.CommonProto.RouteResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<com.game.proto.CommonProto.RouteRequest,
      com.game.proto.CommonProto.RouteResponse> getRouteStreamMethod() {
    io.grpc.MethodDescriptor<com.game.proto.CommonProto.RouteRequest, com.game.proto.CommonProto.RouteResponse> getRouteStreamMethod;
    if ((getRouteStreamMethod = RouteServiceGrpc.getRouteStreamMethod) == null) {
      synchronized (RouteServiceGrpc.class) {
        if ((getRouteStreamMethod = RouteServiceGrpc.getRouteStreamMethod) == null) {
          RouteServiceGrpc.getRouteStreamMethod = getRouteStreamMethod =
              io.grpc.MethodDescriptor.<com.game.proto.CommonProto.RouteRequest, com.game.proto.CommonProto.RouteResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "routeStream"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.game.proto.CommonProto.RouteRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.game.proto.CommonProto.RouteResponse.getDefaultInstance()))
              .setSchemaDescriptor(new RouteServiceMethodDescriptorSupplier("routeStream"))
              .build();
        }
      }
    }
    return getRouteStreamMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static RouteServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RouteServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RouteServiceStub>() {
        @java.lang.Override
        public RouteServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RouteServiceStub(channel, callOptions);
        }
      };
    return RouteServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static RouteServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RouteServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RouteServiceBlockingStub>() {
        @java.lang.Override
        public RouteServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RouteServiceBlockingStub(channel, callOptions);
        }
      };
    return RouteServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static RouteServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<RouteServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<RouteServiceFutureStub>() {
        @java.lang.Override
        public RouteServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new RouteServiceFutureStub(channel, callOptions);
        }
      };
    return RouteServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * 双向流服务间调用接口(客户端不断向服务端写，服务端不断向客户端写)
   * </pre>
   */
  public static abstract class RouteServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public io.grpc.stub.StreamObserver<com.game.proto.CommonProto.RouteRequest> routeStream(
        io.grpc.stub.StreamObserver<com.game.proto.CommonProto.RouteResponse> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getRouteStreamMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getRouteStreamMethod(),
            io.grpc.stub.ServerCalls.asyncBidiStreamingCall(
              new MethodHandlers<
                com.game.proto.CommonProto.RouteRequest,
                com.game.proto.CommonProto.RouteResponse>(
                  this, METHODID_ROUTE_STREAM)))
          .build();
    }
  }

  /**
   * <pre>
   * 双向流服务间调用接口(客户端不断向服务端写，服务端不断向客户端写)
   * </pre>
   */
  public static final class RouteServiceStub extends io.grpc.stub.AbstractAsyncStub<RouteServiceStub> {
    private RouteServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RouteServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RouteServiceStub(channel, callOptions);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<com.game.proto.CommonProto.RouteRequest> routeStream(
        io.grpc.stub.StreamObserver<com.game.proto.CommonProto.RouteResponse> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncBidiStreamingCall(
          getChannel().newCall(getRouteStreamMethod(), getCallOptions()), responseObserver);
    }
  }

  /**
   * <pre>
   * 双向流服务间调用接口(客户端不断向服务端写，服务端不断向客户端写)
   * </pre>
   */
  public static final class RouteServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<RouteServiceBlockingStub> {
    private RouteServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RouteServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RouteServiceBlockingStub(channel, callOptions);
    }
  }

  /**
   * <pre>
   * 双向流服务间调用接口(客户端不断向服务端写，服务端不断向客户端写)
   * </pre>
   */
  public static final class RouteServiceFutureStub extends io.grpc.stub.AbstractFutureStub<RouteServiceFutureStub> {
    private RouteServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected RouteServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new RouteServiceFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_ROUTE_STREAM = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final RouteServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(RouteServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_ROUTE_STREAM:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.routeStream(
              (io.grpc.stub.StreamObserver<com.game.proto.CommonProto.RouteResponse>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class RouteServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    RouteServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.game.proto.CommonProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("RouteService");
    }
  }

  private static final class RouteServiceFileDescriptorSupplier
      extends RouteServiceBaseDescriptorSupplier {
    RouteServiceFileDescriptorSupplier() {}
  }

  private static final class RouteServiceMethodDescriptorSupplier
      extends RouteServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    RouteServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (RouteServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new RouteServiceFileDescriptorSupplier())
              .addMethod(getRouteStreamMethod())
              .build();
        }
      }
    }
    return result;
  }
}
