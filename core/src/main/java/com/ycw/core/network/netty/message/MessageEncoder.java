package com.ycw.core.network.netty.message;

import com.ycw.core.util.CollectionUtils;
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
    protected void encode(ChannelHandlerContext ctx, Object message, ByteBuf out) throws Exception {
        if (message instanceof IMessage) {
            IMessage msg = (IMessage) message;

            // 缓冲区长度根据字段动态创建 节省空间
            int bufferLength = 8; // cmd最多占用8个字节

            if (msg.getPlayerId() != 0) {
                bufferLength += 20;
            }

            int serverIdLen = 0;
            if (StringUtils.isNotEmpty(msg.getServerId())) {
                byte[] serverIdArr = msg.getServerId().getBytes(StandardCharsets.UTF_8);
                serverIdLen = serverIdArr.length;
                bufferLength += serverIdArr.length;
            }

            byte[] binaryData = null;
            if (msg.getArray() != null) {
                binaryData = msg.getArray();
                bufferLength += binaryData.length;
            }

            ByteBuf buffer = ctx.alloc().buffer(bufferLength);
            buffer.writeInt(msg.getCmd());

            // 是否写入玩家id
            if (msg.getPlayerId() != 0) {
                buffer.writeLong(msg.getPlayerId());
            }

            // 这里存是服务器id的长度
            buffer.writeByte((byte) serverIdLen);
            // 是否写入服务器id
            if (StringUtils.isNotEmpty(msg.getServerId())) {
                byte[] serverIdArr = msg.getServerId().getBytes(StandardCharsets.UTF_8);
                buffer.writeBytes(serverIdArr);
            }

            if (!CollectionUtils.isEmpty(binaryData)) {
                buffer.writeBytes(binaryData);
            }

            out.writeBytes(buffer);
        } else {
            logger.error("error msg type:{}", message);
        }
    }
}
