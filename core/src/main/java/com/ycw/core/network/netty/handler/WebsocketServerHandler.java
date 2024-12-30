package com.ycw.core.network.netty.handler;

import com.game.proto.ProtocolProto;
import com.ycw.core.cluster.property.PropertyConfig;
import com.ycw.core.network.netty.enums.OfflineCause;
import com.ycw.core.network.netty.message.IMessage;
import com.ycw.core.network.netty.message.PlayerChannelManage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.StringUtils;
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
public class WebsocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger log = LoggerFactory.getLogger(WebsocketServerHandler.class);

    private final Executor handlerExecutor;

    private final RouterListener routerListener = new RouterHandler();

    public WebsocketServerHandler(Executor handlerExecutor) {
        super();
        this.handlerExecutor = handlerExecutor;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msgPacket) throws Exception {
        log.info("receive [{}]'s message [{}]", ctx, msgPacket);
        if (msgPacket instanceof IMessage) {
            IMessage msg = (IMessage) msgPacket;
            // 首包必须是cmd==0并且携带玩家id和服务器id 用于缓存attr
            if (msg.getCmd() == ProtocolProto.ProtocolCmd.FIRST_PACKET_CMD_VALUE && msg.getPlayerId() != 0 && StringUtils.isNotEmpty(msg.getServerId())) {
                PlayerChannelManage.getInstance().initChannelAttr(ctx.channel(), msg);
                return;
            }

            if (!PlayerChannelManage.getInstance().checkChannel(ctx.channel())) {
                PlayerChannelManage.getInstance().removeSession(ctx.channel());
                return;
            }

            // 心跳机制
            if (msg.getCmd() == ProtocolProto.ProtocolCmd.HEART_BEAT_CMD_VALUE) {
                PlayerChannelManage.getInstance().setAttr(ctx.channel(), PlayerChannelManage.HEART_BEAT, System.currentTimeMillis());
            }

            routerListener.process(ctx, handlerExecutor, msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        PlayerChannelManage.getInstance().removeSession(ctx.channel());
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
