package com.ycw.core.cluster.template;

import java.io.Serializable;

/**
 * <解析yml模版抽象类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public abstract class AbstractYmlTemplate implements Serializable {

    /**
     * 端口号
     */
    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
