package com.ycw.core.network.netty.message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) {
        Channel channel = ctx.channel();
        try {
            // 确保至少有4个字节可读（4）
            int len = in.readableBytes();
            if (len < 4) {
                return;
            }

            // 读取消息号
            int cmd = in.readInt();

            byte[] bytes = null;
            if (len - 4 > 0) {
                bytes = new byte[len - 4];
                in.readBytes(bytes);
            }

            // 封装proto消息
            IMessage iMessage = new SocketMessage();
            iMessage.buildIMessage(cmd, bytes);
            list.add(iMessage);

//            in.resetReaderIndex();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            in.clear();
            channel.close();
        }
    }
}
