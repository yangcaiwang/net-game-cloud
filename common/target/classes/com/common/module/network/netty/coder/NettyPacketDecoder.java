package com.common.module.network.netty.coder;

import com.common.module.network.netty.message.MessageProcess;
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
public class NettyPacketDecoder extends ByteToMessageDecoder {

    transient protected final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {
        Channel channel = ctx.channel();
        try {
            // 检查是否有足够的数据可以解码
            if (in.readableBytes() < 8) {
                return;
            }

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

            // 读取data字节数组
            byte[] dataArr = new byte[in.readableBytes() - b == 0 ? 0 : new byte[b].length];
            in.readBytes(dataArr);

            list.add(MessageProcess.getInstance().buildMsg(cmd, playerId, serverId, dataArr));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            in.clear();
            channel.close();
        }
    }
}
