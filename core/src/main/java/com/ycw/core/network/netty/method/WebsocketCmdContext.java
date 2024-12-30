
package com.ycw.core.network.netty.method;

import com.google.common.collect.Maps;
import com.google.protobuf.Message;
import com.ycw.core.internal.script.Scripts;
import com.ycw.core.network.netty.handler.WebsocketControllerHandler;
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
public class WebsocketCmdContext {
    private static final Logger log = LoggerFactory.getLogger(WebsocketCmdContext.class);
    private static final WebsocketCmdContext handlersMgr = new WebsocketCmdContext();

    public static WebsocketCmdContext getInstance() {
        return handlersMgr;
    }

    private final Map<Integer, WebsocketCmdParams> websocketCmdParamsMap = Maps.newConcurrentMap();
    private final Map<String, WebsocketControllerHandler> websocketCmdClassMap = Maps.newConcurrentMap();

    public WebsocketCmdParams getMethodHandler(int cmd) {
        return websocketCmdParamsMap.get(cmd);
    }

    public Class<? extends Message> getCmdToMsgType(int cmd) {
        WebsocketCmdParams websocketCmdParams = getMethodHandler(cmd);
        if (websocketCmdParams != null) {
            return websocketCmdParams.getReqMsgType();
        }
        return null;
    }

    public WebsocketControllerHandler getHandlerMethodClass(String Name) {
        return websocketCmdClassMap.get(Name);
    }

    public void addMethodHandler(WebsocketCmdParams handler) {
        WebsocketCmdParams old = websocketCmdParamsMap.put(handler.getReqCmd(), handler);
        if (old != null) {
            WebsocketControllerHandler abstractIMethodHandler = websocketCmdClassMap.get(handler.getMethod().getDeclaringClass().getSimpleName());
            if (!Scripts.isScript(abstractIMethodHandler))
                throw new RuntimeException(String.format("重复注册TCP消息处理器cmd=[%s],handler=[%s]", handler.getReqCmd(), handler.getMethod().getDeclaringClass()));
            else
                log.info("add ioHandler [{}][{}] {} of script", handler.getReqCmd(), handler.getMethod().getName(), handler);
        }
    }

    public void addHandlerMethodClass(String name, WebsocketControllerHandler handler) {
        WebsocketControllerHandler old = websocketCmdClassMap.put(name, handler);
        if (old != null) {
            if (!Scripts.isScript(handler))
                throw new RuntimeException(String.format("重复注册TCP消息处理器cmd=[%s],handler=[%s]", name, handler));
            else
                log.info("add ioHandler [{}] of script", handler);
        }
    }
}
