package com.ycw.core.network.netty;

import com.ycw.core.network.netty.handler.WebsocketClientHandler;
import com.ycw.core.network.netty.message.MessageDecoder;
import com.ycw.core.network.netty.message.MessageEncoder;
import com.ycw.core.network.netty.message.MessageProcess;
import com.ycw.proto.CommonProto;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <netty客户端启动类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class WebsocketClient {
    private static final Logger log = LoggerFactory.getLogger(WebsocketClient.class);
    private EventLoopGroup workerGroup = null;
    private ChannelFuture channelFuture = null;
    private static WebsocketClient websocketClient = new WebsocketClient();

    private static WebsocketClient getInstance() {
        return websocketClient;
    }

    public void start(String host, int port) throws Exception {
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
                    ch.pipeline().addLast("decoder", new MessageDecoder());
                    ch.pipeline().addLast("encoder", new MessageEncoder());
                    ch.pipeline().addLast("handler", new WebsocketClientHandler());
                }
            });
            channelFuture = b.connect(host, port).sync();
            Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
            log.info("======================= [] websocket client started ip:{} port:{} =======================", host, port);
            // 首包
            sent(MessageProcess.getInstance().buildMsg(0, 1, "game-1001", CommonProto.msg.newBuilder().build()));

            // TODO 后端模仿客户端自测接口
//            NettyClient.sent(MsgManager.buildMsg(1, CommonProto.msg.newBuilder().setAny(MsgManager.messageToAny(CommonProto.MiniItem.newBuilder().build())).build()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void stop() {
        try {
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }

            if (channelFuture != null) {
                channelFuture.channel().close();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void sent(CommonProto.msg msg) {
        channelFuture.channel().writeAndFlush(msg);
    }
}
