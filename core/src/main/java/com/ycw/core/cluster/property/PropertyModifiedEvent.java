
package com.ycw.core.cluster.property;

import com.google.common.collect.Maps;
import com.ycw.core.internal.event.AbstractEvent;
import com.ycw.core.util.FileUtils;
import com.ycw.core.util.StringUtils;

import java.io.File;
import java.util.Map;

/**
 * <属性修改事件类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
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
