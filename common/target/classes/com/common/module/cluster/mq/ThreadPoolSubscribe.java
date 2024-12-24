package com.common.module.cluster.mq;

import com.common.module.cluster.mq.common.Constants;
import com.common.module.cluster.mq.common.DetailRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolSubscribe<T> {

    public static final Logger log = LoggerFactory.getLogger(MqMessageBuilder.class);
    private ExecutorService executor;
    private volatile boolean stop = false;
    private final ThreadPoolConsumerBuilder<T> infoHolder;

    //构造器
    public static class ThreadPoolConsumerBuilder<T> {
        int threadCount;
        long intervalMils;
        MqMessageBuilder MqMessageBuilder;
        String exchange;
        String routingKey;
        String queue;
        String type;
        MessageProcess<T> messageProcess;

        public ThreadPoolConsumerBuilder<T> setThreadCount(int threadCount) {
            this.threadCount = threadCount;

            return this;
        }

        public ThreadPoolConsumerBuilder<T> setIntervalMils(long intervalMils) {
            this.intervalMils = intervalMils;

            return this;
        }

        public ThreadPoolConsumerBuilder<T> setMQAccessBuilder(MqMessageBuilder MqMessageBuilder) {
            this.MqMessageBuilder = MqMessageBuilder;

            return this;
        }

        public ThreadPoolConsumerBuilder<T> setExchange(String exchange) {
            this.exchange = exchange;

            return this;
        }

        public ThreadPoolConsumerBuilder<T> setRoutingKey(String routingKey) {
            this.routingKey = routingKey;

            return this;
        }

        public ThreadPoolConsumerBuilder<T> setQueue(String queue) {
            this.queue = queue;

            return this;
        }

        public ThreadPoolConsumerBuilder<T> setType(String type) {
            this.type = type;

            return this;
        }

        public ThreadPoolConsumerBuilder<T> setMessageProcess(MessageProcess<T> messageProcess) {
            this.messageProcess = messageProcess;

            return this;
        }

        public ThreadPoolSubscribe<T> build() {
            return new ThreadPoolSubscribe<T>(this);
        }
    }

    private ThreadPoolSubscribe(ThreadPoolConsumerBuilder<T> threadPoolConsumerBuilder) {
        this.infoHolder = threadPoolConsumerBuilder;
        executor = Executors.newFixedThreadPool(threadPoolConsumerBuilder.threadCount);
    }

    //1 构造messageConsumer
    //2 执行consume
    public void start() throws IOException {
        for (int i = 0; i < infoHolder.threadCount; i++) {
            //1
            final MessageSubscribe messageSubscribe = infoHolder.MqMessageBuilder.subscribe(infoHolder.exchange,
                    infoHolder.routingKey, infoHolder.queue, infoHolder.messageProcess, infoHolder.type);

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    while (!stop) {
                        try {
                            //2
                            DetailRes detailRes = messageSubscribe.subscribe();

                            if (infoHolder.intervalMils > 0) {
                                try {
                                    Thread.sleep(infoHolder.intervalMils);
                                } catch (InterruptedException e) {
                                    log.info("interrupt ", e);
                                }
                            }

                            if (!detailRes.isSuccess()) {
                                log.info("run error " + detailRes.getErrMsg());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.info("run exception ", e);
                        }
                    }
                }
            });
        }

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    public void stop() {
        this.stop = true;

        try {
            Thread.sleep(Constants.ONE_SECOND);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
