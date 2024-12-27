//package com.common.module.internal.netty.common;
//
//import com.common.module.cluster.event.EventBusesImpl;
//import com.common.module.internal.db.entity.IdentityCreator;
//import com.common.module.internal.netty.handler.AbstractBinaryHandler;
//import com.common.module.internal.netty.handler.MethodHandler;
//import com.common.module.internal.netty.message.IMsgPacket;
//import com.common.module.internal.netty.message.MsgPacket;
//import com.common.module.util.StringUtils;
//import com.common.module.util.thread.task.AbstractCoreTask;
//import com.google.protobuf.Message;
//import io.netty.channel.Channel;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * 游戏所有的消息处理器都继承它
// */
//public abstract class GameProtobufMethodHandler extends AbstractBinaryHandler {
//	 protected final Logger logger = LoggerFactory.getLogger(getClass());
//
//	@Override
//	public IMsgPacket process(Channel session, IMsgPacket req, MethodHandler handler) throws Exception {
//		try {
//			Exception exception = accept(session, req.getMessage());
//			if (exception != null)
//				throw new RuntimeException(exception.getMessage(), exception);
//			if (req.getMessage() == null) {
//				logger.error("message cmd:{} is null data:{}", req.getCmd(), req.getByteData());
//				return null;
//			}
//			Message message = handleGameProto(session, handler, req.getMessage());
//			if (message == null) {
//				return null;
//			}
//			return new MsgPacket(handler.getResCmd(), message);
//		} catch (Exception e) {
//			exceptionRecord(req.getPlayerId(), req.getPlayerId() + "", StringUtils.toString(StringUtils.getStackTrace(e)), req.getCmd(), handler.getResCmd());
//			throw e;
//		}
//	}
//
//	/**
//	 * 处理游戏protobuffer消息
//	 *
//	 * @param session
//	 * @param request
//	 * @return
//	 * @throws Exception
//	 */
//	protected abstract Message handleGameProto(Channel session, MethodHandler methodHandler, IMsgPacket iMsgPacket) throws Exception;
//
//	/**
//	 * 是否接收消息,用于过滤黑名单等策略
//	 *
//	 * @param session
//	 * @param request
//	 * @return 异常
//	 */
//	protected Exception accept(Channel session, Message request) {
//		return null;
//	}
//
//	public final class After extends AbstractCoreTask {
//
//		public PlayerEntity player;
//		public IAfterDo r;
//
//		public After(){}
//
//		@Override
//		public Object getIdentity() {
//			return this.player.getId();
//		}
//
//		@Override
//		protected void exec() {
//			PlayerLinesManager.cancelPlayerRoleExpiredLogoutTask(player.getId());
//			IReset iReset = ServiceContext.reset();
//			iReset.resetPlayerRole(player.getId(), ResetType.WEEK, true);
//			iReset.resetPlayerRole(player.getId(), ResetType.WEEK_5, true);
//			iReset.resetPlayerRole(player.getId(), ResetType.DAY, true);
//			iReset.resetPlayerRole(player.getId(), ResetType.DAY_5, true);
//			if (r != null) {
//				r.doAfterReset(player);
//			}
//
//			PlayerLinesManager.dispatchPlayerRoleLogin(player.getId());
//			FightSceneManage.getInstance().exitScene(player.getId());
//			ServiceContext.playerService().sendPlayerUpdateAttr(player);
//		}
//
//	}
//
//	protected void exceptionRecord(long roleId, String sessionId, String exception, int requestCmd, int responseCmd) {
// 		if (exception.length() > 1024) {
//			exception = exception.substring(0, 1024);
//		}
//		EventBusesImpl.getInstance().asyncPublish(ExceptionLogEvent.valueOf(roleId, IdentityCreator.getServerId(), sessionId, exception, requestCmd, responseCmd));
//	}
//
//	@FunctionalInterface
//	protected interface IAfterDo {
//		void doAfterReset(PlayerEntity player);
//	}
//}
