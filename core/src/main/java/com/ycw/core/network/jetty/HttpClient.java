package com.ycw.core.network.jetty;

import com.ycw.core.network.jetty.constant.HttpConstant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Vector;

/**
 * <http客户端工具类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class HttpClient {

    private final String DEFAULT_CONTENT_ENCODING = "UTF-8";

    private final int CONNECT_TIME_OUT = 3000;

    private final int READ_TIME_OUT = 5000;

    private static HttpClient httpClient = new HttpClient();

    public static HttpClient getInstance() {
        return httpClient;
    }

    /**
     * 发送GET请求
     *
     * @param address    服务器地址
     * @param httpCmd    http请求命令
     * @param params     参数集合
     * @param properties 请求属性
     * @return HttpResponse 响应对象
     */
    public HttpResponse sendGet(String address, String httpCmd, Map<String, String> params, Map<String, String> properties) throws IOException {
        StringBuilder url = new StringBuilder();
        url.append(HttpConstant.HTTP_PREFIX).append(address).append(httpCmd);
        return this.send(url.toString(), "GET", params, properties);
    }

    /**
     * 发送GET请求
     *
     * @param urlString  url地址
     * @param params     参数集合
     * @param properties 请求属性
     * @return HttpResponse 响应对象
     */
    public HttpResponse sendGet(String urlString, Map<String, String> params, Map<String, String> properties) throws IOException {
        return this.send(urlString, "GET", params, properties);
    }

    /**
     * 发送POST请求
     *
     * @param address    服务器地址
     * @param httpCmd    http请求命令
     * @param params     参数集合
     * @param properties 请求属性
     * @return HttpResponse 响应对象
     */
    public HttpResponse sendPost(String address, String httpCmd, Map<String, String> params, Map<String, String> properties) throws IOException {
        StringBuilder url = new StringBuilder();
        url.append(HttpConstant.HTTP_PREFIX).append(address).append(httpCmd);
        return this.send(url.toString(), "POST", params, properties);
    }

    /**
     * 发送POST请求
     *
     * @param urlString  url地址
     * @param params     参数集合
     * @param properties 请求属性
     * @return HttpResponse 响应对象
     */
    public HttpResponse sendPost(String urlString, Map<String, String> params, Map<String, String> properties) throws IOException {
        return this.send(urlString, "POST", params, properties);
    }

    /**
     * 发送HTTP请求
     *
     * @param urlString URL地址
     * @return 响映对象
     */
    private HttpResponse send(String urlString, String method, Map<String, String> parameters,
                              Map<String, String> properties) throws IOException {
        HttpURLConnection urlConnection = null;

        if (method.equalsIgnoreCase("GET") && parameters != null) {
            StringBuffer param = new StringBuffer();
            int i = 0;
            for (String key : parameters.keySet()) {
                if (i == 0)
                    param.append("?");
                else
                    param.append("&");
                param.append(key).append("=").append(parameters.get(key));
                i++;
            }
            urlString += param;
        }
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod(method);
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.setUseCaches(false);
        // 连接超时 设置为3秒
        urlConnection.setConnectTimeout(CONNECT_TIME_OUT);
        // 读取超时 5秒
        urlConnection.setReadTimeout(READ_TIME_OUT);

        if (properties != null)
            for (String key : properties.keySet()) {
                urlConnection.addRequestProperty(key, properties.get(key));
            }

        if (method.equalsIgnoreCase("POST") && parameters != null) {
            StringBuilder param = new StringBuilder();
            for (String key : parameters.keySet()) {
                param.append("&");
                param.append(key).append("=").append(parameters.get(key));
            }
            urlConnection.getOutputStream().write(param.toString().getBytes());
            urlConnection.getOutputStream().flush();
            urlConnection.getOutputStream().close();
        }
        return this.makeContent(urlString, urlConnection);
    }

    /**
     * 得到响应对象
     */
    private HttpResponse makeContent(String urlString, HttpURLConnection urlConnection) throws IOException {
        HttpResponse httpResponse = new HttpResponse();
        try {
            urlConnection.connect();
            InputStream in = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            httpResponse.contentCollection = new Vector<String>();
            StringBuffer temp = new StringBuffer();
            String line = bufferedReader.readLine();
            while (line != null) {
                httpResponse.contentCollection.add(line);
                temp.append(line).append("\r\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();

            String ecod = urlConnection.getContentEncoding();
            if (ecod == null)
                ecod = this.DEFAULT_CONTENT_ENCODING;
            httpResponse.urlString = urlString;
            httpResponse.go = urlConnection.getHeaderField("MyHeaderData");
            httpResponse.defaultPort = urlConnection.getURL().getDefaultPort();
            httpResponse.file = urlConnection.getURL().getFile();
            httpResponse.host = urlConnection.getURL().getHost();
            httpResponse.path = urlConnection.getURL().getPath();
            httpResponse.port = urlConnection.getURL().getPort();
            httpResponse.protocol = urlConnection.getURL().getProtocol();
            httpResponse.query = urlConnection.getURL().getQuery();
            httpResponse.ref = urlConnection.getURL().getRef();
            httpResponse.userInfo = urlConnection.getURL().getUserInfo();
            httpResponse.content = new String(temp.toString().getBytes());
            httpResponse.contentEncoding = ecod;
            httpResponse.code = urlConnection.getResponseCode();
            httpResponse.message = urlConnection.getResponseMessage();
            httpResponse.contentType = urlConnection.getContentType();
            httpResponse.method = urlConnection.getRequestMethod();
            httpResponse.connectTimeout = urlConnection.getConnectTimeout();
            httpResponse.readTimeout = urlConnection.getReadTimeout();

            return httpResponse;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    public class HttpResponse {
        String urlString;

        int defaultPort;

        String file;

        String go;

        String host;

        String path;

        int port;

        String protocol;

        String query;

        String ref;

        String userInfo;

        String contentEncoding;

        String content;

        String contentType;

        int code;

        String message;

        String method;

        int connectTimeout;

        int readTimeout;

        Vector<String> contentCollection;

        public String getContent() {
            return content;
        }

        public String getContentType() {
            return contentType;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public Vector<String> getContentCollection() {
            return contentCollection;
        }

        public String getContentEncoding() {
            return contentEncoding;
        }

        public String getMethod() {
            return method;
        }

        public int getConnectTimeout() {
            return connectTimeout;
        }

        public int getReadTimeout() {
            return readTimeout;
        }

        public String getUrlString() {
            return urlString;
        }

        public int getDefaultPort() {
            return defaultPort;
        }

        public String getFile() {
            return file;
        }

        public String getHost() {
            return host;
        }

        public String getPath() {
            return path;
        }

        public int getPort() {
            return port;
        }

        public String getProtocol() {
            return protocol;
        }

        public String getQuery() {
            return query;
        }

        public String getRef() {
            return ref;
        }

        public String getUserInfo() {
            return userInfo;
        }

        public String getGo() {
            return go;
        }
    }
}
