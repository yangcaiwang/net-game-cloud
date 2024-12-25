package com.common.module.internal.cache.redission;

import com.common.module.internal.cache.redission.event.TopicEvent;
import com.common.module.internal.cache.redission.event.TopicMessage;
import com.common.module.internal.event.EventBusesImpl;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * <redission客户端工具类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class RedissonClient {
    private static Logger logger = LoggerFactory.getLogger(RedissonClient.class);

    /**
     * redisson配置
     */
    private Config yml;

    /**
     * 真正可以执行分布式redis(redisson)操作的客户端会话句柄
     */
    private org.redisson.api.RedissonClient redisson;

    private static RedissonClient redissonClient = new RedissonClient();

    public static RedissonClient getInstance() {
        return redissonClient;
    }

    public void start(String pathname) throws IOException {
        if (redisson == null) {
            Config config = createConfig(pathname);
            redisson = Redisson.create(config);
            Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        }
    }

    public Config createConfig(String pathname) throws IOException {
        if (yml != null)
            return yml;
        return yml = Config.fromYAML(new File(pathname));
    }

    /**
     * 异步发布消息
     *
     * @param message 消息
     */
    public <T extends TopicMessage> void publishTopic(T message) {
        try {
            RTopic topic = redisson.getTopic(message.getTopic());
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
    public void subscribeTopic(String... topicNames) {
        try {
            for (String topicName : topicNames) {
                RTopic topic = redisson.getTopic(topicName);
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

    private void stop() {
        try {
            if (redisson != null) {
                redisson.shutdown();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public org.redisson.api.RedissonClient getRedisson() {
        return redisson;
    }
}
