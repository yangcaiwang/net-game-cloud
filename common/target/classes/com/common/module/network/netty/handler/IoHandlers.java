
package com.common.module.network.netty.handler;

import com.common.module.internal.script.Scripts;
import com.google.common.collect.Maps;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class IoHandlers {
	private static final Logger log = LoggerFactory.getLogger(IoHandlers.class);
	private static final IoHandlers handlersMgr = new IoHandlers();

	public static IoHandlers getInstance() {
		return handlersMgr;
	}

	private final Map<Integer, MethodHandler> methodHandlerMap = Maps.newConcurrentMap();
	private final Map<String, AbstractBinaryHandler> handleMethodClassMap = Maps.newConcurrentMap();

	public MethodHandler getMethodHandler(int cmd) {
		return methodHandlerMap.get(cmd);
	}

	public Class<? extends Message> getCmdToMsgType(int cmd) {
		MethodHandler methodHandler = getMethodHandler(cmd);
		if (methodHandler != null) {
			return methodHandler.getReqMsgType();
		}
		return null;
	}

	public AbstractBinaryHandler getHandlerMethodClass(String Name) {
		return handleMethodClassMap.get(Name);
	}

	void addMethodHandler(MethodHandler handler) {
		MethodHandler old = methodHandlerMap.put(handler.getReqCmd(), handler);
		if (old != null) {
			AbstractBinaryHandler abstractIMethodHandler = handleMethodClassMap.get(handler.getMethod().getDeclaringClass().getSimpleName());
			if (!Scripts.isScript(abstractIMethodHandler))
				throw new RuntimeException(String.format("重复注册TCP消息处理器cmd=[%s],handler=[%s]", handler.getReqCmd(), handler.getMethod().getDeclaringClass()));
			else
				log.info("add ioHandler [{}][{}] {} of script", handler.getReqCmd(), handler.getMethod().getName(), handler);
		}
	}

	void addHandlerMethodClass(String name, AbstractBinaryHandler handler) {
		AbstractBinaryHandler old = handleMethodClassMap.put(name, handler);
		if (old != null) {
			if (!Scripts.isScript(handler))
				throw new RuntimeException(String.format("重复注册TCP消息处理器cmd=[%s],handler=[%s]", name, handler));
			else
				log.info("add ioHandler [{}] of script", handler);
		}
	}
}
