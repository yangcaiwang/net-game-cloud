
package com.ycw.core.network.netty.socket;

import com.google.common.collect.Maps;
import com.google.protobuf.Message;
import com.ycw.core.internal.script.Scripts;
import com.ycw.core.network.netty.handler.SocketControllerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * <方法处理器的上下文实现类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class SocketCmdContext {
    private static final Logger log = LoggerFactory.getLogger(SocketCmdContext.class);
    private static final SocketCmdContext handlersMgr = new SocketCmdContext();

    public static SocketCmdContext getInstance() {
        return handlersMgr;
    }

    private final Map<Integer, SocketCmdParams> websocketCmdParamsMap = Maps.newConcurrentMap();
    private final Map<String, SocketControllerHandler> websocketCmdClassMap = Maps.newConcurrentMap();

    public SocketCmdParams getMethodHandler(int cmd) {
        return websocketCmdParamsMap.get(cmd);
    }

    public Class<? extends Message> getCmdToMsgType(int cmd) {
        SocketCmdParams socketCmdParams = getMethodHandler(cmd);
        if (socketCmdParams != null) {
            return socketCmdParams.getReqMsgType();
        }
        return null;
    }

    public SocketControllerHandler getHandlerMethodClass(String Name) {
        return websocketCmdClassMap.get(Name);
    }

    public void addMethodHandler(SocketCmdParams handler) {
        SocketCmdParams old = websocketCmdParamsMap.put(handler.getReqCmd(), handler);
        if (old != null) {
            SocketControllerHandler abstractIMethodHandler = websocketCmdClassMap.get(handler.getMethod().getDeclaringClass().getSimpleName());
            if (!Scripts.isScript(abstractIMethodHandler))
                throw new RuntimeException(String.format("重复注册TCP消息处理器cmd=[%s],handler=[%s]", handler.getReqCmd(), handler.getMethod().getDeclaringClass()));
            else
                log.info("add ioHandler [{}][{}] {} of script", handler.getReqCmd(), handler.getMethod().getName(), handler);
        }
    }

    public void addHandlerMethodClass(String name, SocketControllerHandler handler) {
        SocketControllerHandler old = websocketCmdClassMap.put(name, handler);
        if (old != null) {
            if (!Scripts.isScript(handler))
                throw new RuntimeException(String.format("重复注册TCP消息处理器cmd=[%s],handler=[%s]", name, handler));
            else
                log.info("add ioHandler [{}] of script", handler);
        }
    }
}
