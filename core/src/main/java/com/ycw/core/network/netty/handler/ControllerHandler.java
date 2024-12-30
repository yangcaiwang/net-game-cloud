package com.ycw.core.network.netty.handler;

import com.ycw.core.internal.thread.pool.actor.ActorThreadPoolExecutor;
import com.ycw.core.network.grpc.GrpcManage;
import com.ycw.core.network.netty.message.IMessage;
import com.ycw.core.network.netty.message.SocketMessage;
import com.ycw.core.network.netty.socket.SocketCmdContext;
import com.ycw.core.network.netty.socket.SocketCmdParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <后端控制器处理器实现类>
 * <p>
 * ps: 接收路由器的protobuf消息，把消息转发到服务器的控制器
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class ControllerHandler implements ControllerListener {

    private static final Logger log = LoggerFactory.getLogger(ControllerHandler.class);
    public static final ActorThreadPoolExecutor actorExecutor = new ActorThreadPoolExecutor("controller-message-thread", Runtime.getRuntime().availableProcessors() * 2 + 1);

    @Override
    public void exec(IMessage msg) {
        SocketCmdParams socketCmdParams = SocketCmdContext.getInstance().getMethodHandler(msg.getCmd());
        if (socketCmdParams == null) {
            log.error("not found cmd:{} {} methodHandler", msg.getCmd(), msg);
            return;
        }

        SocketControllerHandler handlerMethodClass = SocketCmdContext.getInstance().getHandlerMethodClass(socketCmdParams.getMethod().getDeclaringClass().getSimpleName());
        if (handlerMethodClass == null) {
            log.error("not found cmd:{} handle class", msg.getCmd());
            return;
        }

        IMessage resp = handlerMethodClass.process(socketCmdParams, msg);
        if (resp != null) {
            GrpcManage.getInstance().sentRouter((SocketMessage) resp);
        }
    }
}
