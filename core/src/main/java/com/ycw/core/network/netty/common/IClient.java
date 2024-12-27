
package com.ycw.core.network.netty.common;

import java.io.Serializable;

/**
 * 客户端对象超类
 * 客户端身份唯一标识
 */
public interface IClient extends Serializable {

	/**
	 * 离线原因
	 */
	enum OfflineCause {

		/**
		 * 未知原因
		 */
		UNKNOWN,

		/**
		 * 手动自行关闭
		 */
		MANUAL,

		/**
		 * 非法操作,编解码异常
		 */
		ILLEGAL,

		/**
		 * 超时未注册
		 */
		UNREGISTER_TIMEOUT,

		/**
		 * 异常.io异常
		 */
		EXCEPTION,

		/**
		 * 心跳超时
		 */
		HEARTBEAT_TIMEOUT,

		/**
		 * 顶号
		 */
		REPLACE,

		/**
		 * 停服
		 */
		STOP_SERVER,
		/**
		 * 被踢下线
		 */
		KIT_OUT,

		/**
		 * 玩家数据异常
		 */
		ROLE_ERROR,

		/**
		 * 禁止登录
		 */
		FORBID,
		//
		;
	}

	enum OffLineCmd {
		NORMAL(-1), // 正常断开，可以重连
		KIT_PEOPLE(-98), // 顶号
		KIT_OUT(-99), // 踢到登录去（网络断开)
		;
		public int value;
		private OffLineCmd(int value) {
			this.value = value;
		}
	}

	/**
	 * 网络中存在的可以识别身份的唯一标识
	 * 
	 * @return
	 */
	default Long getIdentity() {
		throw new NullPointerException("必须要子类重写");
	}

	/**
	 * 昵称
	 * 
	 * @return
	 */
	default String nickname() {
		throw new NullPointerException("必须要子类重写");
	}

	/**
	 * 更新标识
	 */
	default void update() {
		throw new NullPointerException("必须要子类重写");
	}

	/**
	 * 获取玩家网络连接对象，不可序列化
	 * 
	 * @return
	 */
//	default IoSession getSession() {
//		return IoSessions.getSession(getIdentity());
//	}

	/**
	 * 为玩家绑定网络连接对象
	 * 
	 * @param session
	 * @param identity
	 *            玩家的身份标识
	 */
//	default void setSession(IoSession session) {
//		Objects.requireNonNull(session);
//		IoSessions.addClient(session, this);
//	}

	/**
	 * 关闭客户端
	 * 
	 * @param flush
	 *            是否把剩余的包下发给客户端
	 * @throws Exception
	 */
//	default void close(boolean flush) {
//		IoSession session = getSession();
//		if (session != null) {
//			if (flush)
//				IoSessions.kickout(OfflineCause.MANUAL, session, new Packet(session, 2, null));// 手动关闭网络连接,cmd=2,data=null
//			else
//				IoSessions.kickout(OfflineCause.MANUAL, session);
//		}
//	}

	/**
	 * 发消息给玩家
	 * 
	 * @param msg
	 *            全量消息体
	 * @return
	 */
//	default boolean sent(Packet msg) {
//
//		IoSessions.writeMessage(getSession(), msg);
//		return true;
//	}

	/**
	 * 发消息给玩家
	 * 
	 * @param cmd
	 *            消息头，指令
	 * @param array
	 *            协议内容
	 * @return
	 */
//	default boolean sent(int cmd, byte[] array) {
//		return sent(new Packet(getSession(), cmd, array));
//	}

	/**
	 * 直接发送protobuf对象
	 * 
	 * @param cmd
	 * @param message
	 * @return
	 */
//	default boolean sent(int cmd, Message message) {
//		IoSessions.writeMessage(getSession(), cmd, message);
//		return true;
//	}

	/**
	 * 是否在线
	 * 
	 * @return
	 */
//	default boolean online() {
//		return getSession() != null && getSession().isConnected();
//	}

	/**
	 * 客户端的远程ip地址
	 * 
	 * @return
	 */
//	default String getHostAddress() {
//		try {
//			return IoSessions.getHost(getSession());
//		} catch (Exception e) {
//			return "unknown";
//		}
//	}

	/**
	 * 客户端的远程套接字 /host:port
	 * 
	 * @return
	 */
//	default String getRemoteAddress() {
//		try {
//			return IoSessions.getAddr(getSession());
//		} catch (Exception e) {
//			return "unknown";
//		}
//	}

}
