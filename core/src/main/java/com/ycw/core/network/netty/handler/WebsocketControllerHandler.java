package com.ycw.core.network.netty.handler;

import com.game.proto.ErrorProto;
import com.google.protobuf.Message;
import com.ycw.core.internal.loader.service.AbstractService;
import com.ycw.core.network.netty.annotation.WebSocketCmd;
import com.ycw.core.network.netty.message.IMessage;
import com.ycw.core.network.netty.message.ProtoMessage;
import com.ycw.core.network.netty.method.WebsocketCmdContext;
import com.ycw.core.network.netty.method.WebsocketCmdParams;
import com.ycw.core.util.LanguagesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <websocket控制器处理器实现类>
 * <p>
 * ps: 所有业务控制器都继承这个抽象类
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class WebsocketControllerHandler extends AbstractService {
    public final Logger logger = LoggerFactory.getLogger(WebsocketControllerHandler.class);

    public WebsocketControllerHandler() {
        WebsocketCmdContext.getInstance().addHandlerMethodClass(getClass().getSimpleName(), this);
        Method[] declaredMethods = getClass().getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (method.isAnnotationPresent(Deprecated.class)) {
                continue;
            }
            WebSocketCmd annotation = method.getAnnotation(WebSocketCmd.class);
            if (annotation != null) {
                method.setAccessible(true);
                WebsocketCmdParams websocketCmdParams = new WebsocketCmdParams();
                websocketCmdParams.setMethod(method);
                websocketCmdParams.setComment(annotation.comment());
                websocketCmdParams.setReqCmd(annotation.reqCmd());
                websocketCmdParams.setResCmd(annotation.respCmd());
                for (Class<?> parameterType : method.getParameterTypes()) {
                    if (Message.class.isAssignableFrom(parameterType)) {
                        websocketCmdParams.setReqMsgType((Class<? extends Message>) parameterType);
                        break;
                    }
                }

                WebsocketCmdContext.getInstance().addMethodHandler(websocketCmdParams);
            }
        }
    }

    /**
     * 控制器处理业务 并返回
     *
     * @param websocketCmdParams 协议参数
     * @param msg                proto消息
     */
    public IMessage process(WebsocketCmdParams websocketCmdParams, IMessage msg) {
        try {
            Message message = handleProtoMessage(websocketCmdParams, msg);
            if (message == null) {
                return null;
            }
            IMessage iMessage = new ProtoMessage();
            iMessage.buildIMessage(websocketCmdParams.getResCmd(), msg.getPlayerId(), message);
            return iMessage;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    protected Message handleProtoMessage(WebsocketCmdParams websocketCmdParams, IMessage msg) {
        Object[] objects = new Object[0];
        try {
            Class<?>[] parameterTypes = websocketCmdParams.getMethod().getParameterTypes();
            int paramLen = parameterTypes.length;
            objects = new Object[paramLen];
            for (int i = 0; i < paramLen; i++) {
                Class<?> parameterType = parameterTypes[i];
                if (parameterType.isAssignableFrom(Long.class)) {
                    objects[i] = msg.getPlayerId();
                }
                if (Message.class.isAssignableFrom(parameterType)) {
                    objects[i] = msg.getMessage();
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        try {
            return (Message) websocketCmdParams.getMethod().invoke(this, objects);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 返回对应协议的错误码 注意需要在协议里手动定义error字段，字段类型为 ErrorProto.ErrorResp
     *
     * @param errorCode 错误码
     * @param builder   消息构建者
     */
    protected <T extends Message> T error(int errorCode, Message.Builder builder) {
        try {
            Class<Message.Builder> builderClass = (Class<Message.Builder>) builder.getClass();
            Method error = builderClass.getMethod("setError", ErrorProto.ErrorResp.class);
            if (error == null) {
                return null;
            }
            error.invoke(builder, errorResp(errorCode));
            return (T) builder.build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return (T) builder.build();
    }

    protected <T extends Message> T error(String msg, Message.Builder builder) {
        try {
            Class<Message.Builder> builderClass = (Class<Message.Builder>) builder.getClass();
            Method error = builderClass.getMethod("setError", ErrorProto.ErrorResp.class);
            if (error == null) {
                return null;
            }
            error.invoke(builder, errorResp(msg));
            return (T) builder.build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return (T) builder.build();
    }

    protected ErrorProto.ErrorResp errorResp(int code, Object... args) {
        ErrorProto.ErrorResp.Builder errorBuilder = ErrorProto.ErrorResp.newBuilder();
        errorBuilder.setErrorCode(code);
        String errorMessage = LanguagesUtil.getErrorMessage(errorBuilder.getErrorCode(), args);
        errorBuilder.setErrorMsg(errorMessage);
        return errorBuilder.build();
    }

    protected ErrorProto.ErrorResp errorResp(String errorMessage) {
        ErrorProto.ErrorResp.Builder errorBuilder = ErrorProto.ErrorResp.newBuilder();
        errorBuilder.setErrorCode(-1);
        errorBuilder.setErrorMsg(errorMessage);
        return errorBuilder.build();
    }
}
