package com.common.module.network.jetty;

import com.common.module.network.jetty.handler.JettyHttpHandler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class JettyHttpServer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static Server jettyServer;

    public static void start(JettyHttpHandler jettyHttpHandler, Map<String, Integer> map) throws Exception {
        int port = map.get("port");
        int httpMinThreads = map.get("httpMinThreads");
        int httpMaxThreads = map.get("httpMaxThreads");
        int idleTimeout = map.get("idleTimeout");
        jettyServer = new Server(getQueuedThreadPool(httpMinThreads, httpMaxThreads, idleTimeout));
        jettyServer.setDumpAfterStart(false);
        jettyServer.setDumpBeforeStop(false);
        jettyServer.setStopAtShutdown(false);
        jettyServer.addConnector(httpConnector(port, idleTimeout));
        jettyServer.setHandler(jettyHttpHandler);
        jettyServer.start();
    }

    public void stop() {
        if (jettyServer.isRunning()) {
            try {
                jettyServer.stop();
            } catch (Exception e) {
                logger.error("stop http server error", e);
            }
        } else {
            this.logger.error("http stop fail. Not running now");
        }
    }

    private static QueuedThreadPool getQueuedThreadPool(int httpMinTreads, int httpMaxThreads, int idleTimeout) {
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMinThreads(httpMinTreads);
        threadPool.setMaxThreads(httpMaxThreads);
        threadPool.setIdleTimeout(idleTimeout);
        return threadPool;
    }

    private static ServerConnector httpConnector(int port, int idleTimeout) {
        // 接受连接线程 与 连接事件处理线程都设置为1
        ServerConnector connector = new ServerConnector(jettyServer, 1, 2, new HttpConnectionFactory(getHttpConfig(port)));
        connector.setPort(port);
        connector.setIdleTimeout(idleTimeout);
        return connector;
    }

    private static HttpConfiguration getHttpConfig(int port) {
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("http");
        http_config.setSecurePort(port);
        http_config.setOutputBufferSize(32768);
        http_config.setRequestHeaderSize(8192);
        http_config.setResponseHeaderSize(8192);
        http_config.setSendServerVersion(true);
        http_config.setSendDateHeader(true);
        return http_config;
    }
}