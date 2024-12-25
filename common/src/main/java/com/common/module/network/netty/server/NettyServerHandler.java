package com.common.module.network.netty.server;

import com.common.module.network.netty.common.IClient;
import com.common.module.network.netty.listener.MessageSuperListener;
import com.common.module.network.netty.message.MsgManager;
import com.common.module.cluster.property.PropertyConfig;
import com.game.proto.CommonProto;
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
            long playerId = msg.getPlayerId();
            String serverId = msg.getServerId();
            // 首包必须是cmd==0并且携带玩家id和服务器id 用于缓存attr
            if (msg.getCmd() == 0 && playerId != 0 && StringUtils.isNotEmpty(serverId)) {
                MsgManager.setAttr(ctx.channel(), MsgManager.SERVER_IP, msg.getServerId());
                MsgManager.setAttr(ctx.channel(), MsgManager.PLAYER_ID, playerId);
                MsgManager.channelMap.putIfAbsent(playerId, ctx.channel());
                MsgManager.channelMap.putIfAbsent(playerId, ctx.channel());
                MsgManager.sent(playerId, msg.getCmd(), null);
                listener.handle(ctx, handlerExecutor, msg);
                return;
            }

            if (!MsgManager.checkChannel(ctx.channel())) {
                MsgManager.removeSession(ctx.channel());
                return;
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
        MsgManager.removeSession(ctx.channel());
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
