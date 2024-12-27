
package com.ycw.core.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.List;
import java.util.Set;

/**
 * <类工具类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class ClassUtils {

	public static void main(String[] args) {
		String s1 = ClassUtils.class.getName().substring(0, ClassUtils.class.getName().lastIndexOf('.'));
		String s2 = ClassUtils.class.getPackage().getName();
		System.err.println(s1 + "," + s2 + "," + (s1.equals(s2)));
	}

	private static final Logger log = LoggerFactory.getLogger(ClassUtils.class);

	/**
	 * class 是否在指定的包中
	 * 
	 * @param cls
	 * @param packageName
	 * @return
	 */
	public static boolean inPackage(Class<?> cls, String packageName) {
		return cls.getName().startsWith(packageName);
	}

	/**
	 * 获取class包名
	 * 
	 * @param cls
	 * @return
	 */
	public static String getPackage(Class<?> cls) {
		// return cls.getName().substring(0, cls.getName().lastIndexOf('.'));
		return cls.getPackage().getName();
	}

	/**
	 * 判断该类型是否包含泛型
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isParameterizedType(Type type) {
		return type instanceof ParameterizedType;
	}

	/**
	 * 判断该类型数据是否是bean,只允许无参数构造器和setter/getter 函数,不可以有多余的方法
	 * 
	 * @param clz
	 * @return Exception null表示是标准的bean
	 */
	public static Exception isBean(Class<?> clz) {
		if (!Modifier.isPublic(clz.getModifiers())) {
			return new RuntimeException(String.format("[%s] 类修饰符不是public", clz));
		}

		Constructor<?>[] constructors = clz.getDeclaredConstructors();
		boolean found = false;
		for (Constructor<?> constructor : constructors) {
			if (constructor.getParameterCount() == 0) {
				found = true;
				break;
			}
			
			if (!Modifier.isPublic(constructor.getModifiers())) {
				return new RuntimeException(String.format("[%s] 构造器修饰符不是public", clz));
			}
		}

		if (!found) {
			return new RuntimeException(String.format("[%s] 未定义无参构造器", clz));
		}

		List<Field> fields = Lists.newArrayList();
		fieldList(false, clz, fields);

		List<Method> methods = Lists.newArrayList();
		methodList(false, clz, methods);

		for (Field field : fields) {

			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}

			Method method = findGetter(clz, field);
			if (method == null) {
				return new RuntimeException(String.format("[%s] 不存在getter 方法[%s]", field.toGenericString(), findGetterName(field)));
			}
			methods.remove(method);
			method = findSetter(clz, field);
			if (method == null) {
				return new RuntimeException(String.format("[%s] 不存在setter 方法[%s]", field.toGenericString(), findSetterName(field)));
			}
			methods.remove(method);
		}
		if (!methods.isEmpty()) {
			String[] names = new String[methods.size()];
			for (int i = 0; i < names.length; i++) {
				names[i] = methods.get(i).getName();
			}
			return new RuntimeException(String.format("[%s] 多余方法[%s]", clz, StringUtils.toString(names)));
		}
		return null;

	}

	public static String findGetterName(Field field) {
		String name = field.getName();
		String n = name.substring(0, 1).toUpperCase();
		String fix = name.substring(1);
		Class<?> type = field.getType();
		String methodName = null;
		if (type == boolean.class || type == Boolean.class) {
			methodName = "is" + n + fix;
		} else {
			methodName = "get" + n + fix;
		}
		return methodName;
	}

	public static String findSetterName(Field field) {
		String name = field.getName();
		String n = name.substring(0, 1).toUpperCase();
		String fix = name.substring(1);
		return "set" + n + fix;
	}

	public static Method findGetter(Class<?> cls, Field field) {
		Method geter = null;
		String methodName = null;
		try {
			methodName = findGetterName(field);
			geter = cls.getDeclaredMethod(methodName);
			geter.setAccessible(true);
			return geter;
		} catch (NoSuchMethodException e) {
			if (cls == Object.class) {
				log.error(String.format("[%s] can not find getter method[%s]!", field, methodName), e);
				return null;
			}
			cls = ((Class<?>) cls.getSuperclass());
			return findGetter(cls, field);
		}
	}

	public static Method findSetter(Class<?> cls, Field field) {
		Method setter = null;
		String methodName = null;
		Class<?> paramClass = null;
		try {
			methodName = findSetterName(field);
			paramClass = field.getType();
			setter = cls.getDeclaredMethod(methodName, paramClass);
			setter.setAccessible(true);
			return setter;
		} catch (NoSuchMethodException e) {
			if (cls == Object.class) {
				log.error(String.format("[%s] can not find setter method[%s(%s)]!", field, methodName, paramClass), e);
				return null;
			}
			cls = ((Class<?>) cls.getSuperclass());
			return findSetter(cls, field);
		}
	}

	/**
	 * 所有相关的class,自己,父,接口,父类的父类,父类的接口...
	 * 
	 * @param clz
	 * @return
	 */
	public static Set<Class<?>> getAllClasses(Class<?> clz) {

		Set<Class<?>> classes = Sets.newLinkedHashSet();
		allClasses(clz, classes);
		return classes;
	}

	/**
	 * 所有相关的class,自己,父,接口,父类的父类,父类的接口...
	 * 
	 * @param clz
	 * @param classes
	 *            暂存器
	 */
	public static void allClasses(Class<?> clz, Set<Class<?>> classes) {

		if (classes == null)
			classes = Sets.newLinkedHashSet();
		if (classes.contains(clz))
			return;
		classes.add(clz);
		Class<?> surClz = clz.getSuperclass();
		if (surClz != null) {
			allClasses(surClz, classes);
		}
		Class<?>[] infs = clz.getInterfaces();
		if (infs != null && infs.length > 0) {
			for (Class<?> inf : infs) {
				allClasses(inf, classes);
			}
		}
		if (surClz != null) {
			infs = surClz.getInterfaces();
			if (infs != null && infs.length > 0) {
				for (Class<?> inf : infs) {
					allClasses(inf, classes);
				}
			}
		}
	}

	/**
	 * 检查class的泛型参数类型
	 * 
	 * @param clz
	 *            必须是实现者(impl),java的泛型是假的,只能在实现后才能确定泛型
	 * @return
	 */
	public static Class<?>[] getTypeArguments(Class<?> clz) {

		Type[] types = ((ParameterizedType) clz.getGenericSuperclass()).getActualTypeArguments();
		Class<?>[] classes = new Class[types.length];
		for (int i = 0; i < classes.length; i++) {
			classes[i] = (Class<?>) types[i];
		}
		return classes;
	}

	/**
	 * a对象所对应类信息是b对象所对应的类信息的父类或者是父接口，简单理解即a是b的父类或接口</br>
	 * a对象所对应类信息与b对象所对应的类信息相同，简单理解即a和b为同一个类或同一个接口
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean isAssignableFrom(Class a, Class b) {

		if (a == null || b == null)
			return false;
		return a.isAssignableFrom(b);// a对象所对应类信息与b对象所对应的类信息相同，简单理解即a和b为同一个类或同一个接口
	}

	/**
	 * 获取指定的方法
	 * 
	 * @param deep
	 *            是否递归向上找父类的方法
	 * @param clz
	 * @param name
	 * @param parameterTypes
	 * @return
	 */
	public static Method getMethod(boolean deep, Class<?> clz, String name, Class<?>... parameterTypes) {

		Method method = null;
		try {
			method = clz.getDeclaredMethod(name, parameterTypes);
			method.setAccessible(true);
			return method;
		} catch (Exception e) {
			if (clz == Object.class)
				return null;
			if (deep)
				return getMethod(deep, clz.getSuperclass(), name, parameterTypes);
			return null;
		}
	}

	/**
	 * 找出所有的方法
	 * 
	 * @param deep
	 *            是否递归向上找父类的方法
	 * @param clz
	 * @param list
	 *            找到的方法存放在这里
	 */
	public static void methodList(boolean deep, Class<?> clz, List<Method> list) {

		Method[] methods = clz.getDeclaredMethods();
		if (methods != null && methods.length > 0) {
			for (Method method : methods) {
				method.setAccessible(true);
				list.add(method);
			}
		}
		if (clz == Object.class)
			return;
		if (deep)
			methodList(deep, clz.getSuperclass(), list);
	}

	public static void fieldList(boolean deep, Class<?> clz, List<Field> list) {

		Field[] fields = clz.getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				field.setAccessible(true);
				list.add(field);
			}
		}
		if (clz == Object.class)
			return;
		if (deep)
			fieldList(deep, clz.getSuperclass(), list);
	}

	public static Field getField(boolean deep, Class<?> clz, String name) {

		Field field = null;
		try {
			field = clz.getDeclaredField(name);
			field.setAccessible(true);
			return field;
		} catch (Exception e) {
			if (clz == Object.class)
				return null;
			if (deep)
				return getField(deep, clz.getSuperclass(), name);
			return null;
		}
	}

	public static boolean isSimpleMode(Class<?> type) {
		return type == byte.class || type == Byte.class

				|| type == short.class || type == Short.class

				|| type == int.class || type == Integer.class

				|| type == long.class || type == Long.class

				|| type == float.class || type == Float.class

				|| type == double.class || type == Double.class

				|| type == boolean.class || type == Boolean.class

				|| type == char.class || type == Character.class

				|| type == String.class

				|| type == Enum.class

				|| type == java.util.Date.class

				|| type == java.sql.Date.class

				|| type == java.sql.Timestamp.class;
	}

}
