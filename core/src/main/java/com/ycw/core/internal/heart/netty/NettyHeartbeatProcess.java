package com.ycw.core.internal.heart.netty;

import com.game.proto.ProtocolProto;
import com.ycw.core.internal.heart.HeartbeatProcess;
import com.ycw.core.internal.thread.pool.actor.TimerActorThread;
import com.ycw.core.network.netty.enums.OfflineCause;
import com.ycw.core.network.netty.message.IMessage;
import com.ycw.core.network.netty.message.PlayerChannelManage;
import com.ycw.core.network.netty.message.ProtoMessage;
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
                Map<Long, Channel> channelMap = PlayerChannelManage.getInstance().getChannelMap();
                if (MapUtils.isNotEmpty(channelMap)) {
                    for (Channel channel : channelMap.values()) {
                        IMessage iMessage = new ProtoMessage();
                        iMessage.buildIMessage(ProtocolProto.ProtocolCmd.HEART_BEAT_CMD_VALUE);
                        PlayerChannelManage.getInstance().sent(channel, iMessage);
                    }
                }
            });
        }
    }

    @Override
    public void monitor() {
        if (heartbeatTimeout > 0) {
            threadMonitor = new TimerActorThread("nettyHeartbeat-threadMonitor", heartbeatTimeout, () -> {
                PlayerChannelManage playerChannelManage = PlayerChannelManage.getInstance();
                Map<Long, Channel> channelMap = playerChannelManage.getChannelMap();
                if (MapUtils.isNotEmpty(channelMap)) {
                    for (Channel channel : channelMap.values()) {
                        long lastHeartBeatTime = playerChannelManage.getAttr(channel, PlayerChannelManage.HEART_BEAT);
                        if (isTimeOut(lastHeartBeatTime)) {
                            PlayerChannelManage.getInstance().kitOut(channel, OfflineCause.HEARTBEAT_TIMEOUT);
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
