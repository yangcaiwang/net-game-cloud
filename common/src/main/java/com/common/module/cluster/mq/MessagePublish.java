package com.common.module.cluster.mq;

import com.common.module.cluster.mq.common.DetailRes;

public interface MessagePublish {
    DetailRes publish(Object message);
}
