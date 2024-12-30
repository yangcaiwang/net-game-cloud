package com.ycw.core.network.netty.handler;

import com.ycw.core.network.netty.method.WebsocketCmdContext;
import com.ycw.core.network.netty.method.WebsocketCmdParams;
import com.ycw.proto.CommonProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <后端控制器处理器实现类>
 * <p>
 * ps: 接收路由器的protobuf消息，把消息转发到服务器的控制器
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class ControllerHandler implements ControllerListener {

    private static final Logger log = LoggerFactory.getLogger(ControllerHandler.class);

    @Override
    public void exec(CommonProto.msg msg) {
        CommonProto.msg resp = process(msg);
        if (resp != null) {
            process(msg);
        }
    }

    @Override
    public CommonProto.msg process(CommonProto.msg msg) {
        WebsocketCmdParams websocketCmdParams = WebsocketCmdContext.getInstance().getMethodHandler(msg.getCmd());
        if (websocketCmdParams == null) {
            log.error("not found cmd:{} {} methodHandler", msg.getCmd(), msg);
            return null;
        }

        WebsocketControllerHandler handlerMethodClass = WebsocketCmdContext.getInstance().getHandlerMethodClass(websocketCmdParams.getMethod().getDeclaringClass().getSimpleName());
        if (handlerMethodClass == null) {
            log.error("not found cmd:{} handle class", msg.getCmd());
            return null;
        }

        return handlerMethodClass.process(websocketCmdParams, msg);
    }
}
