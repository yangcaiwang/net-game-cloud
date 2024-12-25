
package com.common.module.internal.proxy.cglib;

import com.common.module.util.StringUtils;
import net.sf.cglib.core.DebuggingClassWriter;
import net.sf.cglib.core.DefaultGeneratorStrategy;
import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.Method;

/**
 * <Cglib代理实现类>
 * <p>
 * ps:
 * cglib动态代理,可以代理任意对象,但是不能多重代理,也就是已经是代理对象不能再创建代理,出现异常:
 * </p>
 * Caused by: java.lang.ClassFormatError: Duplicate method name "newInstance"
 * with signature "..........
 * </p>
 * 如果要实现多重代理,可以使用apring-aop动态代理(org.springframework.aop.framework.CglibAopProxy.
 * DynamicAdvisedInterceptor)
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class CglibProxy {

	/**
	 * cglib代理生成的类以这个为分隔符
	 */
	public static final String PROXY_CLASS_NAME_SPLIT_FLAG = "$$EnhancerByCGLIB$$";

	public static <T, MethodFilter extends AbstractCglibMethodFilter> T getProxy(MethodFilter methodFilter) {

		System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "./net/sf/cglib");// 开启代理dump出来的字节码

		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(methodFilter.target.getClass());
		enhancer.setUseCache(true);// 使用class缓存,防止内存溢出,生成的代理类都会复用原先产生在缓存中的类
		enhancer.setStrategy(DefaultGeneratorStrategy.INSTANCE);
		enhancer.setCallback(methodFilter);
		T result = (T) enhancer.create();
		return result;
	}

	public static void main(String[] args) {

		ObjectImpl object = new ObjectImpl();
		ObjectImpl proxy = getProxy(new AbstractCglibMethodFilter(object) {

			@Override
			public void doAfter(Method method, Object[] args, Object result) {

				System.err.println("" + method.toGenericString() + ",args" + StringUtils.toString(args) + ", executed : " + result);
			}
		});
		proxy.dosomething("xxxxxxx", 1, 2);
		System.err.println(proxy.getClass());
		proxy = getProxy(new AbstractCglibMethodFilter(object) {

			@Override
			public void doAfter(Method method, Object[] args, Object result) {

				System.err.println("" + method.toGenericString() + ",args" + StringUtils.toString(args) + ", executed : " + result);
			}
		});
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
