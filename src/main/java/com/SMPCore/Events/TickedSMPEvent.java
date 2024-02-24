package com.SMPCore.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TickedSMPEvent extends Event {

    public static HandlerList handlerList = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}

