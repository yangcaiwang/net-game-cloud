package com.ycw.core.network.netty.message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * <netty编码器实现类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class MessageEncoder extends MessageToByteEncoder<Object> {
    transient protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void encode(ChannelHandlerContext ctx, Object message, ByteBuf out){
        try {

            if (message instanceof IMessage) {
                IMessage msg = (IMessage) message;
                // 缓冲区长度根据字段动态创建 节省空间
                // cmd最多占用4个字节(int)
                int bufferLength = 4; //

                // playerId最多占用8个字节(long)
                if (msg.getPlayerId() != 0L) {
                    bufferLength += 8;
                }

                byte[] serverIdBytes = new byte[]{};
                if (StringUtils.isNotEmpty(msg.getServerId())) {
                    serverIdBytes = msg.getServerId().getBytes(StandardCharsets.UTF_8);
                    bufferLength += serverIdBytes.length;
                }

                byte[] bytes = new byte[]{};
                if (msg.getBytes() != null) {
                    bytes = msg.getBytes();
                    bufferLength += bytes.length;
                }

                ByteBuf buffer = ctx.alloc().buffer(bufferLength);
                buffer.writeInt(msg.getCmd());

                // 是否写入玩家id
                if (msg.getPlayerId() != 0L) {
                    buffer.writeLong(msg.getPlayerId());
                }

                // 写入服务器id的字节数组长度
                buffer.writeShort(serverIdBytes.length);

                // 写入proto消息体的字节数组长度
                buffer.writeShort(bytes.length);

                // 是否写入服务器id的字节数组
                if (serverIdBytes.length > 0) {
                    buffer.writeBytes(serverIdBytes);
                }

                // 是否写入proto消息体的字节数组
                if (bytes.length > 0) {
                    buffer.writeBytes(bytes);
                }

                // 写入缓冲区
                out.writeBytes(buffer);
            } else {
                logger.error("error msg type:{}", message);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
