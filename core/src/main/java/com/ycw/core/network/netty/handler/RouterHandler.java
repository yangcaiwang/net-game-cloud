package com.ycw.core.network.netty.handler;

import com.ycw.core.network.grpc.GrpcManager;
import com.ycw.core.network.netty.message.MessageProcess;
import com.ycw.proto.CommonProto;
import io.netty.channel.Channel;

/**
 * <Grpc路由器处理器实现类>
 * <p>
 * ps: 把Netty服务器解析出来的protobuf消息，通过路由器分发到XXX服务器的控制器
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class RouterHandler implements RouterListener {

    @Override
    public CommonProto.msg process(Channel channel, CommonProto.msg msg, String serverId) {
        GrpcManager.GrpcClient grpcClient = GrpcManager.getInstance().getGrpcClient(serverId);
        if (grpcClient != null) {
            CommonProto.RouteRequest build = CommonProto.RouteRequest.newBuilder()
                    .setMsg(msg)
                    .build();
            grpcClient.sentServer(build);
        }
        return null;
    }

    @Override
    public void exec(Channel channel, CommonProto.msg msg) {
        CommonProto.msg resp = process(channel, msg, MessageProcess.getInstance().getAttr(channel, MessageProcess.SERVER_IP));
        if (resp != null) {
            MessageProcess.getInstance().sent(channel, resp);
        }
    }
}
