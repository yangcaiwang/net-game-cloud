
package com.common.module.internal.thread.task;

import com.common.module.internal.loader.service.IService;

/**
 * 任务超类
 */
public interface ICoreTask extends IService {

	/**
	 * 单个执行慢任务耗时-毫秒
	 */
	int SLOWLY = 100;

	/**
	 * 批量执行繁忙任务-任务个数
	 */
	int BUSILY = 10;

	default Object getIdentity() {
		throw new RuntimeException();
	}

}
