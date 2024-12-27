
package com.ycw.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * <序列化工具类>
 * <p>
 * ps:
 * 对象序列化工具,必须是实现Serializable接口的对象,不需要序列化的字段加修饰符transient</br>
 * 如果用json序列化,对象必须要有无参构造器,所有字段必须要有getter,setter方法,也就是标准的bean!!!,
 * 如果有不需要序列化的字段和方法需要加上@JSONField(serialize=false) </br>
 * 转二进制过程中缓冲区如果不填默认使用256作为初始值,根据需求自己预估是比较科学的</br>
 * !!! 请务必使用hessian2作为二进制序列化/反序列化,fastjson作为字符串序列化/反序列化</br>
 * FST, 10倍于JDK序列化性能而且100%兼容的编码,是新增的工具,目前没有使用,暂时不要使用以免造成序列化和反序列化不一致问题</br>
 * 经测试,序列化1000个对象hessian2序列化耗时41毫秒,反序列化9ms</br>
 * 经测试,序列化1000个对象FST序列化耗时136毫秒,反序列化4ms</br>
 * hessian2性能较均衡,FST反序列化性能更高</br>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class SerializationUtils {

    /**
     * 自定义序列化接口
     *
     * @author lijishun
     */
    public static interface MySerializable extends java.io.Externalizable {

    }

    /************************************************** fastjson ***************************************************/

    private static final SerializerFeature[] FEATURES = new SerializerFeature[]{SerializerFeature.WriteClassName, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteEnumUsingToString};

    public static <T> String beanToJson(T bean, SerializerFeature... features) {
        try {
            if (features == null || features.length < 1)
                features = FEATURES;
            return JSON.toJSONString(bean, features);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static <T> T jsonToBean(String json, Class<T> clazz) {
        try {
            return JSON.parseObject(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static <T> T jsonToBean(String json, Type type) {

        try {
            return JSON.parseObject(json, type);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static <T> T jsonToBean(String json, TypeReference<T> type) {

        try {
            return JSON.parseObject(json, type);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static <T> T jsonToBean(InputStream is, Type type) {

        try {
            return JSON.parseObject(is, type);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**************************************************
     * hessian2 序列化/反序列化
     ***************************************************/
    public static <T extends Serializable> T copyByH2(T obj, int initBufSize) {

        try {
            return toObjectByH2(toByteArrayByH2(obj, initBufSize));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static <T extends Serializable> T copyByH2(T obj) {

        return copyByH2(obj, 256);
    }

    public static <T extends Serializable> byte[] toByteArrayByH2(T obj, int initBufSize) {

        Hessian2Output h2o = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(initBufSize);
            h2o = new Hessian2Output(baos);
            h2o.writeObject(obj);
            h2o.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (h2o != null) {
                try {
                    h2o.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static <T extends Serializable> byte[] toByteArrayByH2(T obj) {

        return toByteArrayByH2(obj, 256);
    }

    public static <T extends Serializable> T toObjectByH2(byte[] bytes) {

        Hessian2Input h2i = null;
        try {
            h2i = new Hessian2Input(new ByteArrayInputStream(bytes));
            return (T) h2i.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (h2i != null) {
                try {
                    h2i.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
