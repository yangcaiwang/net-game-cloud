
package com.ycw.core.util;

import com.google.common.collect.Sets;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * <注解工具类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class AnnotationUtil {

    public static boolean isAnnotation(Class<?> clz, Class<? extends Annotation> annoClz) {

        return clz.isAnnotationPresent(annoClz);
    }

    public static boolean isAnnotation(Method method, Class<? extends Annotation> annoClz) {

        return method.isAnnotationPresent(annoClz);
    }

    public static boolean isAnnotation(Field field, Class<? extends Annotation> annoClz) {

        return field.isAnnotationPresent(annoClz);
    }

    public static <A extends Annotation> A findAnnotation(Class<?> clz, Class<? extends Annotation> annoClz) {

        return (A) clz.getAnnotation(annoClz);
    }

    public static <A extends Annotation> A findAnnotation(Method method, Class<? extends Annotation> annoClz) {

        return (A) method.getAnnotation(annoClz);
    }

    public static <A extends Annotation> A findAnnotation(Field field, Class<? extends Annotation> annoClz) {

        return (A) field.getAnnotation(annoClz);
    }

    /**
     * 深度查找类注解,包括注解的注解,全部父类,接口的注解
     */
    public static <A extends Annotation> A deepFindAnnotation(Class<?> clz, Class<? extends Annotation> annoClz) {

        Set<Annotation> annotations = getAllAnnotations(clz);
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == annoClz)
                return (A) annotation;
        }
        return null;
    }

    /**
     * 深度查找方法注解,包括注解的注解
     */
    public static <A extends Annotation> A deepFindAnnotation(Method method, Class<? extends Annotation> annoClz) {

        Set<Annotation> annotations = getAllAnnotations(method);
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == annoClz)
                return (A) annotation;
        }
        return null;
    }

    /**
     * 深度查找变量注解,包括注解的注解
     */
    public static <A extends Annotation> A deepFindAnnotation(Field field, Class<? extends Annotation> annoClz) {

        Set<Annotation> annotations = getAllAnnotations(field);
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == annoClz)
                return (A) annotation;
        }
        return null;
    }

    public static Set<Annotation> getAllAnnotations(Class<?> clz) {

        Set<Annotation> allAnnotations = Sets.newHashSet();
        Set<Class<?>> classes = ClassUtils.getAllClasses(clz);
        for (Class<?> cls : classes) {
            Annotation[] annotations = cls.getDeclaredAnnotations();
            if (!CollectionUtils.isEmpty(annotations)) {
                for (Annotation annotation : annotations) {
                    Set<Annotation> annotations3 = getAllAnnotations(annotation);
                    allAnnotations.addAll(annotations3);
                }
            }
        }
        return allAnnotations;
    }

    public static Set<Annotation> getAllAnnotations(Method method) {

        Set<Annotation> allAnnotations = Sets.newHashSet();
        Annotation[] annotations = method.getDeclaredAnnotations();
        if (!CollectionUtils.isEmpty(annotations)) {
            for (Annotation annotation : annotations) {
                Set<Annotation> annotations3 = getAllAnnotations(annotation);
                allAnnotations.addAll(annotations3);
            }
        }
        return allAnnotations;
    }

    public static Set<Annotation> getAllAnnotations(Field field) {

        Set<Annotation> allAnnotations = Sets.newHashSet();
        Annotation[] annotations = field.getDeclaredAnnotations();
        if (!CollectionUtils.isEmpty(annotations)) {
            for (Annotation annotation : annotations) {
                Set<Annotation> annotations3 = getAllAnnotations(annotation);
                allAnnotations.addAll(annotations3);
            }
        }
        return allAnnotations;
    }

    public static Set<Annotation> getAllAnnotations(Annotation annotation) {

        Set<Annotation> annotations = Sets.newHashSet();
        allAnnotations(annotation, annotations);
        return annotations;
    }

    public static void allAnnotations(Annotation annotation, Set<Annotation> annotations) {

        if (annotations == null)
            annotations = Sets.newHashSet();
        if (annotations.contains(annotation))
            return;
        annotations.add(annotation);
        Annotation[] annotations2 = annotation.annotationType().getDeclaredAnnotations();
        if (!CollectionUtils.isEmpty(annotations2)) {
            for (Annotation annotation3 : annotations2) {
                allAnnotations(annotation3, annotations);
            }
        }
    }
}
