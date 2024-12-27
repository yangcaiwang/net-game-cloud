package com.ycw.core.internal.heart.netty;

import com.ycw.core.internal.heart.HeartbeatProcess;
import com.ycw.core.internal.thread.pool.actor.TimerActorThread;
import com.ycw.core.network.netty.common.IClient;
import com.ycw.core.network.netty.message.MessageProcess;
import com.ycw.proto.CommonProto;
import com.ycw.proto.ProtocolProto;
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
        threadSender = new TimerActorThread("nettyHeartbeat-threadSender", heartbeatTime, () -> {
            Map<Long, Channel> channelMap = MessageProcess.getInstance().getChannelMap();
            if (MapUtils.isNotEmpty(channelMap)) {
                for (Channel channel : channelMap.values()) {
                    MessageProcess.getInstance().sent(channel, CommonProto.msg.newBuilder().setCmd(ProtocolProto.ProtocolCmd.HEART_BEAT_CMD_VALUE).build());
                }
            }
        });
    }

    @Override
    public void monitor() {
        threadMonitor = new TimerActorThread("nettyHeartbeat-threadMonitor", heartbeatTime, () -> {
            MessageProcess messageProcess = MessageProcess.getInstance();
            Map<Long, Channel> channelMap = messageProcess.getChannelMap();
            if (MapUtils.isNotEmpty(channelMap)) {
                for (Channel channel : channelMap.values()) {
                    long lastHeartBeatTime = messageProcess.getAttr(channel, MessageProcess.HEART_BEAT);
                    if (isTimeOut(lastHeartBeatTime)) {
                        MessageProcess.getInstance().kitOut(channel, IClient.OfflineCause.HEARTBEAT_TIMEOUT);
                    }
                }
            }
        });
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
