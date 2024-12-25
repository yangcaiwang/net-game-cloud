package com.common.module.network.netty.handler;

import com.common.module.network.netty.annotation.NetHandler;
import com.google.common.util.concurrent.AbstractService;
import com.google.protobuf.Message;

import java.lang.reflect.Method;

/**
 * <netty二进制处理器实现类>
 * <p>
 * ps: 封装后传递到具体业务层
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
abstract public class AbstractBinaryHandler extends AbstractService {

    public AbstractBinaryHandler() {
        MethodHandlerManager.getInstance().addHandlerMethodClass(getClass().getSimpleName(), this);
        Method[] declaredMethods = getClass().getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (method.isAnnotationPresent(Deprecated.class)) {
                continue;
            }
            NetHandler annotation = method.getAnnotation(NetHandler.class);
            if (annotation != null) {
                method.setAccessible(true);
                MethodHandler methodHandler = new MethodHandler();
                methodHandler.setMethod(method);
                methodHandler.setComment(annotation.comment());
                methodHandler.setReqCmd(annotation.reqCmd());
                methodHandler.setResCmd(annotation.respCmd());
                for (Class<?> parameterType : method.getParameterTypes()) {
                    if (Message.class.isAssignableFrom(parameterType)) {
                        methodHandler.setReqMsgType((Class<? extends Message>) parameterType);
                        break;
                    }
                }
                MethodHandlerManager.getInstance().addMethodHandler(methodHandler);
            }
        }
    }
}
