package com.ycw.core.internal.cache.redis;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <redis客户端工具类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
@Component
public class RedisUtil {

    private static RedisTemplate<String, String> redisTemplate;

    public static void createRedisCli(
            String host,
            int port,
            String password,
            int database,
            int maxActive,
            long maxWait,
            int maxIdle,
            int minIdle
    ) {
        //redis配置
        RedisConfiguration redisConfiguration = new RedisStandaloneConfiguration(host, port);
        ((RedisStandaloneConfiguration) redisConfiguration).setDatabase(database);
        ((RedisStandaloneConfiguration) redisConfiguration).setPassword(password);

        //连接池配置
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxTotal(maxActive);
        genericObjectPoolConfig.setMaxWaitMillis(maxWait);
        genericObjectPoolConfig.setMaxIdle(maxIdle);
        genericObjectPoolConfig.setMinIdle(minIdle);

        //redis客户端配置
        LettuceClientConfiguration lettuceClientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(genericObjectPoolConfig).build();
        //根据配置和客户端配置创建连接
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisConfiguration, lettuceClientConfiguration);
        lettuceConnectionFactory.afterPropertiesSet();

        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(lettuceConnectionFactory);

        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setDefaultSerializer(jackson2JsonRedisSerializer);
        redisTemplate = template;
    }

    /**
     * 判断是否存在key
     *
     * @param key key
     * @return
     */
    public static boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * redis赋值
     *
     * @param key        key
     * @param value      value
     * @param expireTime 过期时间（单位为 秒）
     */
    public static void setString(String key, String value, long expireTime) {
        redisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.SECONDS);
    }

    /**
     * redis赋值-无过期时间
     *
     * @param key   key
     * @param value value
     */
    public static void setString(String key, String value) {
        setString(key, value, -1);
    }

    /**
     * 获取redis值
     *
     * @param key key
     * @return
     */
    public static String getString(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 获取redis内容
     *
     * @param key
     * @param clzz
     * @param <T>
     * @return
     */
    public static <T> T getData(String key, Class<T> clzz) {
        String value = redisTemplate.opsForValue().get(key);
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return JSONObject.parseObject(value, clzz);
    }

    /**
     * 获取list集合
     *
     * @param key  redis key
     * @param clzz class对象
     * @param <T>  返回类型
     * @return
     */
    public static <T> List<T> getList(String key, Class<T> clzz) {
        String value = redisTemplate.opsForValue().get(key);
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return JSONArray.parseArray(value, clzz);
    }
}