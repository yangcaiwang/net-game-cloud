package com.common.module.network.netty.client;

import com.common.module.network.netty.coder.NettyPacketDecoder;
import com.common.module.network.netty.coder.NettyPacketEncoder;
import com.common.module.network.netty.message.MsgManager;
import com.game.proto.CommonProto;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClient {

    private static final Logger log = LoggerFactory.getLogger(NettyClient.class);
    private static Channel channel = null;

    public static void doConnect(String host, int port) throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    //处理websocket请求的编解码器
                    ch.pipeline().addLast(new IdleStateHandler(7200, 0, 0));
                    ch.pipeline().addLast("decoder", new NettyPacketDecoder());
                    ch.pipeline().addLast("encoder", new NettyPacketEncoder());
                    ch.pipeline().addLast("handler", new NettyClientHandler());
                }
            });
            ChannelFuture f = b.connect(host, port).sync();
            channel = f.channel();
            log.info("======================= [] websocket client started ip:{} port:{} =======================", host, port);
            // 首包
            NettyClient.sent(MsgManager.buildMsg(0, 1, "game-1001", CommonProto.msg.newBuilder().build()));

            // TODO 后端模仿客户端自测接口
//            NettyClient.sent(MsgManager.buildMsg(1, CommonProto.msg.newBuilder().setAny(MsgManager.messageToAny(CommonProto.MiniItem.newBuilder().build())).build()));

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public static void sent(CommonProto.msg msg) {
        channel.writeAndFlush(msg);
    }
}
