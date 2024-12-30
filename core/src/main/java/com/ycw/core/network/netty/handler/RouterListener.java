package com.ycw.core.network.netty.handler;

import com.ycw.core.internal.thread.task.linked.AbstractLinkedTask;
import com.ycw.core.network.netty.message.MessageProcess;
import com.ycw.proto.CommonProto;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.Executor;

/**
 * <路由器异步监听器接口>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public interface RouterListener {

    /**
     * 处理路由消息
     *
     * @param ctx      网络链接句柄
     * @param executor 线程池
     * @param msg      消息
     */
    default void handle(ChannelHandlerContext ctx, Executor executor, CommonProto.msg msg) {
        RouterListener listener = this;
        try {
            executor.execute(new AbstractLinkedTask() {
                @Override
                protected void exec() throws Exception {
                    listener.exec(ctx.channel(), msg);
                }

                @Override
                public Object getIdentity() {
                    Object attr = msg.getPlayerId();
                    if ((long) attr <= 0L) {
                        attr = ctx.channel().id().asLongText();
                    }
                    return attr;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 路由器异步链式处理
     *
     * @param channel 玩家连接的管道
     * @param msg     消息体
     */
    void exec(Channel channel, CommonProto.msg msg);

    /**
     * 路由消息
     *
     * @param channel  玩家通道
     * @param msg      消息对象
     * @param serverId 服务器id
     * @return CommonProto.msg {@link CommonProto.msg}
     */
    CommonProto.msg process(Channel channel, CommonProto.msg msg, String serverId);

    default void onChannelClose(Channel channel, NettyConstant.OfflineCause offlineCause) {
        MessageProcess.getInstance().kitOut(channel, offlineCause);
    }
}
