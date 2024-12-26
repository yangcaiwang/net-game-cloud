package com.common.module.network.jetty.http;

/**
 * <http响应结果类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class HttpResult {

    private String code;

    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
