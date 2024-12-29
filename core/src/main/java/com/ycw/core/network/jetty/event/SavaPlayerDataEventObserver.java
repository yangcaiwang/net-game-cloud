package com.ycw.core.network.jetty.event;

import com.ycw.core.internal.event.annotation.EventSubscriber;

public class SavaPlayerDataEventObserver {
    @EventSubscriber
    public void save(SavePlayerDataEvent event) {
        long playerId = event.getPlayerId();
        // TODO 处理某个玩家业务service入库
    }

    @EventSubscriber
    public void saveAll(SaveAllPlayerDataEvent event) {
        // TODO 处理所有业务service入库
    }
}
