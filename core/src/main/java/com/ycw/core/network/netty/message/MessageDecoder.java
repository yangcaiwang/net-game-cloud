package com.ycw.core.network.netty.message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * <netty解码器实现类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class MessageDecoder extends ByteToMessageDecoder {

    transient protected final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {
        Channel channel = ctx.channel();
        try {
            // 检查是否有足够的数据可以解码
            if (in.readableBytes() < 8) {
                return;
            }
            int length = in.readableBytes();

            // 读取消息号
            int cmd = in.readInt();

            long playerId = 0;
            if (cmd == 0) {
                // 读取玩家id字符串
                playerId = in.readLong();
            }

            byte b = in.readByte(); // 跳过长度字段（存储的是服务器id的字节数组长度）
            // 读取服务器id字符串
            String serverId = "";
            if (b != 0) {
                byte[] serverIdArr = new byte[b];
                in.readBytes(serverIdArr);
                serverId = new String(serverIdArr, StandardCharsets.UTF_8);
            }

            // 读取proto消息体的字节数组
            byte[] bytes = new byte[Math.max(length - b, 0)];
            in.readBytes(bytes);
            IMessage iMessage = new SocketMessage();
            iMessage.buildIMessage(cmd, bytes, playerId, serverId);
            list.add(iMessage);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            in.clear();
            channel.close();
        }
    }
}
