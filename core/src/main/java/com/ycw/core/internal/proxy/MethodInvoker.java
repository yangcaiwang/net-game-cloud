
package com.ycw.core.internal.proxy;

import java.lang.reflect.Method;

/**
 * <Jdk代理实现类>
 * <p>
 * ps: Java自带的代理,必须是接口类型才能被代理,不会产生内存溢出问题,但是只能代理接口
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public interface MethodInvoker {

    /**
     * 方法调用前过滤
     *
     * @param method 方法
     * @param args   方法参数
     */
    default boolean filterBefore(Method method, Object[] args) {
        return false;
    }

    /**
     * 方法调用前执行
     *
     * @param method 方法
     * @param args   方法参数
     */
    default void doBefore(Method method, Object[] args) {
    }

    /**
     * 方法调用后过滤
     *
     * @param method 方法
     * @param args   方法参数
     */
    default boolean filterAfter(Method method, Object[] args, Object result) {
        return false;
    }

    /**
     * 方法调用后执行
     *
     * @param method 方法
     * @param args   方法参数
     */
    default void doAfter(Method method, Object[] args, Object result) {
    }
}
