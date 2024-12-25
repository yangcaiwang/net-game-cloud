
package com.common.module.internal.delay;

import com.common.module.internal.event.AbstractEventObserver;

import java.util.Iterator;

/**
 * <服务器定时播报观察者实现类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
class ServerTimerObserver extends AbstractEventObserver {

	private void rec(ServerTimer.ServerTimePassEvent event) {
		if (event.passType == ServerTimer.PassType.SECOND) {
			Iterator<ServerTimerTask> iterator = ServerTimerTaskQueue.getInstance().taskMap.values().iterator();
			while (iterator.hasNext()) {
				ServerTimerTask task = iterator.next();
				if (task.getAction() == null) {
					iterator.remove();
					continue;
				}
				if (event.currentTime < task.getEnd()) {
					continue;
				}
				if (task.running()) {
					continue;
				}
				try {
					if (task.start()) {
						task.addCount();
						task.getAction().accept(task.getParam());
						log.info("currtime:{} taskendTIme:{} task:{}", event.currentTime, task.getEnd(), task);
					}
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				} finally {
					if (task.getMaxCount() == -1 && task.getAction() != null) {// 无限迭代执行
						task.again();
						task.stop();
					} else {
						if (task.getCount() < task.getMaxCount() && task.getAction() != null) {// 执行次数未满
							task.again();
							task.stop();
						} else {
							iterator.remove();
						}
					}
				}
			}
		}
	}
}
