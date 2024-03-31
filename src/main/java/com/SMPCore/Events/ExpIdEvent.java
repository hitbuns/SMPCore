package com.SMPCore.Events;


import com.SMPCore.skills.PlayerDataHandler;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class ExpIdEvent extends Event implements iOfflinePlayerDependent {

    public static HandlerList handlerList = new HandlerList();
    private final OfflinePlayer offlinePlayer;
    private final PlayerDataHandler.ExpId expId;

    public ExpIdEvent(OfflinePlayer offlinePlayer, PlayerDataHandler.ExpId expId) {
        this.offlinePlayer = offlinePlayer;
        this.expId = expId;
    }

    @Override
    public OfflinePlayer getOfflinePlayer() {
        return offlinePlayer;
    }

    public PlayerDataHandler.ExpId getExpId() {
        return expId;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }


}
