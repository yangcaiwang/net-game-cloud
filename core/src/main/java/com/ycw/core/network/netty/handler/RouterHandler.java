package com.ycw.core.network.netty.handler;

import com.ycw.core.internal.thread.pool.actor.ActorThreadPoolExecutor;
import com.ycw.core.network.grpc.GrpcManage;
import com.ycw.core.network.netty.message.IMessage;
import com.ycw.core.network.netty.message.SocketChannelManage;
import com.ycw.core.network.netty.message.SocketMessage;
import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <Grpc路由器处理器实现类>
 * <p>
 * ps: 把Netty服务器解析出来的protobuf消息，通过路由器分发到XXX服务器的控制器
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class RouterHandler implements RouterListener {
    private static final Logger log = LoggerFactory.getLogger(ControllerHandler.class);
    public static final ActorThreadPoolExecutor actorExecutor = new ActorThreadPoolExecutor("route-message-thread", Runtime.getRuntime().availableProcessors() * 2 + 1);

    @Override
    public void exec(Channel channel, IMessage msg) {
        String serverId = SocketChannelManage.getInstance().getAttr(channel, SocketChannelManage.SERVER_IP);
        if (StringUtils.isNotEmpty(serverId) && GrpcManage.getInstance().getGrpcClient(serverId) != null) {
            if (msg instanceof SocketMessage) {
                GrpcManage.getInstance().sentController((SocketMessage) msg);
            }
        } else {
            log.error("serverId 有误！");
        }
    }
}
