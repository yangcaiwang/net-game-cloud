
package com.common.module.internal.thread.task.linked;

import com.common.module.internal.thread.task.AbstractCoreTask;

/**
 * 存在于双向链表任务列表的抽象任务
 */
public abstract class AbstractLinkedTask extends AbstractCoreTask {

	/**
	 * 获取自己的id,(串行化的依据)用于固定任务执行所在的线程
	 * 
	 * @return
	 */
	public Object getIdentity() {
		throw new RuntimeException();
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
