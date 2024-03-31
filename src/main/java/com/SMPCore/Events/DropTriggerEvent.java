package com.SMPCore.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

public class DropTriggerEvent extends PlayerEvent {

    public static HandlerList handlerList = new HandlerList();
    private ItemStack itemStack;

    public DropTriggerEvent(Player who, ItemStack itemStack) {
        super(who);
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}