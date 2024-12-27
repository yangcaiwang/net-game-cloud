package com.ycw.core.network.jetty.handler;

import com.ycw.core.internal.loader.service.ServiceContext;
import com.ycw.core.network.jetty.command.SuperHttpCommand;
import com.ycw.core.network.jetty.http.HttpCode;
import com.ycw.core.network.jetty.http.HttpSession;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * <Jetty处理器实现类>
 * <p>
 * ps: 监听http请求
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class JettyHttpHandler extends AbstractHandler {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String url = request.getRequestURI();
            Map<String, String> params = new HashMap<>();
            String action = "";
            if (url.contains("do")) {
                String[] arr = url.split("/");
                String[] actions = arr[arr.length - 1].split("[.]");
                action = actions[0].trim();
                analyticalParameters(params, baseRequest, request);
            } else {
                jettyParam(params, request);
            }
            log.debug("action:" + action);
            SuperHttpCommand service = ServiceContext.getInstance().get(action);
            HttpSession httpSession = new HttpSession(request, response, action, params);
            if (service != null) {
                service.running(httpSession);
            } else {
                httpSession.sendHttpResponseError(HttpCode.METHOD_WRONG);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            String s = "{\"code\":" +
                    HttpCode.SYSTEM_ERROR.getIndex() + "," +
                    "\"message\":" +
                    "\"" + URLEncoder.encode(HttpCode.SYSTEM_ERROR.getName(), "utf-8") + "\"}";
            response.getWriter().write(s);
            response.getWriter().flush();
        }
    }

    private void jettyParam(Map<String, String> params, HttpServletRequest request) {
        Enumeration<String> nameEn = request.getParameterNames();
        while (nameEn.hasMoreElements()) {
            String k = nameEn.nextElement();
            params.put(k, request.getParameter(k));
        }
    }

    private void analyticalParameters(Map<String, String> params, Request baseRequest, HttpServletRequest request) throws IOException {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            InputStream is = request.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder data = new StringBuilder();
            String s = null;
            while ((s = reader.readLine()) != null) {
                data.append(s);
            }
            string2Map(params, data.toString());
        } else {
            String url = baseRequest.getHttpURI().toString();
            String[] par = url.split("[?]");
            string2Map(params, par.length > 1 ? par[1] : null);
        }
    }

    private void string2Map(Map<String, String> params, String data) {
        if (data != null) {
            String[] par = data.split("&");
            for (String string : par) {
                int length = string.length();
                if (length > 0) {
                    int index = string.indexOf("=");
                    String c = string.substring(0, index);
                    String v = string.substring(index + 1, length);
                    params.put(c, v);
                }
            }
        }
    }
}
