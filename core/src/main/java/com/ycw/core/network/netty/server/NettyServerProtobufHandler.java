package com.ycw.core.network.netty.server;

import com.ycw.core.network.grpc.GrpcManager;
import com.ycw.core.network.netty.common.IClient;
import com.ycw.core.network.netty.listener.RouteMessageListener;
import com.ycw.core.network.netty.message.MessageProcess;
import com.ycw.proto.CommonProto;
import io.netty.channel.Channel;

/**
 * <netty服务端协议处理器实现类>
 * <p>
 * ps: 使用protobuf协议的默认业务处理器实现，此处仅仅做消息分发对应服务器
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class NettyServerProtobufHandler implements RouteMessageListener {

    @Override
    public CommonProto.msg route(Channel channel, CommonProto.msg req, String serverId) {
        GrpcManager.GrpcClient grpcClient = GrpcManager.getInstance().getGrpcClient(serverId);
        if (grpcClient != null) {
            CommonProto.RouteRequest build = CommonProto.RouteRequest.newBuilder()
                    .setMsg(req)
                    .build();
            grpcClient.sentServer(build);
        }
        return null;
    }

    @Override
    public void exec(Channel channel, CommonProto.msg req) throws Exception {
        CommonProto.msg resp = route(channel, req, MessageProcess.getInstance().getAttr(channel, MessageProcess.SERVER_IP));
        if (resp != null) {
            MessageProcess.getInstance().sent(channel, resp);
        }
    }

    @Override
    public void onChannelClose(Channel channel, IClient.OfflineCause offlineCause) {
        MessageProcess.getInstance().kitOut(channel, offlineCause);
    }
}
