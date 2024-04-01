package com.SMPCore.mining;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CustomBlockBreakEvent extends Event implements Cancellable {

    public static HandlerList handlerList = new HandlerList();
    private boolean cancel = false;
    public final Block block;
    public final Player player;

    public CustomBlockBreakEvent(Player player,Block block) {
        this.block = block;
        this.player = player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public void setCancelled(boolean b) {
        cancel = b;
    }
}
