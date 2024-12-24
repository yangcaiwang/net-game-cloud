
package com.common.module.internal.proxy.jdk;

import com.common.module.internal.proxy.MethodInkover;
import com.common.module.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;

public abstract class AbstractJdkMethodFilter implements MethodInkover, InvocationHandler {

	transient protected final Logger log = LoggerFactory.getLogger(getClass());

	public final Object target;

	public AbstractJdkMethodFilter(Object target) {
		super();
		Objects.nonNull(target);
		this.target = target;
	}

	@Override
	public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
		Object result = null;
		try {
			boolean filter = filterBefore(method, args);
			if (!filter)
				doBefore(method, args);
			if (!filter)
				result = method.invoke(target, args);
			if (!filter)
				filter = filterAfter(method, args, result);
			if (!filter)
				doAfter(method, args, result);
		} catch (Exception e) {
			log.error(e.getMessage() + ":" + obj + "," + method.toGenericString() + "," + StringUtils.toString(args), e);
			result = new RuntimeException(method.toGenericString());
		}
		return result;
	}
}
