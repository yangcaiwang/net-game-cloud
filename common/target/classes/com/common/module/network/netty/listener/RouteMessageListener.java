package com.common.module.network.netty.listener;

import com.common.module.internal.thread.task.linked.AbstractLinkedTask;
import com.game.proto.CommonProto;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.Executor;

public interface RouteMessageListener extends MessageListener {

    @Override
    default void handle(ChannelHandlerContext ctx, Executor executor, CommonProto.msg req) throws Exception {
        RouteMessageListener listener = this;

        executor.execute(new AbstractLinkedTask() {
            @Override
            protected void exec() throws Exception {
                listener.exec(ctx.channel(), req);
            }

            @Override
            public Object getIdentity() {
                Object attr = req.getPlayerId();
                if ((long) attr <= 0L) {
                    attr = ctx.channel().id().asLongText();
                }
                return attr;
            }
        });
    }

    /**
     * 异步链式处理websocket请求
     *
     * @param channel 玩家连接的管道
     * @param req     消息体
     */
    void exec(Channel channel, CommonProto.msg req) throws Exception;

    CommonProto.msg route(Channel channel, CommonProto.msg req, String serverId);

}
