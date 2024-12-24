
package com.common.module.cluster.property;

import com.common.module.internal.event.AbstractEvent;
import com.common.module.util.FileUtils;
import com.common.module.util.StringUtils;
import com.google.common.collect.Maps;

import java.io.File;
import java.util.Map;

public class PropertyModifiedEvent extends AbstractEvent {

	final public File file; // 变化的文件

	final public Map<String, String> modifies;// 变化的键值对

	public PropertyModifiedEvent(File file, Map<String, String> modifies) {
		super();
		this.file = file;
		this.modifies = modifies;
	}

	public static void main(String[] args) {

		Map<String, String> modifies = Maps.newHashMap();
		modifies.put("1", "a");
		PropertyModifiedEvent propertyModifiedEvent = new PropertyModifiedEvent(FileUtils.makeSureFile("./src/main/resources/ck.htm"), modifies);

		System.err.println(StringUtils.toString(propertyModifiedEvent));

	}
}
