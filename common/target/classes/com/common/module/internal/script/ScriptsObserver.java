
package com.common.module.internal.script;

import com.common.module.internal.event.AbstractEventObserver;
import com.common.module.util.FileUtils;
import com.common.module.cluster.property.PropertyConfig;
import com.common.module.internal.delay.ServerTimer;

import java.io.File;

class ScriptsObserver extends AbstractEventObserver {

	/**
	 * 监听脚本变化
	 * 
	 * @param event
	 */
	private void recv(ServerTimer.ServerTimePassEvent event) {
		if (event.passType == ServerTimer.PassType.SECOND && event.onTime % 10 == 0) {
			String fs = PropertyConfig.getString("script.path", "./script");
			File file = new File(fs);
			if (!file.exists())
				return;
			File[] files = file.listFiles();
			if (files == null || files.length < 1)
				return;
			for (int i = 0; i < files.length; i++) {
				File f = files[i];
				if (!f.getName().endsWith(".groovy") && !f.getName().endsWith(".java")) {
					continue;
				}
				if (!Scripts.checkFileModify(f)) {
					continue;
				}
				Object script = Scripts.getScript(f.getAbsolutePath());
				if (!(script instanceof ScriptProcessor)) {// 非自定义脚本,一旦被加载就改名,因为service,handler,eventobserver加载后就保存在内存了
					Scripts.removeCachedScript(f);
					FileUtils.renameWithDate(f);
				}
			}
		}
	}
}
