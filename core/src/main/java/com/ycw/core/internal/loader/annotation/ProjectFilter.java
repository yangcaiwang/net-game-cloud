
package com.ycw.core.internal.loader.annotation;

import java.io.File;
import java.util.jar.JarFile;

/**
 * 工程(项目)过滤器
 *
 */
public interface ProjectFilter {

	/**
	 * 是否认可这个jar包,运行jar包情况下用
	 */
	boolean accept(JarFile jar);

	/**
	 * 是否认可这个项目文件夹,以文件形式运行情况用
	 */
	boolean accept(File file);
}
