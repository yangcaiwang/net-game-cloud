package com.ycw.core.network.netty.handler;

import com.ycw.core.internal.thread.task.linked.AbstractLinkedTask;
import com.ycw.core.network.netty.enums.OfflineCause;
import com.ycw.core.network.netty.message.IMessage;
import com.ycw.core.network.netty.message.SocketChannelManage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.Executor;

import static com.ycw.core.network.netty.message.SocketChannelManage.PLAYER_ID;

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
    default void process(ChannelHandlerContext ctx, Executor executor, IMessage msg) {
        RouterListener listener = this;
        try {
            executor.execute(new AbstractLinkedTask() {
                @Override
                protected void exec() {
                    msg.setPlayerId(SocketChannelManage.getInstance().getAttr(ctx.channel(), PLAYER_ID));
                    listener.exec(ctx.channel(), msg);
                }

                @Override
                public Object getIdentity() {
                    return msg.getPlayerId();
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
    void exec(Channel channel, IMessage msg);

    default void onChannelClose(Channel channel, OfflineCause offlineCause) {
        SocketChannelManage.getInstance().kitOut(channel, offlineCause);
    }
}
