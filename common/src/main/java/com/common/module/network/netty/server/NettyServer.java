package com.common.module.network.netty.server;

import com.common.module.network.netty.coder.NettyPacketDecoder;
import com.common.module.network.netty.coder.NettyPacketEncoder;
import com.common.module.internal.thread.pool.actor.ActorThreadPoolExecutor;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;

public class NettyServer {
    public static void bind(String host, int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("HNettyBoss"));
        EventLoopGroup workerGroup = new NioEventLoopGroup(16, new DefaultThreadFactory("HNettyWorker"));
        try {
            // 服务端辅助启动类，用以降低服务端的开发复杂度
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    // 实例化ServerSocketChannel
                    .channel(NioServerSocketChannel.class)
                    // 设置ServerSocketChannel的TCP参数
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // handler
                            ch.pipeline().addLast(new IdleStateHandler(7200, 0, 0));
                            ch.pipeline().addLast("decoder", new NettyPacketDecoder());
                            ch.pipeline().addLast("encoder", new NettyPacketEncoder());
                            ch.pipeline().addLast(new NettyServerHandler(new NettyServerProtobufHandler(), new ActorThreadPoolExecutor("websocket-server-thread", Runtime.getRuntime().availableProcessors() * 2)));
                        }
                    });
            ChannelFuture future = bootstrap.bind(host, port).sync();
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
