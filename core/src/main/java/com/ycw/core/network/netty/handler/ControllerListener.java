package com.ycw.core.network.netty.handler;

import com.ycw.core.internal.thread.task.linked.AbstractLinkedTask;
import com.ycw.core.network.netty.message.IMessage;
import com.ycw.core.util.SerializationUtils;

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
    default void process(Executor executor, byte[] msg) {
        ControllerListener listener = this;
        try {
            IMessage iMessage = SerializationUtils.toObjectByH2(msg);
            executor.execute(new AbstractLinkedTask() {
                @Override
                protected void exec() {
                    listener.exec(iMessage);
                }

                @Override
                public Object getIdentity() {
                    return iMessage.getPlayerId();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 控制器异步链式处理
     */
    void exec(IMessage msg);
}
