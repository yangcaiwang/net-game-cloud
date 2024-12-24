package com.common.module.network.netty.server;

import com.common.module.network.grpc.GrpcManager;
import com.common.module.network.netty.common.IClient;
import com.common.module.network.netty.listener.RouteMessageListener;
import com.common.module.network.netty.message.MsgManager;
import com.game.proto.CommonProto;
import io.netty.channel.Channel;

/**
 * 使用protobuf协议的默认业务处理器实现，此处仅仅做消息分发对应服务器
 */
public class NettyServerProtobufHandler implements RouteMessageListener {

    @Override
    public CommonProto.msg route(Channel channel, CommonProto.msg req, String serverId){
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
        CommonProto.msg resp = route(channel, req, MsgManager.getAttr(channel, MsgManager.SERVER_IP));
        if (resp != null) {
            MsgManager.sent(channel, resp);
        }
    }

    @Override
    public void onChannelClose(Channel channel, IClient.OfflineCause offlineCause) {
        MsgManager.kitOut(channel, offlineCause);
    }
}
