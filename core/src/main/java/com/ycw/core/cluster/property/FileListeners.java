
package com.ycw.core.cluster.property;

import com.google.common.collect.Maps;
import com.ycw.core.internal.thread.pool.scheduled.ScheduledExecutor;
import com.ycw.core.internal.thread.task.scheduled.AbstractScheduledTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
/**
 * <文件监听类>
 * <p>
 * ps: 用于热更配置
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class FileListeners {

	static private final Logger log = LoggerFactory.getLogger(FileListeners.class);

	private static FileListeners instance;

	public static FileListeners getInstance() {
		if (instance == null) {
			synchronized (FileListeners.class) {
				instance = new FileListeners();
			}
		}
		return instance;
	}

	public void addListener(File file, FileFilter filter, FileListener listener) {
		Objects.requireNonNull(file);
		Objects.requireNonNull(filter);
		Objects.requireNonNull(listener);
		if (!file.exists()) {
			return;
		}
		if (file.isFile() && filter.accept(file)) {
			ScheduledExecutor.scheduleWithFixedDelay(new FileModifiedListener(listener, file), 1, 3, TimeUnit.SECONDS);
			return;
		} else if (file.isDirectory()) {
			File[] fs = file.listFiles(f -> filter.accept(f));
			if (Objects.isNull(fs) || fs.length < 1) {
				return;
			}
			ScheduledExecutor.scheduleWithFixedDelay(new FileModifiedListener(listener, fs), 4, 5, TimeUnit.SECONDS);
			return;
		}
	}

	private final class FileModifiedListener extends AbstractScheduledTask {

		private final List<String> pathList = new CopyOnWriteArrayList<>();

		private final FileListener listener;

		private final Map<String, Long> lastModifiedMap = Maps.newConcurrentMap();

		private FileModifiedListener(FileListener listener, File... files) {
			super();
			this.listener = listener;
			for (File file : files) {
				pathList.add(file.getPath());
				lastModifiedMap.put(file.getPath(), file.lastModified());
			}
		}

		@Override
		public void exec() throws Exception {
			for (String path : pathList) {
				File file = new File(path);
				if (!file.exists()) {
					try {
						log.info("delete file :" + path);
						listener.onDeleted(file);
					} catch (Exception e) {
						if (PropertyConfig.isDebug()) {
							log.error("onDeleted | " + path, e);
						}
					}
				} else if (lastModifiedMap.get(file.getPath()) != file.lastModified()) {
					try {
						lastModifiedMap.put(file.getPath(), file.lastModified());
						log.info("update file :" + path);
						listener.onUpdate(file);
					} catch (Exception e) {
						log.error("onUpdate | " + path, e);
					} finally {

					}
				}
			}
		}
	}

	public interface FileListener {

		void onUpdate(File file) throws Exception;

		default void onDeleted(File file) throws Exception {

			log.error(String.format("文件[%s]被删除!", file));
		}
	}
}
