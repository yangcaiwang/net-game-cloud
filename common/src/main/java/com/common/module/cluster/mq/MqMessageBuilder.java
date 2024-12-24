package com.common.module.cluster.mq;

import com.common.module.cluster.mq.common.Constants;
import com.common.module.cluster.mq.common.DetailRes;
import com.common.module.cluster.mq.common.MessageWithTime;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import com.rabbitmq.client.ShutdownSignalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.ConsumerCancelledException;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.amqp.rabbit.support.DefaultMessagePropertiesConverter;
import org.springframework.amqp.rabbit.support.MessagePropertiesConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

public class MqMessageBuilder {

    public static final Logger log = LoggerFactory.getLogger(MqMessageBuilder.class);
    private static ConnectionFactory connectionFactory;

    public static void start() {
        Properties properties = new Properties();

        try {
            Resource res = new ClassPathResource("rabbitmq.properties");
            properties.load(res.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load rabbitmq.properties!");
        }

        String ip = properties.getProperty("ip");
        int port = Integer.parseInt(properties.getProperty("port"));
        String userName = properties.getProperty("user_name");
        String password = properties.getProperty("password");

        CachingConnectionFactory connectionFactory1 = new CachingConnectionFactory(ip, port);

        connectionFactory1.setUsername(userName);
        connectionFactory1.setPassword(password);
        connectionFactory1.setPublisherConfirms(true); // enable confirm mode

        connectionFactory = connectionFactory1;
    }

    public static MessagePublish publishDirect(final String exchange, final String routingKey, final String queue) throws IOException {
        return publish(exchange, routingKey, queue, Constants.DIRECT);
    }

    public static MessagePublish publishTopic(final String exchange, final String routingKey) throws IOException {
        return publish(exchange, routingKey, null, Constants.TOPIC);
    }

    //1 构造template, exchange, routingkey等
    //2 设置message序列化方法
    //3 设置发送确认
    //4 构造sender方法
    public static MessagePublish publish(final String exchange, final String routingKey,
                                         final String queue, final String type) throws IOException {
        Connection connection = connectionFactory.createConnection();
        //1
        buildQueue(exchange, routingKey, queue, connection, type);

        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setExchange(exchange);
        rabbitTemplate.setRoutingKey(routingKey);
        //2
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        RetryCache retryCache = new RetryCache();

        //3
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.info("send message failed: " + cause + correlationData.toString());
            } else {
                retryCache.del(Long.valueOf(correlationData.getId()));
            }
        });

        rabbitTemplate.setReturnCallback((message, replyCode, replyText, tmpExchange, tmpRoutingKey) -> {
            try {
                Thread.sleep(Constants.ONE_SECOND);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            log.info("send message failed: " + replyCode + " " + replyText);
            rabbitTemplate.send(message);
        });

        //4
        return new MessagePublish() {
            {
                retryCache.setSender(this);
            }

            @Override
            public DetailRes publish(Object message) {
                long id = retryCache.generateId();
                long time = System.currentTimeMillis();

                try {
                    MessageWithTime messageWithTime = new MessageWithTime(id, time, message);
                    retryCache.add(messageWithTime);
                    rabbitTemplate.correlationConvertAndSend(messageWithTime.getMessage(), new CorrelationData(String.valueOf(messageWithTime.getId())));
                } catch (Exception e) {
                    return new DetailRes(false, "");
                }

                return new DetailRes(true, "");
            }
        };
    }

    public static <T> MessageSubscribe subscribeDirect(String exchange, String routingKey, final String queue,
                                                       final MessageProcess<T> messageProcess) throws IOException {
        return subscribe(exchange, routingKey, queue, messageProcess, Constants.DIRECT);
    }

    public static <T> MessageSubscribe subscribeTopic(String exchange, String routingKey, final String queue,
                                                      final MessageProcess<T> messageProcess) throws IOException {
        return subscribe(exchange, routingKey, queue, messageProcess, Constants.TOPIC);
    }

    //1 创建连接和channel
    //2 设置message序列化方法
    //3 consume
    public static <T> MessageSubscribe subscribe(String exchange, String routingKey, final String queue,
                                                 final MessageProcess<T> messageProcess, String type) throws IOException {
        final Connection connection = connectionFactory.createConnection();

        //1
        buildQueue(exchange, routingKey, queue, connection, type);

        //2
        final MessagePropertiesConverter messagePropertiesConverter = new DefaultMessagePropertiesConverter();
        final MessageConverter messageConverter = new Jackson2JsonMessageConverter();

        //3
        return new MessageSubscribe() {
            Channel channel;

            {
                channel = connection.createChannel(false);
            }

            //1 通过basicGet获取原始数据
            //2 将原始数据转换为特定类型的包
            //3 处理数据
            //4 手动发送ack确认
            @Override
            public DetailRes subscribe() {
                try {
                    //1
                    GetResponse response = channel.basicGet(queue, false);

                    while (response == null) {
                        response = channel.basicGet(queue, false);
                        Thread.sleep(Constants.ONE_SECOND);
                    }

                    Message message = new Message(response.getBody(),
                            messagePropertiesConverter.toMessageProperties(response.getProps(), response.getEnvelope(), "UTF-8"));

                    //2
                    @SuppressWarnings("unchecked")
                    T messageBean = (T) messageConverter.fromMessage(message);

                    //3
                    DetailRes detailRes;

                    try {
                        detailRes = messageProcess.process(messageBean);
                    } catch (Exception e) {
                        log.error("exception", e);
                        detailRes = new DetailRes(false, "process exception: " + e);
                    }

                    //4
                    if (detailRes.isSuccess()) {
                        channel.basicAck(response.getEnvelope().getDeliveryTag(), false);
                    } else {
                        //避免过多失败log
                        Thread.sleep(Constants.ONE_SECOND);
                        log.info("process message failed: " + detailRes.getErrMsg());
                        channel.basicNack(response.getEnvelope().getDeliveryTag(), false, true);
                    }

                    return detailRes;
                } catch (InterruptedException e) {
                    log.error("exception", e);
                    return new DetailRes(false, "interrupted exception " + e.toString());
                } catch (ShutdownSignalException | ConsumerCancelledException | IOException e) {
                    log.error("exception", e);

                    try {
                        channel.close();
                    } catch (IOException | TimeoutException ex) {
                        log.error("exception", ex);
                    }

                    channel = connection.createChannel(false);

                    return new DetailRes(false, "shutdown or cancelled exception " + e.toString());
                } catch (Exception e) {
                    log.info("exception : ", e);

                    try {
                        channel.close();
                    } catch (IOException | TimeoutException ex) {
                        ex.printStackTrace();
                    }

                    channel = connection.createChannel(false);

                    return new DetailRes(false, "exception " + e.toString());
                }
            }
        };
    }

    private static void buildQueue(String exchange, String routingKey,
                                   final String queue, Connection connection, String type) throws IOException {
        Channel channel = connection.createChannel(false);

        if (type.equals(Constants.DIRECT)) {
            channel.exchangeDeclare(exchange, Constants.DIRECT, true, false, null);
            channel.queueDeclare(queue, true, false, false, null);
            channel.queueBind(queue, exchange, routingKey);
        } else if (type.equals(Constants.TOPIC)) {
            channel.exchangeDeclare(exchange, Constants.TOPIC, true, false, null);
        }

        try {
            channel.close();
        } catch (TimeoutException e) {
            log.info("close channel time out ", e);
        }
    }

    //for test
    public static int getMessageCount(final String queue) throws IOException {
        Connection connection = connectionFactory.createConnection();
        final Channel channel = connection.createChannel(false);
        final AMQP.Queue.DeclareOk declareOk = channel.queueDeclarePassive(queue);

        return declareOk.getMessageCount();
    }
}
