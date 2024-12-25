package com.common.module.network.netty.listener;

import com.common.module.internal.thread.task.linked.AbstractLinkedTask;
import com.game.proto.CommonProto;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.Executor;

/**
 * <netty路由消息异步监听器接口>
 * <p>
 * ps: 网关路由游戏服
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public interface RouteMessageListener extends MessageSuperListener {

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

    /**
     * 网关路由游戏服
     *
     * @param channel  玩家通道
     * @param req      消息对象
     * @param serverId 服务器id
     * @return CommonProto.msg {@link CommonProto.msg}
     */
    CommonProto.msg route(Channel channel, CommonProto.msg req, String serverId);

}
