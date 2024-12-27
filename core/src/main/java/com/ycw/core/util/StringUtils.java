
package com.ycw.core.util;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.TypeUtils;
import com.google.common.collect.Lists;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * <字符串工具类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class StringUtils {

	public static final String CHARSET_NAME = "UTF-8";

	public static final Charset CHARSET = Charset.forName(CHARSET_NAME);

	public static final CharsetEncoder CHARSET_ENCODER = CHARSET.newEncoder();

	public static final CharsetDecoder CHARSET_DECODER = CHARSET.newDecoder();

	/**
	 * 字符类型
	 * 
	 * @author ljs
	 *
	 */
	public static enum Type {

		/**
		 * 未知
		 */
		UNKNOWN(-1),

		/**
		 * 中文
		 */
		TYPE_CHINESE(1 << 0),

		/**
		 * 英文
		 */
		TYPE_ENGLISH(1 << 1),

		/**
		 * 数字
		 */
		TYPE_NUMBER(1 << 2),

		/**
		 * 符号
		 */
		TYPE_SYMBOL(1 << 3),

		/**
		 * 大写英文
		 */
		TYPE_LAGER_ENGLISH(1 << 4),

		/**
		 * 小写英文
		 */
		TYPE_SMALL_ENGLISH(1 << 5),

		/**
		 * 英文+数字
		 */
		TYPE_ENGLISH_NUMBER(TYPE_ENGLISH.value | TYPE_NUMBER.value),

		/**
		 * 中文+英文+数字
		 */
		TYPE_CHINESE_ENFLISH_NUMBER(TYPE_CHINESE.value | TYPE_ENGLISH.value | TYPE_NUMBER.value),

		;

		public final int value;

		private Type(int value) {
			this.value = value;
		}

	}

	/**
	 * 通用的分隔符
	 */
	public static final String SPLITTER = "-";

	/**
	 * 通过通用的分隔符连接,如果是db相关的数据清使用DBEntityUtils.merged(Object...objects)
	 * 
	 * @param objects 对象列表
	 * @return String
	 */
	public static String merged(Object... objects) {
		if (objects == null || objects.length < 1)
			throw new NullPointerException();
		return mergedJoiner(SPLITTER, objects);
	}

	/**
	 * 通过通用的分隔符还原,如果是db相关的数据清使用DBEntityUtils.parsed(String str)
	 * 
	 * @param s 传入的字符串
	 * @return String[]
	 */
	public static String[] parsed(String s) {
		if (isEmpty(s))
			throw new NullPointerException();
		return parsedByJoiner(SPLITTER, s);
	}

	/**
	 * 通过指定的的分隔符连接
	 * 
	 * @param joiner
	 *            指定的连接符
	 * @param objects 对象列表
	 * @return String
	 */
	public static String mergedJoiner(String joiner, Object... objects) {
		if (isEmpty(joiner)) {
			throw new RuntimeException("连接符joiner不可以为空:" + joiner);
		}

		if (!StringUtils.stringContainIt(joiner, Type.TYPE_SYMBOL)) {
			throw new RuntimeException("连接符joiner必须包含符号:" + joiner);
		}

		if (objects == null || objects.length < 1)
			throw new NullPointerException();

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < objects.length; i++) {
			Object obj = objects[i];
			sb.append(TypeUtils.castToString(obj));
			if (i < objects.length - 1) {
				sb.append(joiner);
			}
		}
		return sb.toString();
	}

	/**
	 * 通过指定的的分隔符还原
	 * 
	 * @param joiner 分隔符
	 * @param s 传入的字符串
	 * @return String[]
	 */
	public static String[] parsedByJoiner(String joiner, String s) {
		if (isEmpty(joiner) || isEmpty(s))
			throw new NullPointerException();
		return s.split(joiner);
	}

	public static String bytes2String(byte[] array) {
		Objects.requireNonNull(array);
		int offset = 0;
		int dest = 0;
		for (int i = 0; i < array.length - offset; i++) {
			if (array[offset + i] == '\0') {
				dest = i;
				break;
			}
		}
		return new String(array, offset, dest, CHARSET);
	}

	public static boolean isEmpty(String str) {

		return str == null || str.isEmpty();
	}

	/**
	 * 去掉字符串所有的空格
	 *
	 * @param str 传入的字符串
	 * @return String
	 */
	public static String replaceEmpty(String str) {

		return str.replaceAll(" ", "").trim();
	}

	/**
	 * 二分法倒转,效率更高
	 * 
	 * @param str 传入的字符串
	 * @return String
	 */
	public static String reversed(String str) {
		return new String(ArraysUtils.reversed2(str.toCharArray()));
	}

	public static boolean isAllSymbol(String str) {
		ArrayList<Type> types = getCharTypeList(str);
		for (Type t : types) {
			if (t != Type.TYPE_SYMBOL)
				return false;
		}
		return true;
	}

	public static boolean isAllEnglish(String str) {

		for (int i = 0; i < str.length(); i++) {
			if (!(str.charAt(i) >= 'A' && str.charAt(i) <= 'Z') && !(str.charAt(i) >= 'a' && str.charAt(i) <= 'z')) {
				return false;
			}
		}
		return true;
	}

	public static boolean isAllNumber(String str) {

		for (int i = 0; i < str.length(); i++) {
			if (!(str.charAt(i) >= '0' && str.charAt(i) <= '9')) {
				return false;
			}
		}
		return true;
	}

	public static boolean isAllChinese(String str) {

		for (int i = 0; i < str.length(); i++) {
			if (!(str.charAt(i) >= 0x4e00 && str.charAt(i) <= 0x9faf)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * str中是否不包含type类型的字符
	 */
	public static boolean stringNotContainIt(String str, Type type) {

		int charType = Type.UNKNOWN.value;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c >= 0x4E00 && c <= 0x9FAF) {
				charType &= ~Type.TYPE_CHINESE.value;
			} else if (c >= '0' && c <= '9') {
				charType &= ~Type.TYPE_NUMBER.value;
			} else if (c >= 'a' && c <= 'z') {
				charType &= ~Type.TYPE_SMALL_ENGLISH.value;
			} else if (c >= 'A' && c <= 'Z') {
				charType &= ~Type.TYPE_LAGER_ENGLISH.value;
			} else {
				charType &= ~Type.TYPE_SYMBOL.value;
			}
		}
		return (charType & type.value) != 0;
	}

	/**
	 * str中是否包含type类型的字符
	 */
	public static boolean stringContainIt(String str, Type type) {

		Type charType = Type.UNKNOWN;
		int size = str.length();
		for (int i = 0; i < size; i++) {
			char c = str.charAt(i);
			if (c >= 0x4E00 && c <= 0x9FAF) {
				charType = Type.TYPE_CHINESE;
			} else if (c >= '0' && c <= '9') {
				charType = Type.TYPE_NUMBER;
			} else if (c >= 'a' && c <= 'z') {
				charType = Type.TYPE_SMALL_ENGLISH;
			} else if (c >= 'A' && c <= 'Z') {
				charType = Type.TYPE_LAGER_ENGLISH;
			} else {
				charType = Type.TYPE_SYMBOL;
			}
			if (charType == type) {
				return true;
			}
		}
		return false;
	}

	public static ArrayList<Type> getCharTypeList(String str) {

		int len = str.length();
		ArrayList<Type> list = new ArrayList<Type>(len);
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
			if (c >= 0x4e00 && c <= 0x9faf) {
				list.add(Type.TYPE_CHINESE);
			} else if (c >= 'A' && c <= 'Z') {
				list.add(Type.TYPE_LAGER_ENGLISH);
			} else if (c >= 'a' && c <= 'z') {
				list.add(Type.TYPE_SMALL_ENGLISH);
			} else if (c >= '0' && c <= '9') {
				list.add(Type.TYPE_NUMBER);
			} else {
				list.add(Type.TYPE_SYMBOL);
			}
		}
		return list;
	}

	public static String toString(byte[] buffer) {

		return Arrays.toString(buffer);
	}

	public static String toString(short[] buffer) {

		return Arrays.toString(buffer);
	}

	public static String toString(int[] buffer) {

		return Arrays.toString(buffer);
	}

	public static String toString(long[] buffer) {

		return Arrays.toString(buffer);
	}

	public static String toString(float[] buffer) {

		return Arrays.toString(buffer);
	}

	public static String toString(double[] buffer) {

		return Arrays.toString(buffer);
	}

	public static String toString(boolean[] buffer) {

		return Arrays.toString(buffer);
	}

	public static String toString(char[] buffer) {

		return Arrays.toString(buffer);
	}

	public static String toString(String[] arr) {

		return Arrays.toString(arr);
	}

	public static String toString(Object[] arr) {

		try {
			return Arrays.toString(arr);
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public static String deepToString(Object[] arr) {

		try {
			return Arrays.deepToString(arr);
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	/**
	 * 把对象转String,仅仅提供可视化需求,并不能反序列化,默认使用fastjson规则
	 * 
	 * @param obj
	 *            必须是标准的bean 所有字段必须有setter/getter 方法,并且必须有无参构造器
	 *            </p>
	 *            如果不是bean,那么fastjson序列化可能会出现异常导致失败
	 *            </p>
	 *            如果失败并且抛出异常就使用toString(Object object, boolean
	 *            showSuperClassField)重新转string
	 */
	public static String toString(Object obj) {
		if (Objects.isNull(obj))
			return "null";
		try {
			return SerializationUtils.beanToJson(obj, SerializerFeature.PrettyFormat, SerializerFeature.WriteClassName, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteEnumUsingToString);
		} catch (Exception ex) {
			return toString(obj, true);
		}
	}

	/**
	 * 把对象转String,仅仅提供可视化需求,并不能反序列化,默认使用fastjson规则
	 *
	 * @param obj
	 *            必须是标准的bean 所有字段必须有setter/getter 方法,并且必须有无参构造器
	 *            </p>
	 *            如果不是bean,那么fastjson序列化可能会出现异常导致失败
	 *            </p>
	 *            如果失败并且抛出异常就使用toString(Object object, boolean
	 *            showSuperClassField)重新转string
	 */
	public static String toStringFeature(Object obj) {
		if (Objects.isNull(obj))
			return "null";
		try {
			return SerializationUtils.beanToJson(obj, SerializerFeature.NotWriteDefaultValue);
		} catch (Exception ex) {
			return toString(obj, true);
		}
	}

	/**
	 * 把对象转String,仅仅提供可视化需求,并不能反序列化,默认使用fastjson规则
	 *
	 * @param obj
	 *            必须是标准的bean 所有字段必须有setter/getter 方法,并且必须有无参构造器
	 *            </p>
	 *            如果不是bean,那么fastjson序列化可能会出现异常导致失败
	 *            </p>
	 *            如果失败并且抛出异常就使用toString(Object object, boolean
	 *            showSuperClassField)重新转string
	 */
	public static String toJsonString(Object obj) {
		if (Objects.isNull(obj))
			return "null";
		try {
			return SerializationUtils.beanToJson(obj, SerializerFeature.WriteClassName, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteEnumUsingToString, SerializerFeature.NotWriteDefaultValue);
		} catch (Exception ex) {
			return toString(obj, true);
		}
	}

	/**
	 * 对象转string,没有实现toString的成员变量对象会显示identityHashCode的值
	 * 
	 * @param obj 对象
	 * @param showSuperClassField
	 *            是否包含父类的字段
	 * @return String
	 */
	public static String toString(Object obj, boolean showSuperClassField) {
		List<Field> list = Lists.newArrayList();
		ClassUtils.fieldList(showSuperClassField, obj.getClass(), list);
		StringBuilder sb = new StringBuilder();
		for (Field field : list) {
			try {
				String name = field.getName();
				Object value = field.get(obj);
				sb.append(name).append("=").append(value == null ? "null" : value).append(",\n");
			} catch (Exception e) {
				LoggerFactory.getLogger(StringUtils.class).error(e.getMessage() + ":" + field.toGenericString() + "," + showSuperClassField, e);
			}
		}
		return obj.getClass().getName() + ":\n" + sb.toString();
	}

	public static String[] toStringArray(Object... args) {

		if (args == null || args.length < 1) {
			return new String[0];
		}
		String[] ss = new String[args.length];
		for (int i = 0; i < ss.length; i++) {
			ss[i] = TypeUtils.castToString(args[i]);
		}
		return ss;
	}
}
