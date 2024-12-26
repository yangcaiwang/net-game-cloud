package com.gm.server.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.module.cluster.property.PropertyConfig;
import com.common.module.internal.thread.NamedThreadFactory;
import com.gm.server.common.utils.crypto.CryptoUtils;
import com.gm.server.common.utils.http.HttpUtils;
import com.gm.server.common.utils.ssl.RSAUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

public class ParamParseUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParamParseUtils.class);
    private static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("paramParse-scheduler"));
    public static String makeURL(String host, int port) {
        return String.format("https://%s:%d", host, port);
    }

    public static String makeURL(String host, int port, String method) {
        return String.format("http://%s:%d/%s", host, port, method);
    }

    public static void sendAsyncPost(String uri, Map<String, Object> map) throws IOException {
        sendAsyncPost(uri, map, null, "utf-8");
    }

    public static void sendAsyncPost(String uri, Map<String, Object> map, Map<String, String> headers, String charset) throws IOException {
        sendAsyncPost(uri, mergedParamsString(map, charset), headers);
    }

    public static String sendSyncPost(String uri, Map<String, Object> map) throws IOException {
        return sendSyncPost(uri, map, null, "utf-8");
    }

    public static String sendSyncPost(String uri, Map<String, Object> map, Map<String, String> headers, String charset) throws IOException {
        return sendSyncPost(uri, mergedParamsString(map, charset));
    }

    public static String sendSyncTokenPost(String uri, Map<String, Object> map) throws IOException {
        return sendSyncPost(uri, mergedParamsAddToken(map, "utf-8"));
    }

    public static String sendSyncGet(String uri, Map<String, Object> map, String charset) throws IOException {
        return HttpUtils.sendGet(uri, mergedParamsString(map, charset));
    }

    public static String sendRSASyncPost(String url, Map<String, Object> map) throws IOException {
        return sendSyncPost(url, mergedRSAParamsString(map));
    }

    public static void sendRSAAsyncPost(String url, Map<String, Object> map) throws IOException {
        sendAsyncPost(url, mergedRSAParamsString(map), null);
    }

    public static String sendSyncPost(String url, String content) throws IOException {
        if (PropertyConfig.isDebug()) {
            LOGGER.info("url:{} 请求信息：{}", url, content);
        }
        String s = HttpUtils.sendPost(url, content);
        return s;
    }

    /**
     * 键值对包装的参数只用urlencode并合并成string拼接到url连接去
     *
     * @param map
     * @param charset
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String mergedParamsString(Map<String, Object> map, String charset) throws UnsupportedEncodingException {
        if (map == null || map.isEmpty())
            return "";
        StringBuilder sb = new StringBuilder();
        // 构建请求参数
        int count = 0;
        for (Map.Entry<String, Object> e : map.entrySet()) {
            // sb.append(e.getKey());
            sb.append(encode(e.getKey(), charset));
            sb.append("=");
            // sb.append(e.getValue());
            sb.append(encode(String.valueOf(e.getValue()), charset));
            if (count++ < map.size() - 1)
                sb.append("&");
        }
        return sb.toString();
    }

    public static String mergedParamsAddToken(Map<String, Object> map, String charset) throws UnsupportedEncodingException {
        if (map == null || map.isEmpty())
            return "";
        StringBuilder sb = new StringBuilder();
        // 构建请求参数
        int count = 0;
        for (Map.Entry<String, Object> e : map.entrySet()) {
            // sb.append(e.getKey());
            sb.append(encode(e.getKey(), charset));
            sb.append("=");
            // sb.append(e.getValue());
            sb.append(encode(String.valueOf(e.getValue()), charset));
            if (count++ < map.size() - 1)
                sb.append("&");
        }
        if (map.containsKey("cmd")) {
            String key = PropertyConfig.getString("game.check.token.key", "");
            String s = CryptoUtils.encodeMD5(key);
            sb.append("&token=").append(s);
        }
        return sb.toString();
    }

    public static String encode(String content, String charset) throws UnsupportedEncodingException {
        return URLEncoder.encode(content, charset);
    }

    public static String decode(String content, String charset) throws UnsupportedEncodingException {
        return URLDecoder.decode(content, charset);
    }

    public static void sendAsyncPost(String url, String content, Map<String, String> headers) throws IOException {
        executor.execute(new TimerTask() {
            @Override
            public void run() {
                try {
                    String string = HttpUtils.sendPost(url, content);
                    JSONObject jsonObject = (JSONObject) JSON.parse(string);
                    String code = jsonObject.getString("Code");
                    if (PropertyConfig.isDebug()) {
                        LOGGER.info("url:{} 请求信息：{}", url, content);
                    }
                    if (!"200".equals(code)) {
                        String msg = jsonObject.getString("Msg");
                        if (msg != null) {
                            LOGGER.error("url:{} 上传数据：{} 错误信息：{}", url, content, msg);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("url:{} 上传数据：{} 异常信息：{} ", url, content, e.getMessage());
                }
            }
        });

    }

    /**
     * 键值对包装的参数只用urlencode并合并成string拼接到url连接去
     *
     * @param map
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String mergedRSAParamsString(Map<String, Object> map) throws UnsupportedEncodingException {
        if (map == null || map.isEmpty()) {
            return "";
        }
        // 构建请求参数
        JSONObject object = new JSONObject(map);
        return "params=" + RSAUtils.encrypt(object.toJSONString());
    }

    public static Map<String, Object> parsedRSAParamsString(String content) throws Exception {
        Map<String, Object> map = new HashMap<>();
        String[] ss = content.split("&");
        for (String s : ss) {
            String[] kv = s.split("=");
            if ("params".equals(kv[0])) {
                map.putAll(parsedRSAString(kv[1]));
            } else {
                if (kv.length > 1) {
                    map.put(kv[0], kv[1]);
                } else {
                    map.put(kv[0], "");
                }
            }
        }
        return map;
    }

    public static Map<String, Object> parsedRSAString(String content) throws Exception {
        Map<String, Object> map = new HashMap<>();
        String cont = RSAUtils.decrypt(content);
        JSONObject parse = (JSONObject) JSON.parse(cont);
        for (String key : parse.keySet()) {
            Object o = parse.get(key);
            if (o instanceof JSONArray) {
                String collect = (String) ((JSONArray) o).stream().map(v -> {
                    String str = String.valueOf(v);
                    return str.replace("=>", "|");
                }).collect(Collectors.joining("|"));
                map.put(key, collect);
            } else if (o instanceof JSONObject) {
                String collect = ((JSONObject) o).entrySet().stream().map(v -> v.getKey() + "|" + v.getValue()).collect(Collectors.joining("|"));
                map.put(key, collect);
            } else {
                map.put(key, o);
            }
        }
        return map;
    }

    public static Map<String, Object> getParamsMap(HttpServletRequest req) throws Exception {
        String param = req.getParameter("params");
        Map<String, Object> map = null;
        if (Strings.isEmpty(param)) {
            String context = getUTF8Body(req);
            if (!Strings.isEmpty(context)) {
                map = ParamParseUtils.parsedRSAParamsString(context);
            }
        } else {
            map = ParamParseUtils.parsedRSAString(param);
        }
        return map;
    }

    public static String getUTF8Body(HttpServletRequest req) throws Exception {
        String s = new String(HttpUtils.getBytesBoby(req, 256), StandardCharsets.UTF_8);
        return URLDecoder.decode(s, "utf-8");
    }
}
