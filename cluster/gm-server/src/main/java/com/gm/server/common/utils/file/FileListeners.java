
package com.gm.server.common.utils.file;

import com.common.module.internal.thread.NamedThreadFactory;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimerTask;
import java.util.concurrent.*;

@Component
public class FileListeners {

	static private final org.slf4j.Logger log = LoggerFactory.getLogger(FileListeners.class);

//	private static FileListeners instance;
//
//	public static FileListeners getInstance() {
//		if (instance == null) {
//			synchronized (FileListeners.class) {
//				instance = new FileListeners();
//			}
//		}
//		return instance;
//	}

	public void addListener(File file, FileFilter filter, FileListener listener) {
		Objects.requireNonNull(file);
		Objects.requireNonNull(filter);
		Objects.requireNonNull(listener);
		if (!file.exists()) {
			return;
		}
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("file-scheduler"));

		if (file.isFile() && filter.accept(file)) {
			executor.scheduleWithFixedDelay(new FileModifiedListener(listener, file), 1, 10, TimeUnit.SECONDS);
		} else if (file.isDirectory()) {
			File[] fs = file.listFiles(f -> filter.accept(f));
			if (Objects.isNull(fs) || fs.length < 1) {
				return;
			}
			executor.scheduleWithFixedDelay(new FileModifiedListener(listener, fs), 4, 5, TimeUnit.SECONDS);
		}
	}

	private final class FileModifiedListener extends TimerTask {

		private final List<String> pathList = new CopyOnWriteArrayList<>();

		private final FileListener listener;

		private final Map<String, Long> lastModifiedMap = new ConcurrentHashMap<>();

		private FileModifiedListener(FileListener listener, File... files) {
			super();
			this.listener = listener;
			for (File file : files) {
				pathList.add(file.getPath());
				lastModifiedMap.put(file.getPath(), file.lastModified());
			}
		}

		@Override
		public void run() {
			for (String path : pathList) {
				File file = new File(path);
				if (!file.exists()) {
					try {
						log.info("delete file :" + path);
						listener.onDeleted(file);
					} catch (Exception e) {
//						if (HotConfigUtils.isDebug()) {
//							log.error("onDeleted | " + path, e);
//						}
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
