
package com.common.module.internal.delay;

import com.common.module.internal.event.AbstractEvent;
import com.common.module.internal.event.EventBusesImpl;
import com.common.module.internal.event.EventObjectPool;
import com.common.module.util.DateUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <服务器定时播报任务类>
 * <p>
 * ps: 每秒播报异常
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
final public class ServerTimer extends TimerTask {

    private static final Logger log = LoggerFactory.getLogger(ServerTimer.class);

    private long _seconds;

    private long _minutes;

    private long _hours;

    private final Timer timer;
    private final TimerTask timerTask;

    private final AtomicBoolean running = new AtomicBoolean();

    private static ServerTimer instance;

    public static ServerTimer getInstance() {
        if (instance == null) {
            synchronized (ServerTimer.class) {
                if (instance == null) {
                    instance = new ServerTimer();
                }
            }
        }
        return instance;
    }

    private ServerTimer() {
        timer = new Timer(getClass().getName());
        timerTask = this;
    }

    public boolean isRunning() {
        return running.get();
    }

    public void start() {
        if (running.compareAndSet(false, true)) {
            log.info("{} starting", this);
            timer.scheduleAtFixedRate(timerTask, 1000, 1000);
            log.info("{} started", this);
        }
    }

    @Override
    public boolean cancel() {
        if (running.compareAndSet(true, false)) {
            log.info("{} canceling", this);
            timerTask.cancel();
            timer.cancel();
            log.info("{} cancelled", this);
        }
        return true;
    }

    @Override
    public void run() {
        try {
            long now = System.currentTimeMillis();
            DateUnit.DateTemplate temp = DateUnit.getDateTemplateInstance(now);
            _seconds = temp.second;
            ServerTimePassEvent eventObjFromPool = EventObjectPool.getEventObjFromPool(ServerTimePassEvent.class);
            EventBusesImpl.getInstance().asyncPublish(now, eventObjFromPool.resetData(now, _seconds, temp.second, PassType.SECOND));
            if (_seconds % 60 == 0) {
                _minutes = temp.minute;
                eventObjFromPool = EventObjectPool.getEventObjFromPool(ServerTimePassEvent.class);
                EventBusesImpl.getInstance().asyncPublish(now, eventObjFromPool.resetData(now, _minutes, temp.minute, PassType.MINUTE));
                if (_minutes % 60 == 0) {
                    _hours = temp.hour;
                    eventObjFromPool = EventObjectPool.getEventObjFromPool(ServerTimePassEvent.class);
                    EventBusesImpl.getInstance().asyncPublish(now, eventObjFromPool.resetData(now, _hours, temp.hour, PassType.HOUR));
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 服务器时间流逝单位
     *
     * @author lijishun
     */
    static public enum PassType {
        /**
         * 秒 - 最小单位1
         */
        SECOND,
        /**
         * 分 - 最小单位1
         */
        MINUTE,
        /**
         * 小时 - 最小单位1
         */
        HOUR,
        //
        ;
    }

    /**
     * 服务器时间流逝事件,时间都是以准秒发布,当服务启动时,并不在准秒,也就是%10==0会做延迟调整,后续的时间都是准秒
     *
     * @author lijishun
     */
    final static public class ServerTimePassEvent extends AbstractEvent {

        /**
         * 当前系统时间
         */
        public long currentTime;

        /**
         * 当前计数单位passType=SECOND表示流逝多少秒,passType=MINUTE表示流逝多少分,passType=
         * HOUR表示流逝多少个钟头
         */
        public long count;

        /**
         * 当前准点单位时间,几点几分几秒
         */
        public int onTime;

        /**
         * 计数类型
         */
        public PassType passType;

        /**
         * 服务器时间流逝事件
         * </p>
         *
         * @param currentTime 当前系统时间
         *                    </p>
         * @param count       当前计数单位passType=MILLSECOND表示流逝多少毫秒 ,passType=SECOND表示流逝多少秒,
         *                    passType=MINUTE表示流逝多少分, passType=HOUR表示流逝多少个钟头
         *                    </p>
         * @param onTime      当前准点单位时间
         *                    </p>
         * @param passType    计数类型
         *                    </p>
         */
        public ServerTimePassEvent(long currentTime, long count, int onTime, PassType passType) {
            super();
            this.currentTime = currentTime;
            this.count = count;
            this.onTime = onTime;
            this.passType = passType;
            // log.debug("post ServerTimePassEvent :" + this);
        }

        public ServerTimePassEvent() {
        }

        public ServerTimePassEvent resetData(long currentTime, long count, int onTime, PassType passType) {
            this.currentTime = currentTime;
            this.count = count;
            this.onTime = onTime;
            this.passType = passType;
            return this;
        }

        public long getCurrentTime() {

            return currentTime;
        }

        public void setCurrentTime(long currentTime) {

            this.currentTime = currentTime;
        }

        public long getCount() {

            return count;
        }

        public void setCount(long count) {

            this.count = count;
        }

        public int getOnTime() {

            return onTime;
        }

        public void setOnTime(int onTime) {

            this.onTime = onTime;
        }

        public PassType getPassType() {

            return passType;
        }

        public void setPassType(PassType passType) {

            this.passType = passType;
        }

        @Override
        public String toString() {
            return "ServerTimePassEvent{" +
                    "currentTime=" + currentTime +
                    ", count=" + count +
                    ", onTime=" + onTime +
                    ", passType=" + passType +
                    '}';
        }
    }
}
