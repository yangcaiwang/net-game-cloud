package com.common.module.network.netty.listener;

import com.common.module.network.netty.common.IClient;
import com.game.proto.CommonProto;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.Executor;

/**
 * netty 消息异步监听器
 */
public interface MessageListener {

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
