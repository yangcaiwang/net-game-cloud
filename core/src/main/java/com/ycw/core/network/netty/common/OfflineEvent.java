
package com.ycw.core.network.netty.common;

import com.ycw.core.internal.event.AbstractEvent;

/**
 * 离线事件
 */
public class OfflineEvent extends AbstractEvent {

	/**
	 * 当前的客户端实体
	 */
	public final Long clientIdentity;

	/**
	 * 离线原因
	 */
	public final IClient.OfflineCause cause;

	/**
	 * 构造离线事件
	 *
	 * @param clientIdentity
	 *            当前的客户端实体id
	 * @param cause
	 *            离线原因
	 */
	public OfflineEvent(Long clientIdentity, IClient.OfflineCause cause) {
		super();
		this.clientIdentity = clientIdentity;
		this.cause = cause;
	}

}
