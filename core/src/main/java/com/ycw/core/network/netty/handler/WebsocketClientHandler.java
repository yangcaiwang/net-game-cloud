package com.ycw.core.network.netty.handler;

import com.ycw.proto.CommonProto;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <netty客户端处理器实现类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
@ChannelHandler.Sharable
public class WebsocketClientHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger log = LoggerFactory.getLogger(WebsocketClientHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msgPacket) throws Exception {
        log.info("receive [{}]'s message [{}]", ctx, msgPacket);
        if (msgPacket instanceof CommonProto.msg) {
            CommonProto.msg msg = (CommonProto.msg) msgPacket;
            System.out.println(msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ctx.close();
    }
}
