package com.common.module.network.netty.server;

import com.common.module.internal.heart.netty.NettyHeartbeatProcess;
import com.common.module.internal.thread.pool.actor.ActorThreadPoolExecutor;
import com.common.module.network.netty.coder.NettyPacketDecoder;
import com.common.module.network.netty.coder.NettyPacketEncoder;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <netty服务端启动类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class NettyServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ActorThreadPoolExecutor executor = new ActorThreadPoolExecutor("netty-server-thread", Runtime.getRuntime().availableProcessors() * 2);
    private NettyServerHandler nettyServerHandler = new NettyServerHandler(new NettyServerProtobufHandler(), executor);
    private NettyHeartbeatProcess nettyHeartbeatProcess;
    private ChannelFuture channelFuture;
    private static NettyServer nettyServer = new NettyServer();

    public static NettyServer getInstance() {
        return nettyServer;
    }

    public void start(String host, int port, long heartbeatTime, long heartbeatTimeout) {
        bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("HNettyBoss"));
        workerGroup = new NioEventLoopGroup(16, new DefaultThreadFactory("HNettyWorker"));
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
                            ch.pipeline().addLast(nettyServerHandler);
                        }
                    });
            channelFuture = bootstrap.bind(host, port).sync();

            // 开启心跳机制
            nettyHeartbeatProcess = new NettyHeartbeatProcess(heartbeatTime, heartbeatTimeout);
            nettyHeartbeatProcess.sent();
            nettyHeartbeatProcess.monitor();
            Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void stop() {
        try {
            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
            }

            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }

            if (channelFuture != null) {
                channelFuture.channel().close();
            }

            if (executor != null) {
                executor.shutdown();
            }

            if (nettyHeartbeatProcess != null) {
                nettyHeartbeatProcess.showdown();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
