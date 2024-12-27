package com.ycw.core.network.netty.server;

import com.ycw.core.cluster.property.PropertyConfig;
import com.ycw.core.network.netty.common.IClient;
import com.ycw.core.network.netty.listener.MessageSuperListener;
import com.ycw.core.network.netty.message.MessageProcess;
import com.ycw.proto.CommonProto;
import com.ycw.proto.ProtocolProto;
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
public class NettyServerHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger log = LoggerFactory.getLogger(NettyServerHandler.class);

    private final Executor handlerExecutor;
    private final MessageSuperListener listener;

    public NettyServerHandler(MessageSuperListener listener, Executor handlerExecutor) {
        super();
        this.handlerExecutor = handlerExecutor;
        this.listener = listener;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msgPacket) throws Exception {
        log.info("receive [{}]'s message [{}]", ctx, msgPacket);
        if (msgPacket instanceof CommonProto.msg) {
            CommonProto.msg msg = (CommonProto.msg) msgPacket;
            // 首包必须是cmd==0并且携带玩家id和服务器id 用于缓存attr
            if (msg.getCmd() == ProtocolProto.ProtocolCmd.FIRST_PACKET_CMD_VALUE && msg.getPlayerId() != 0 && StringUtils.isNotEmpty(msg.getServerId())) {
                MessageProcess.getInstance().initChannelAttr(ctx.channel(), msg);
                return;
            }

            if (!MessageProcess.getInstance().checkChannel(ctx.channel())) {
                MessageProcess.getInstance().removeSession(ctx.channel());
                return;
            }

            // 心跳机制
            if (msg.getCmd() == ProtocolProto.ProtocolCmd.HEART_BEAT_CMD_VALUE) {
                MessageProcess.getInstance().setAttr(ctx.channel(), MessageProcess.HEART_BEAT, System.currentTimeMillis());
            }

            listener.handle(ctx, handlerExecutor, msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        MessageProcess.getInstance().removeSession(ctx.channel());
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

        this.listener.onChannelClose(ctx.channel(), ioClose ? IClient.OfflineCause.MANUAL : IClient.OfflineCause.EXCEPTION);
    }
}
