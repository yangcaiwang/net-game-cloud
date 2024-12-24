package com.common.module.internal.cache.redission;

import com.common.module.internal.event.EventBusesImpl;
import com.common.module.internal.cache.redission.topic.TopicEvent;
import com.common.module.internal.cache.redission.topic.TopicMessage;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * redisson客户端操作者
 * </p>
 * cd F:\work-life
 * </p>
 * 安装命令：redis-server.exe --service-install redis.windows.conf --loglevel verbose
 * </p>
 * <p>
 * 启动服务命令：redis-server.exe --service-start
 * </p>
 * <p>
 * 关闭服务命令：redis-server.exe --service-stop
 * </p>
 *
 * @author yangcaiwang
 */
public class RedissonClient {

    /**
     * redisson配置
     */
    private static Config yml;

    /**
     * 真正可以执行分布式redis(redisson)操作的客户端会话句柄
     */
    private static org.redisson.api.RedissonClient redisson;

    private static Logger logger = LoggerFactory.getLogger(RedissonClient.class);

    public static Config createConfig(String pathname) throws IOException {
        if (yml != null)
            return yml;
        return yml = Config.fromYAML(new File(pathname));
    }

    public static org.redisson.api.RedissonClient start(Config config) {
        if (redisson == null)
            redisson = Redisson.create(config);
        return redisson;
    }

    public static void start(String pathname) throws IOException {
        if (redisson == null) {
            redisson = start(createConfig(pathname));
        }
    }

    /**
     * 异步发布消息
     *
     * @param message 消息
     */
    public static <T extends TopicMessage> void publishTopic(T message) {
        try {
            RTopic topic = redisson().getTopic(message.getTopic());
            topic.publishAsync(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 异步订阅消息
     *
     * @param topicNames 话题
     */
    public static void subscribeTopic(String... topicNames) {
        try {
            for (String topicName : topicNames) {
                RTopic topic = redisson().getTopic(topicName);
                topic.addListener(TopicMessage.class, (channel, message) -> {
//                    // 处理接收到的消息
                    EventBusesImpl.getInstance().asyncPublish(channel, new TopicEvent(message));
                });
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * 获取连接redis的redisson句柄
     */
    public static org.redisson.api.RedissonClient redisson() {
        return redisson;
    }

    public static void shutdown() {
        redisson.shutdown();
    }
}
