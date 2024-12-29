package com.ycw.core.network.jetty.constant;

/**
 * <http命令通用常量>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public interface HttpCmd {

    /**
     * http请求前缀
     */
    String HTTP_PREFIX = "http://";

    /**
     * https请求前缀
     */
    String HTTPS_PREFIX = "https://";
    /**
     * 发送心跳包
     */
    String HEARTBEAT_CMD = "/HeartbeatCmd.do";

    /**
     * 更新目标服务器yml配置
     */
    String MODIFY_SERVER_YML_CMD = "/ModifyServerYmlCmd.do";

    /**
     * 连接Grpc服务端
     */
    String CONNECT_GRPC_SERVER_CMD = "/ConnectGrpcServerCmd.do";
}
