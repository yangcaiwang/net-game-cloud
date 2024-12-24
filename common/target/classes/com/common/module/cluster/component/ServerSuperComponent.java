package com.common.module.cluster.component;

import com.common.module.cluster.enums.ServerType;

public interface ServerSuperComponent {

    ServerType serverType();

    void init();

    void start();

    void stop();
}
