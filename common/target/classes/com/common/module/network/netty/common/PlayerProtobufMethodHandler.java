//package com.common.module.internal.netty.common;
//
//import com.client.proto.ErrorProto;
//import com.common.module.internal.netty.handler.MethodHandler;
//import com.common.module.internal.netty.handler.ReqHandler;
//import com.common.module.internal.netty.message.IMsgPacket;
//import com.common.module.util.ClassUtils;
//import com.common.module.util.LanguagesUtil;
//import com.google.protobuf.Message;
//import io.netty.channel.Channel;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.List;
//
//public abstract class PlayerProtobufMethodHandler extends GameProtobufMethodHandler {
//
//	protected static final Logger logger = LoggerFactory.getLogger(PlayerProtobufMethodHandler.class);
//	public static final String NEW_PROTO_OBJECT_STATIC_METHOD = "getDefaultInstance";
//	public PlayerProtobufMethodHandler() {
//		super();
//	}
//
//	@Override
//	public Message handleGameProto(Channel session, MethodHandler methodHandler, IMsgPacket iMsgPacket) throws Exception {
//		if (iMsgPacket.getPlayerId() > 0L) {
////			if (player == null) {
////				MsgManager.kitOut(session, IClient.OfflineCause.UNKNOWN, IClient.OffLineCmd.KIT_OUT.value, null);
////				logger.error("session关闭了，req={} : {}", req.getClass(), req);
////				return null;
////			}
//			ReqHandler annotation = methodHandler.getMethod().getAnnotation(ReqHandler.class);
//			if (annotation != null) {
//				if (annotation.actId() > 0 ) {
//					Message.Builder builder = getReturnMessageBuilder(methodHandler.getMethod());
//					if (builder == null) {
//						logger.error("该请求不存在返回 : {}", annotation.reqCmd());
//						return null;
//					}
//					return error(ErrorProto.ErrorCode.NO_ERROR_VALUE, builder);
//				}
//				if (annotation.funcId() > 0 ) {
//					Message.Builder builder = getReturnMessageBuilder(methodHandler.getMethod());
//					if (builder == null) {
//						logger.error("该请求不存在返回 : {}", annotation.reqCmd());
//						return null;
//					}
//					return error(ErrorProto.ErrorCode.NO_ERROR_VALUE, builder);
//				}
//			}
//		}
//
//		Class<?>[] parameterTypes = methodHandler.getMethod().getParameterTypes();
//		int paramLen = parameterTypes.length;
//		Object[] objects = new Object[paramLen];
//		for (int i = 0; i < paramLen; i++) {
//			Class<?> parameterType = parameterTypes[i];
//			if (parameterType.isAssignableFrom(Long.class)){
//				objects[i] = iMsgPacket.getPlayerId();
//			}
//			if (parameterType.isAssignableFrom(Channel.class)) {
//				objects[i] = session;
//			} else if (Message.class.isAssignableFrom(parameterType)) {
//				objects[i] = iMsgPacket.getMessage();
//			}
//		}
//
//		return (Message) methodHandler.getMethod().invoke(this, objects);
//	}
//
//	protected Message.Builder getReturnMessageBuilder(Method md) throws Exception {
//		Class<?> returnType = md.getReturnType();
//		Method method = ClassUtils.getMethod(false, returnType, NEW_PROTO_OBJECT_STATIC_METHOD);
//		if (method == null) {
//			return null;
//		}
//		Message request = (Message) method.invoke(null);
//		return request.newBuilderForType();
//	}
//
//	protected ErrorProto.ErrorResp errorResp(int code, Object... args) {
//		ErrorProto.ErrorResp.Builder errorBuilder = ErrorProto.ErrorResp.newBuilder();
//		errorBuilder.setErrorCode(code);
//		String errorMessage = LanguagesUtil.getErrorMessage(errorBuilder.getErrorCode(), args);
//		errorBuilder.setErrorMsg(errorMessage);
//		return errorBuilder.build();
//	}
//
//	protected ErrorProto.ErrorResp errorResp(String errorMessage) {
//		ErrorProto.ErrorResp.Builder errorBuilder = ErrorProto.ErrorResp.newBuilder();
//		errorBuilder.setErrorCode(-1);
//		errorBuilder.setErrorMsg(errorMessage);
//		return errorBuilder.build();
//	}
//
//	protected List<Long> protoStringToLong(List<String> strings) {
//		List<Long> longs = new ArrayList<>();
//		strings.forEach(s -> longs.add(Long.valueOf(s)));
//		return longs;
//	}
//
//	protected List<String> protoLongToString(List<Long> longs) {
//		List<String> strings = new ArrayList<>();
//		longs.forEach(l -> strings.add(String.valueOf(l)));
//		return strings;
//	}
//
//	/**
//	 * 返回对应协议的错误码 注意需要在协议里手动定义error字段，字段类型为 ErrorProto.ErrorResp
//	 *
//	 * @param errorCode
//	 * @return
//	 */
//	protected <T extends Message> T error(int errorCode, Message.Builder builder) {
//		try {
//			Class<Message.Builder> builderClass = (Class<Message.Builder>) builder.getClass();
//			Method error = builderClass.getMethod("setError", ErrorProto.ErrorResp.class);
//			if (error == null) {
//				return null;
//			}
//			error.invoke(builder, errorResp(errorCode));
//			return (T) builder.build();
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//		}
//
//		return (T) builder.build();
//	}
//
//	protected <T extends Message> T error(String msg, Message.Builder builder) {
//		try {
//			Class<Message.Builder> builderClass = (Class<Message.Builder>) builder.getClass();
//			Method error = builderClass.getMethod("setError",ErrorProto.ErrorResp.class);
//			if (error == null) {
//				return null;
//			}
//			error.invoke(builder, errorResp(msg));
//			return (T) builder.build();
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//		}
//
//		return (T) builder.build();
//	}
//}
