package com.ycw.core.network.netty.message;

import com.game.proto.ProtocolProto;
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
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list){
        Channel channel = ctx.channel();
        try {
            // 确保至少有13个字节可读（4+2+2）
            if (in.readableBytes() < 8) {
                return;
            }

            // 读取消息号
            int cmd = in.readInt();

            // 读取玩家id
            long playerId = 0;
            if (cmd == ProtocolProto.ProtocolCmd.FIRST_CMD_VALUE) {
                playerId = in.readLong();
            }

            // 读取服务器id的字节数组长度
            int serverIdBytesLen = in.readShort();

            // 读取proto消息体的字节数组长度
            int bytesLen = in.readShort();

            String serverId = "";
            if (serverIdBytesLen > 0) {
                byte[] serverIdArr = new byte[serverIdBytesLen];
                in.readBytes(serverIdArr);
                serverId = new String(serverIdArr, StandardCharsets.UTF_8);
            }

            byte[] bytes = null;
            if (bytesLen > 0) {
                bytes = new byte[bytesLen];
                in.readBytes(bytes);
            }

            // 封装proto消息
            IMessage iMessage = new SocketMessage();
            iMessage.buildIMessage(cmd, bytes, playerId, serverId);
            list.add(iMessage);

//            in.resetReaderIndex();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            in.clear();
            channel.close();
        }
    }
}
