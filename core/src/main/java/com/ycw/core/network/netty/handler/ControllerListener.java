package com.ycw.core.network.netty.handler;

import com.ycw.core.internal.thread.task.linked.AbstractLinkedTask;
import com.ycw.proto.CommonProto;

import java.util.concurrent.Executor;

/**
 * <控制器异步监听者接口>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public interface ControllerListener {

    /**
     * 处理业务消息
     *
     * @param executor 线程池
     * @param msg      消息
     */
    default void handle(Executor executor, CommonProto.msg msg) {
        ControllerListener listener = this;
        try {
            executor.execute(new AbstractLinkedTask() {
                @Override
                protected void exec() throws Exception {
                    listener.exec(msg);
                }

                @Override
                public Object getIdentity() {
                    return msg.getPlayerId();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 控制器异步链式处理
     */
    void exec(CommonProto.msg msg);

    /**
     * 业务消息处理
     *
     * @param msg 消息对象
     * @return CommonProto.msg {@link CommonProto.msg}
     */
    CommonProto.msg process(CommonProto.msg msg);
}
