package com.ycw.core.network.jetty;

import com.ycw.core.cluster.enums.ServerType;
import com.ycw.core.cluster.template.BaseYmlTemplate;
import com.ycw.core.internal.heart.jetty.JettyHeartbeatProcess;
import com.ycw.core.network.jetty.handler.JettyHttpHandler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <Jetty服务端启动类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class JettyHttpServer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Server jettyServer;

    private JettyHeartbeatProcess jettyHeartbeatProcess;

    private static JettyHttpServer jettyHttpServer = new JettyHttpServer();

    public static JettyHttpServer getInstance() {
        return jettyHttpServer;
    }

    public void start(JettyHttpHandler jettyHttpHandler, BaseYmlTemplate jettyYmlTemplate, ServerType serverType) throws Exception {
        jettyServer = new Server(getQueuedThreadPool());
        jettyServer.setDumpAfterStart(false);
        jettyServer.setDumpBeforeStop(false);
        jettyServer.setStopAtShutdown(false);
        jettyServer.addConnector(httpConnector(jettyYmlTemplate.getPort()));
        jettyServer.setHandler(jettyHttpHandler);
        jettyServer.start();

        jettyHeartbeatProcess = new JettyHeartbeatProcess(jettyYmlTemplate.getHeartbeatTime(), jettyYmlTemplate.getHeartbeatTimeout());

        // Gm服作为注册中心
        if (serverType == ServerType.GM_SERVER) {
            jettyHeartbeatProcess.monitor();
        } else {
            jettyHeartbeatProcess.sent();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }

    private QueuedThreadPool getQueuedThreadPool() {
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMinThreads(10);
        threadPool.setMaxThreads(100);
        threadPool.setIdleTimeout(60000);
        return threadPool;
    }

    private ServerConnector httpConnector(int port) {
        ServerConnector connector = new ServerConnector(jettyServer, new HttpConnectionFactory(getHttpConfig(port)));
        connector.setPort(port);
        connector.setIdleTimeout(60000);
        return connector;
    }

    private HttpConfiguration getHttpConfig(int port) {
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

    private void stop() {
        try {
            if (jettyServer != null && jettyServer.isRunning()) {
                jettyServer.stop();

            }

            if (jettyHeartbeatProcess != null) {
                jettyHeartbeatProcess.showdown();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public JettyHeartbeatProcess getJettyHeartbeatProcess() {
        return jettyHeartbeatProcess;
    }
}
