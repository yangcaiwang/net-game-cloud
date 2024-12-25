
package com.common.module.internal.proxy.jdk;

import com.common.module.util.ArraysUtils;
import com.common.module.util.StringUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * <Jdk代理实现类>
 * <p>
 * ps: Java自带的代理,必须是接口类型才能被代理,不会产生内存溢出问题,但是只能代理接口
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class JdkProxy {

	public static <T, MethodFilter extends AbstractJdkMethodFilter> T getProxy(MethodFilter methodFilter, Class<?>... interfaces) {

		Validate.isTrue(!ArraysUtils.isEmpty(interfaces));
		for (int i = 0; i < interfaces.length; i++) {
			Validate.isTrue(ClassUtils.getAllInterfaces(methodFilter.target.getClass()).contains(interfaces[i]));
		}

		return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), interfaces, methodFilter);
	}

	public static void main(String[] args) {

		IObject object = new ObjectImpl();
		IObject proxy = getProxy(new AbstractJdkMethodFilter(object) {

			@Override
			public void doAfter(Method method, Object[] args, Object result) {
				System.err.println("" + method.toGenericString() + ",args=" + StringUtils.toString(args) + ", executed  :" + result);
			}
		}, IObject.class);
		proxy.dosomething("xxxxxxx", 1, 2);
		System.err.println(proxy.getClass());

		proxy = getProxy(new AbstractJdkMethodFilter(object) {

			@Override
			public void doAfter(Method method, Object[] args, Object result) {
				System.err.println("" + method.toGenericString() + ",args=" + StringUtils.toString(args) + ", executed  :" + result);
			}
		}, IObject.class);
		proxy.dosomething("xxxxxxx", 1, 2);
		System.err.println(proxy.getClass());
	}

	static interface IObject {

		void dosomething(String str, int a, int b);
	}

	static class ObjectImpl implements IObject {

		@Override
		public void dosomething(String str, int a, int b) {

			System.err.println(str + "," + a + "," + b);
		}
	}
}
