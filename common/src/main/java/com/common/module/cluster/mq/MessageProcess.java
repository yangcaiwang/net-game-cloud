package com.common.module.cluster.mq;

import com.common.module.cluster.mq.common.DetailRes;

public interface MessageProcess<T> {
    DetailRes process(T message);
}
