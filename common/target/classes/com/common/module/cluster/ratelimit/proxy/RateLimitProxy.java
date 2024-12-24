package com.common.module.cluster.ratelimit.proxy;

import com.common.module.cluster.annotation.RateLimit;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class RateLimitProxy<T> implements InvocationHandler {
    private final T target;
    private final Map<Method, RateLimiterService> rateLimiterMap = new HashMap<>();

    public RateLimitProxy(T target) {
        this.target = target;
        for (Method method : target.getClass().getMethods()) {
            if (method.isAnnotationPresent(RateLimit.class)) {
                RateLimit rateLimit = method.getAnnotation(RateLimit.class);
                RateLimiterService rateLimiterService = new RateLimiterService(rateLimit.permitsPerSecond());
                rateLimiterMap.put(method, rateLimiterService);
            }
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (rateLimiterMap.containsKey(method)) {
            RateLimiterService rateLimiterService = rateLimiterMap.get(method);
            if (!rateLimiterService.tryAcquire()) {
                throw new RuntimeException("请求过于频繁，请稍后再试");
            }
        }
        return method.invoke(target, args);
    }

    public static <T> T newProxyInstance(T target) {
        return (T) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new RateLimitProxy<>(target)
        );
    }

    public static void main(String[] args) {
//        Service service = new RealService();
//        Service limitedService = RateLimitProxy.newProxyInstance(service);

        for (int i = 0; i < 10; i++) {
            try {
//                limitedService.limitedMethod();
                Thread.sleep(100); // 模拟请求间隔
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}