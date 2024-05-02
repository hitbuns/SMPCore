package com.SMPCore.LootTableEvents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class LootTableEvent extends Event {

    final String rewardPath;
    public static HandlerList handlerList = new HandlerList();

    public LootTableEvent(String rewardPath) {
        this.rewardPath = rewardPath;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public String getRewardPath() {
        return rewardPath;
    }

    public interface ProcMultiplier {

        double process(double a);

    }

}
