package com.ycw.core.network.netty.socket;

import com.ycw.core.internal.heart.netty.NettyHeartbeatProcess;
import com.ycw.core.internal.thread.pool.actor.ActorThreadPoolExecutor;
import com.ycw.core.network.netty.handler.SocketServerHandler;
import com.ycw.core.network.netty.message.MessageDecoder;
import com.ycw.core.network.netty.message.MessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
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
public class SocketServer {
    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ActorThreadPoolExecutor executor = new ActorThreadPoolExecutor("netty-server-thread", Runtime.getRuntime().availableProcessors() * 2);
    private SocketServerHandler socketServerHandler = new SocketServerHandler(executor);
    private NettyHeartbeatProcess nettyHeartbeatProcess;
    private ChannelFuture channelFuture;
    private static SocketServer socketServer = new SocketServer();

    public static SocketServer getInstance() {
        return socketServer;
    }

    public void start(String host, int port, long heartbeatTime, long heartbeatTimeout) throws Exception {
        bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("HNettyBoss"));
        workerGroup = new NioEventLoopGroup(16, new DefaultThreadFactory("HNettyWorker"));

        try {
            // 服务端辅助启动类，用以降低服务端的开发复杂度
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    // 实例化ServerSocketChannel
                    .channel(NioServerSocketChannel.class).
                    // 设置ServerSocketChannel的TCP参数
                            option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT).
                    option(ChannelOption.SO_BACKLOG, 1024).
                    option(ChannelOption.SO_REUSEADDR, true).
                    childOption(ChannelOption.TCP_NODELAY, false).
                    childOption(ChannelOption.SO_KEEPALIVE, true).
                    childOption(ChannelOption.SO_REUSEADDR, true).
                    childOption(ChannelOption.SO_LINGER, 0).
                    childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
//                            pipeline.addLast(new HttpServerCodec());
//                            pipeline.addLast("chunked-handler", new ChunkedWriteHandler());
//                            pipeline.addLast(new HttpObjectAggregator(65535));
//                            pipeline.addLast(new WebSocketServerProtocolHandler("/ycw", null, true));
                            pipeline.addLast(new IdleStateHandler(7200, -1, -1));
                            // handler
                            ch.pipeline().addLast("decoder", new MessageDecoder());
                            ch.pipeline().addLast("encoder", new MessageEncoder());
                            ch.pipeline().addLast(socketServerHandler);
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
