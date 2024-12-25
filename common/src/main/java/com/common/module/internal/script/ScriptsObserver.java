
package com.common.module.internal.script;

import com.common.module.internal.event.AbstractEventObserver;
import com.common.module.util.FileUtils;
import com.common.module.cluster.property.PropertyConfig;
import com.common.module.internal.delay.ServerTimer;

import java.io.File;

/**
 * <热更脚本观察者实现类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
class ScriptsObserver extends AbstractEventObserver {

    /**
     * 监听脚本变化
     *
     * @param event 事件
     */
    private void rec(ServerTimer.ServerTimePassEvent event) {
        if (event.passType == ServerTimer.PassType.SECOND && event.onTime % 10 == 0) {
            String fs = PropertyConfig.getString("script.path", "./script");
            File file = new File(fs);
            if (!file.exists())
                return;
            File[] files = file.listFiles();
            if (files == null || files.length < 1)
                return;
            for (File f : files) {
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
