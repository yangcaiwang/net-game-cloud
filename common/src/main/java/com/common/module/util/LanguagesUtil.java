
package com.common.module.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Properties;

/**
 * 语言-国际化
 */
public class LanguagesUtil {
	private static final Logger log = LoggerFactory.getLogger(LanguagesUtil.class);
	private static Properties languageProperties = new Properties();

	public static boolean init(Properties properties) {
		try {
			LanguagesUtil.languageProperties.clear();
			LanguagesUtil.languageProperties.putAll(properties);
			return true;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}

	/**
	 * 获取用int数据结构配置的语言内容
	 * 
	 * @param key
	 * @param args
	 * @return
	 */
	public static String getMessage(int key, Object... args) {

		return getMessageFormat(String.valueOf(key), args);
	}

	/**
	 * 错误码都用这个封装
	 * 
	 * @param code
	 * @param args
	 * @return
	 */
	public static String getErrorMessage(int code, Object... args) {
		if (args == null || args.length == 0) {
			return getMessageFormat(String.valueOf(code), args);
		}
		// 改为字符串拼装
		return StringUtils.mergedJoiner("_$_", args);
	}

	/**
	 * 使用MessageFormat格式化文本
	 * 
	 * @param key
	 * @param args
	 * @return
	 */
	public static String getMessageFormat(String key, Object... args) {

		args = StringUtils.toStringArray(args);
		if (languageProperties == null || !languageProperties.containsKey(key)) {
			return key;
		}
		String value = languageProperties.getProperty(key);
		if (StringUtils.isEmpty(value)) {
			return key;
		}
		if (CollectionUtils.isEmpty(args)) {
			return value;
		}
		return MessageFormat.format(value, args);
	}

	/**
	 * 使用String.format格式化文本
	 * 
	 * @param key
	 * @param args
	 * @return
	 */
	public static String getStringFormat(String key, Object... args) {

		if (languageProperties == null || !languageProperties.containsKey(key)) {
			return key;
		}
		String value = languageProperties.getProperty(key);
		if (StringUtils.isEmpty(value)) {
			return key;
		}
		if (CollectionUtils.isEmpty(args)) {
			return value;
		}
		return String.format(value, args);
	}

	public static String getStringContent(int key, Object... args) {

		return getStringFormat(String.valueOf(key), args);
	}

	public static String getErrorContent(int code, Object... args) {

		return getStringContent(code, args);
	}
}
