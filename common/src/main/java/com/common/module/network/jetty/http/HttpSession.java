package com.common.module.network.jetty.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * <http会话实现类>
 * <p>
 * ps: 封装
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class HttpSession {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private String action;
    private Map<String, String> parameters;

    public HttpSession(HttpServletRequest request, HttpServletResponse response, String action,
                       Map<String, String> parameters) throws Exception {
        this.request = request;
        this.response = response;
        this.action = action;
        this.parameters = parameters;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public String getRemoteAddr() {
        return request.getRemoteAddr();
    }

    public void sendHttpResponseError(HttpCode httpCode) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("{\"code\":");
            sb.append(httpCode.getIndex() + ",");
            sb.append("\"message\":");
            sb.append("\"" + URLEncoder.encode(httpCode.getName(), "utf-8") + "\"}");
            String s = sb.toString();
            response.getWriter().write(s);
            response.getWriter().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendHttpResponse(String message) {
        StringBuffer sb = new StringBuffer();
        sb.append("{\"code\":");
        sb.append(HttpCode.SUCCESS.getIndex()).append(",");
        sb.append("\"message\":");
        sb.append("\"").append(message).append("\"}");
        String s = sb.toString();
        try {
            response.getWriter().write(s);
            response.getWriter().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> T getParameters(String key, Class<T> clazz) {
        return parameters.get(key) == null ? null : (T) parameters.get(key);
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    public String getAction() {
        return action;
    }

}
