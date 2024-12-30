package com.ycw.core.internal.heart.netty;

import com.game.proto.ProtocolProto;
import com.ycw.core.internal.heart.HeartbeatProcess;
import com.ycw.core.internal.thread.pool.actor.TimerActorThread;
import com.ycw.core.network.netty.enums.OfflineCause;
import com.ycw.core.network.netty.message.IMessage;
import com.ycw.core.network.netty.message.SocketChannelManage;
import com.ycw.core.network.netty.message.SocketMessage;
import io.netty.channel.Channel;
import org.apache.commons.collections.MapUtils;

import java.util.Map;

/**
 * <netty心跳机制处理器实现类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class NettyHeartbeatProcess implements HeartbeatProcess {
    private long heartbeatTime;
    private long heartbeatTimeout;
    private TimerActorThread threadSender;
    private TimerActorThread threadMonitor;

    public NettyHeartbeatProcess(long heartbeatTime, long heartbeatTimeout) {
        this.heartbeatTime = heartbeatTime;
        this.heartbeatTimeout = heartbeatTimeout;
    }

    @Override
    public void sent() {
        if (heartbeatTime > 0) {
            threadSender = new TimerActorThread("nettyHeartbeat-threadSender", heartbeatTime, () -> {
                Map<Long, Channel> channelMap = SocketChannelManage.getInstance().getChannelMap();
                if (MapUtils.isNotEmpty(channelMap)) {
                    for (Channel channel : channelMap.values()) {
                        IMessage iMessage = new SocketMessage();
                        iMessage.buildIMessage(ProtocolProto.ProtocolCmd.HEART_BEAT_REQ_VALUE);
                        SocketChannelManage.getInstance().sent(channel, iMessage);
                    }
                }
            });
        }
    }

    @Override
    public void monitor() {
        if (heartbeatTimeout > 0) {
            threadMonitor = new TimerActorThread("nettyHeartbeat-threadMonitor", heartbeatTimeout, () -> {
                SocketChannelManage socketChannelManage = SocketChannelManage.getInstance();
                Map<Long, Channel> channelMap = socketChannelManage.getChannelMap();
                if (MapUtils.isNotEmpty(channelMap)) {
                    for (Channel channel : channelMap.values()) {
                        long lastHeartBeatTime = socketChannelManage.getAttr(channel, SocketChannelManage.HEART_BEAT);
                        if (isTimeOut(lastHeartBeatTime)) {
                            SocketChannelManage.getInstance().kitOut(channel, OfflineCause.HEARTBEAT_TIMEOUT);
                        }
                    }
                }
            });
        }
    }

    @Override
    public boolean isTimeOut(long lastHeartbeatTime) {
        return System.currentTimeMillis() - lastHeartbeatTime >= heartbeatTimeout;
    }

    @Override
    public void showdown() {
        try {
            if (threadSender != null) {
                threadSender.shutdown();
            }
            if (threadMonitor != null) {
                threadMonitor.shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
