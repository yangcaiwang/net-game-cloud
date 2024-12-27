package com.ycw.core.network.netty.message;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Message;
import com.ycw.core.cluster.property.PropertyConfig;
import com.ycw.core.internal.thread.pool.actor.ActorThreadPoolExecutor;
import com.ycw.core.internal.thread.task.linked.AbstractLinkedTask;
import com.ycw.core.network.netty.common.IClient;
import com.ycw.proto.CommonProto;
import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <netty消息处理器实现类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class MessageProcess {
    private static final Logger log = LoggerFactory.getLogger(MessageProcess.class);
    public static final String PUBLIC_KEY = "publicKey";
    public static final String PRIVATE_KEY = "privateKey";
    public static final String PLAYER_ID = "playerId";
    public static final String SERVER_IP = "serverId";
    public static final String HEART_BEAT = "heartBeat";
    public static final String OFFLINE_CAUSE_OFF = "offlineCause";
    public static final Executor handlerExecutor = new ActorThreadPoolExecutor("io-sent", Runtime.getRuntime().availableProcessors() + 1);

    private static MessageProcess messageProcess = new MessageProcess();

    public static MessageProcess getInstance() {
        return messageProcess;
    }

    /**
     * 玩家和所属服务器映射
     */
    private Map<Long, Channel> channelMap = new ConcurrentHashMap<>();

    /**
     * 构建消息体
     *
     * @param cmd      报文号
     * @param playerId 玩家id
     * @param message  具体proto消息体
     * @return msg {@link CommonProto.msg}
     */
    public CommonProto.msg buildMsg(int cmd, long playerId, Message message) {
        CommonProto.msg.Builder builder = CommonProto.msg.newBuilder();
        builder.setCmd(cmd);
        builder.setPlayerId(playerId);
        if (message != null) {
            builder.setAny(messageToAny(message));
            builder.setData(ByteString.copyFrom(message.toByteArray()));
        }

        return builder.build();
    }

    /**
     * 构建消息体
     *
     * @param cmd     报文号
     * @param message 具体proto消息体
     * @return msg {@link CommonProto.msg}
     */
    public CommonProto.msg buildMsg(int cmd, Message message) {
        CommonProto.msg.Builder builder = CommonProto.msg.newBuilder();
        builder.setCmd(cmd);
        if (message != null) {
            builder.setAny(messageToAny(message));
            builder.setData(ByteString.copyFrom(message.toByteArray()));
        }
        return builder.build();
    }

    /**
     * 构建消息体
     *
     * @param cmd      报文号
     * @param playerId 玩家id
     * @param serverId 服务器id
     * @param message  具体proto消息体
     * @return msg {@link CommonProto.msg}
     */
    public CommonProto.msg buildMsg(int cmd, long playerId, String serverId, Message message) {
        CommonProto.msg.Builder builder = CommonProto.msg.newBuilder();
        builder.setCmd(cmd);
        builder.setPlayerId(playerId);
        builder.setServerId(serverId);
        if (message != null) {
            builder.setAny(messageToAny(message));
            builder.setData(ByteString.copyFrom(message.toByteArray()));
        }

        return builder.build();
    }

    /**
     * 构建消息体
     *
     * @param cmd      报文号
     * @param playerId 玩家id
     * @param serverId 服务器id
     * @param bytes    字节数据
     * @return msg {@link CommonProto.msg}
     */
    public CommonProto.msg buildMsg(int cmd, long playerId, String serverId, byte[] bytes) {
        CommonProto.msg.Builder builder = CommonProto.msg.newBuilder();
        builder.setCmd(cmd);
        builder.setPlayerId(playerId);
        builder.setPlayerId(playerId);
        builder.setServerId(serverId);
        builder.setData(ByteString.copyFrom(bytes));
        return builder.build();
    }

    /**
     * Message转为为Any
     *
     * @param message 具体proto消息体
     * @return Any {@link Any}
     */
    public Any messageToAny(Message message) {
        return Any.newBuilder()
                .setTypeUrl("type.googleapis.com/" + message.getDescriptorForType().getFullName())
                .setValue(ByteString.copyFrom(message.toByteArray()))
                .build();
    }

    /**
     * Any转化为Message
     *
     * @param any     Any 类型是一个通用的消息类型，可以用来包装任意类型的消息
     * @param message 具体proto消息体
     * @return Message {@link Message}
     */
    public Message anyToMessage(Any any, Message message) throws IOException {
        // 检查类型 URL 是否匹配
        if (any.getTypeUrl().equals("type.googleapis.com/" + message.getDescriptorForType().getFullName())) {
            // 反序列化 Any 中的 ByteString 为 Message
            return DynamicMessage.parseFrom(message.getDescriptorForType(), any.getValue().newInput());
        }

        return null;
    }

    /**
     * 发送消息
     *
     * @param channel 玩家的管道
     * @param cmd     报文号
     * @param message 具体proto消息体
     */
    public void sent(Channel channel, int cmd, Message message) {
        CommonProto.msg msg = buildMsg(getAttr(channel, MessageProcess.PLAYER_ID), cmd, message);
        sent(channel, msg);
    }

    /**
     * 发送消息
     *
     * @param playerId 玩家id
     * @param cmd      报文号
     * @param message  具体proto消息体
     */
    public void sent(long playerId, int cmd, Message message) {
        Channel channel = channelMap.get(playerId);
        if (channel != null) {
            CommonProto.msg msg = buildMsg(cmd, playerId, message);
            sent(channel, msg);
        }
    }

    /**
     * 发送消息
     *
     * @param channel 玩家连接的管道
     * @param msg     玩家id
     */
    public void sent(Channel channel, CommonProto.msg msg) {
        if (msg.getCmd() <= 1) {
            channel.writeAndFlush(msg);
            return;
        }
        boolean debug = PropertyConfig.isDebug() && PropertyConfig.getBoolean("proto.message.print", true);
        if (debug) {
            log.info("SENT:{}", msg);
        }

        handlerExecutor.execute(new AbstractLinkedTask() {
            @Override
            protected void exec() throws Exception {
                channel.writeAndFlush(msg);
            }

            @Override
            public Object getIdentity() {
                return msg.getPlayerId();
            }
        });
    }

    /**
     * Channel作用域中添加k，v
     *
     * @param channel 玩家连接的管道
     * @param key     键
     * @param value   值
     */
    public <T> void setAttr(Channel channel, String key, T value) {
        Attribute<T> attribute = channel.attr(AttributeKey.valueOf(key));
        attribute.set(value);
    }

    /**
     * 从Channel作用域中根据k获取v
     *
     * @param channel 玩家连接的管道
     * @param key     键
     * @return T {@link T}
     */
    public <T> T getAttr(Channel channel, String key) {
        Attribute<T> attribute = channel.attr(AttributeKey.valueOf(key));
        return attribute.get();
    }

    /**
     * 踢玩家下线
     *
     * @param cause    下线原因
     * @param playerId 玩家id
     * @param cmd      报文号
     * @param message  proto消息体
     */
    public void kitOut(IClient.OfflineCause cause, long playerId, int cmd, Message message) {
        Channel channel = channelMap.get(playerId);
        if (channel != null) {
            // 设置离线原因
            setAttr(channel, OFFLINE_CAUSE_OFF, cause);
            sent(channel, cmd, message);
            channel.close();
        }
    }

    /**
     * 踢玩家下线
     *
     * @param channel 玩家连接的管道
     * @param cause   下线原因
     */
    public void kitOut(Channel channel, IClient.OfflineCause cause) {
        // 返回离线原因
        setAttr(channel, OFFLINE_CAUSE_OFF, cause);
        sent(channel, IClient.OffLineCmd.NORMAL.value, null);
        channel.close();
    }

    /**
     * 把所有玩家踢下线
     */
    public int kitOutAll() {
        AtomicInteger players = new AtomicInteger();
        if (MapUtils.isNotEmpty(channelMap)) {
            for (Channel channel : channelMap.values()) {
                kitOut(channel, IClient.OfflineCause.STOP_SERVER);
                players.decrementAndGet();
            }
        }
        return players.get();
    }

    /**
     * 获取所有服在线玩家id集合
     *
     * @return List {@link List}
     */
    public List<Long> getAllOnline() {
        return new ArrayList<>(channelMap.keySet());
    }

    /**
     * 获取某服在线玩家id集合
     *
     * @param serverId 服务器id
     * @return List {@link List}
     */
    public List<Long> getOnlineByServerId(String serverId) {
        return channelMap.entrySet()
                .stream()
                .filter(entry -> getAttr(entry.getValue(), MessageProcess.SERVER_IP).equals(serverId))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public void initChannelAttr(Channel channel, CommonProto.msg msg) {
        long playerId = msg.getPlayerId();
        setAttr(channel, MessageProcess.SERVER_IP, msg.getServerId());
        setAttr(channel, MessageProcess.PLAYER_ID, playerId);
        setAttr(channel, MessageProcess.HEART_BEAT, System.currentTimeMillis());
        channelMap.putIfAbsent(playerId, channel);
    }

    /**
     * 检查玩家通道作用域，是否发了首包
     *
     * @param channel 玩家通道
     * @return boolean {@link Boolean}
     */
    public boolean checkChannel(Channel channel) {
        return channel.hasAttr(AttributeKey.valueOf(MessageProcess.SERVER_IP)) && channel.hasAttr(AttributeKey.valueOf(MessageProcess.PLAYER_ID));
    }

    /**
     * 统计所有服在线人数量
     *
     * @return List {@link List}
     */
    public int onlineSize() {
        return getAllOnline().size();
    }

    /**
     * 获取某服的在线玩家数量
     *
     * @param serverId 服务器id
     * @return Integer {@link Integer}
     */
    public int onlineSizeByServerId(String serverId) {
        return getOnlineByServerId(serverId).size();
    }

    /**
     * 玩家主动离线
     *
     * @param channel 玩家连接的管道
     */
    public void removeSession(Channel channel) {
        if (!channel.hasAttr(AttributeKey.valueOf(PLAYER_ID))) {
            channel.close();
        } else {
            channelMap.remove(getAttr(channel, PLAYER_ID));
            channel.close();
        }
    }

    /**
     * 单服广播
     *
     * @param serverId 服务器id
     * @param cmd      报文号
     * @param message  proto结构体
     */
    public void broadcastByServer(String serverId, int cmd, Message message) {
        List<Long> allOnline = getOnlineByServerId(serverId);
        for (Long playerId : allOnline) {
            sent(playerId, cmd, message);
        }
    }

    /**
     * 全服广播
     *
     * @param cmd     报文号
     * @param message proto结构体
     */
    public void broadcast(int cmd, Message message) {
        List<Long> allOnline = getAllOnline();
        for (Long playerId : allOnline) {
            sent(playerId, cmd, message);
        }
    }

    public Map<Long, Channel> getChannelMap() {
        return channelMap;
    }
}
