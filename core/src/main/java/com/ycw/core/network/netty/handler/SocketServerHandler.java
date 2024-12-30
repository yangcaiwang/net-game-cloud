package com.ycw.core.network.netty.handler;

import com.game.proto.ProtocolProto;
import com.ycw.core.cluster.property.PropertyConfig;
import com.ycw.core.network.netty.enums.OfflineCause;
import com.ycw.core.network.netty.message.IMessage;
import com.ycw.core.network.netty.message.SocketChannelManage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executor;

/**
 * <netty服务端处理器实现类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
@ChannelHandler.Sharable
public class SocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger log = LoggerFactory.getLogger(SocketServerHandler.class);

    private final Executor handlerExecutor;

    private final RouterListener routerListener = new RouterHandler();

    public SocketServerHandler(Executor handlerExecutor) {
        super();
        this.handlerExecutor = handlerExecutor;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msgPacket) {
        try {

            log.info("receive [{}]'s message [{}]", ctx, msgPacket);
            if (msgPacket instanceof IMessage) {
                IMessage msg = (IMessage) msgPacket;
                // 首包必须是cmd==1001001并且携带玩家id和服务器id 用于绑定玩家channel通道
                if (msg.getCmd() == ProtocolProto.ProtocolCmd.FIRST_REQ_VALUE) {
                    SocketChannelManage.getInstance().bindChannel(ctx.channel(), msg);
                    return;
                }

                if (!SocketChannelManage.getInstance().checkBindChannel(ctx.channel())) {
                    SocketChannelManage.getInstance().removeChannel(ctx.channel());
                    return;
                }

                // 心跳机制
                if (msg.getCmd() == ProtocolProto.ProtocolCmd.HEART_BEAT_REQ_VALUE) {
                    SocketChannelManage.getInstance().setAttr(ctx.channel(), SocketChannelManage.HEART_BEAT, System.currentTimeMillis());
                    SocketChannelManage.getInstance().sent(ctx.channel(), ProtocolProto.ProtocolCmd.HEART_BEAT_RESP_VALUE, null);
                }

                routerListener.process(ctx, handlerExecutor, msg);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        SocketChannelManage.getInstance().removeChannel(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        boolean ioClose = cause instanceof IOException;
        if (!ioClose) {
            log.error(cause.getMessage(), cause);
        }

        if (PropertyConfig.isDebug()) {
            log.error(cause.getMessage(), cause);
        }

        this.routerListener.onChannelClose(ctx.channel(), ioClose ? OfflineCause.MANUAL : OfflineCause.EXCEPTION);
    }
}
