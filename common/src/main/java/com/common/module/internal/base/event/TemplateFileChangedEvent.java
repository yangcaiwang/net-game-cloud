package com.common.module.internal.base.event;
import com.common.module.internal.event.AbstractEvent;

import java.io.File;

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
