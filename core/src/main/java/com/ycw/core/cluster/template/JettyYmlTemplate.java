package com.ycw.core.cluster.template;

/**
 * <jetty解析yml模版类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class JettyYmlTemplate extends AbstractYmlTemplate {
    /**
     * 最小线程数
     */
    private int httpMinThreads;

    /**
     * 最大线程数
     */
    private int httpMaxThreads;

    /**
     * 空闲时间
     */
    private int idleTimeout;

    public int getHttpMinThreads() {
        return httpMinThreads;
    }

    public void setHttpMinThreads(int httpMinThreads) {
        this.httpMinThreads = httpMinThreads;
    }

    public int getHttpMaxThreads() {
        return httpMaxThreads;
    }

    public void setHttpMaxThreads(int httpMaxThreads) {
        this.httpMaxThreads = httpMaxThreads;
    }

    public int getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(int idleTimeout) {
        this.idleTimeout = idleTimeout;
    }
}
