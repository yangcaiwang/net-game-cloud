package com.ycw.core.network.netty.listener;

import com.ycw.core.network.netty.common.IClient;
import com.ycw.proto.CommonProto;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.Executor;

/**
 * <netty消息异步监听器接口>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public interface MessageSuperListener {

    /**
     * 处理消息
     *
     * @param ctx      网络链接句柄
     * @param executor 线程池
     * @param req      小写内容
     */
    void handle(ChannelHandlerContext ctx, Executor executor, CommonProto.msg req) throws Exception;

    default void onConnectSuc(Channel channel, CommonProto.msg msg) throws Exception {}

    default void onChannelClose(Channel channel, IClient.OfflineCause offlineCause){}
}
