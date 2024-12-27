package com.ycw.core.internal.base.event;
import com.ycw.core.internal.event.AbstractEvent;

import java.io.File;

/**
 * <基础配置数据改变事件类>
 * <p>
 * ps: 用于热更配置
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class TemplateFileChangedEvent extends AbstractEvent {

    public static TemplateFileChangedEvent valueOf(File file) {
	TemplateFileChangedEvent changedEvent = new TemplateFileChangedEvent();
	changedEvent.setFile(file);
	return changedEvent;
    }

    private File file;

    public File getFile() {
	return file;
    }

    public void setFile(File file) {
	this.file = file;
    }

}
