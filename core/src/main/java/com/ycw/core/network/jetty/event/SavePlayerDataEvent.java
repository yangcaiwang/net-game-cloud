package com.ycw.core.network.jetty.event;

import com.ycw.core.internal.event.AbstractEvent;

public class SavePlayerDataEvent extends AbstractEvent {
    long playerId;

    public SavePlayerDataEvent(long playerId) {
        this.playerId = playerId;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }
}
