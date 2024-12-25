
package com.common.module.internal.proxy.cglib;

import com.common.module.util.StringUtils;
import com.common.module.internal.proxy.MethodInvoker;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Objects;
/**
 * <Cglib代理过滤器抽象类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public abstract class AbstractCglibMethodFilter implements MethodInvoker, MethodInterceptor {

	transient protected final Logger log = LoggerFactory.getLogger(getClass());

	public final Object target;

	public AbstractCglibMethodFilter(Object target) {
		super();
		Objects.nonNull(target);
		this.target = target;
	}

	@Override
	final public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		Object result = null;
		try {
			boolean filter = filterBefore(method, args);
			if (!filter)
				doBefore(method, args);
			if (!filter)
				result = proxy.invoke(target, args);
			if (!filter)
				filter = filterAfter(method, args, result);
			if (!filter)
				doAfter(method, args, result);
		} catch (Exception e) {
			log.error(e.getMessage() + ":" + obj + "," + method.toGenericString() + "," + StringUtils.toString(args) + "," + proxy, e);
			result = new RuntimeException(method.toGenericString());
		}
		return result;
	}
}
