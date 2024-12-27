
package com.ycw.core.internal.thread.task.linked;

import com.ycw.core.internal.thread.task.AbstractCoreTask;

/**
 * 存在于双向链表任务列表的抽象任务
 */
public abstract class PlayerAbstractLinkedTask extends AbstractCoreTask {

	private Object identity;
	
	/**
	 * 获取自己的id,(串行化的依据)用于固定任务执行所在的线程
	 * 
	 * @return
	 */
	public Object getIdentity() {
		return identity;
	}

	public void setIdentity(Object identity) {
		this.identity = identity;
	}

	/**
	 * 是否可以插队执行
	 * 
	 * @return
	 */
	public boolean cutline() {
		return false;
	}

}
