package com.SMPCore.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerEvent;

public class FarmHarvestEvent extends PlayerEvent implements Cancellable {

    public static HandlerList handlerList = new HandlerList();
    private boolean cancel = false;
    public double exp;
    public double amountMultiplier;
    public final BlockBreakEvent blockBreakEvent;

    public FarmHarvestEvent(BlockBreakEvent blockBreakEvent,Player who, double exp) {
        this(blockBreakEvent,who,exp,1);
    }

    public FarmHarvestEvent(BlockBreakEvent blockBreakEvent,Player who,double exp,double amountMultiplier) {
        super(who);
        this.exp = exp;
        this.amountMultiplier = amountMultiplier;
        this.blockBreakEvent = blockBreakEvent;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        cancel = b;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
