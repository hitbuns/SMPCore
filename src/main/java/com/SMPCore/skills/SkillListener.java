package com.SMPCore.skills;

import com.MenuAPI.Utils;
import com.SMPCore.Events.ExpIdExpGainEvent;
import com.SMPCore.mining.CustomBlockBreakEvent;
import com.SMPCore.skills.impl.NonCombatStatType;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class SkillListener implements Listener {

    @EventHandler (priority = EventPriority.MONITOR)
    public void onMining(CustomBlockBreakEvent customBlockBreakEvent) {

        if (customBlockBreakEvent.isCancelled()) return;

        Material material = customBlockBreakEvent.block.getType();
        ItemStack itemStack = customBlockBreakEvent.player.getInventory().getItemInMainHand();
        World world = customBlockBreakEvent.player.getWorld();
        (Utils.isNullorAir( itemStack) ? customBlockBreakEvent.block.getDrops() : customBlockBreakEvent
                .block.getDrops(itemStack,customBlockBreakEvent.player)).stream().filter(Objects::nonNull).forEach(itemStack1 ->
                world.dropItemNaturally(customBlockBreakEvent.block.getLocation(),itemStack1.clone()));

        if (material.name().endsWith("_ORE"))
            PlayerDataHandler.addExp(customBlockBreakEvent.player, NonCombatStatType.MINING,ExpReason.GRIND,switch (material) {
                case COAL_ORE,DEEPSLATE_COAL_ORE -> 5;
                case COPPER_ORE,DEEPSLATE_COPPER_ORE -> 3;
                case GOLD_ORE,DEEPSLATE_GOLD_ORE,NETHER_GOLD_ORE -> 30;
                case REDSTONE_ORE,DEEPSLATE_REDSTONE_ORE -> 8;
                case LAPIS_ORE,DEEPSLATE_LAPIS_ORE -> 10;
                case EMERALD_ORE,DEEPSLATE_EMERALD_ORE -> 15;
                case DIAMOND_ORE,DEEPSLATE_DIAMOND_ORE -> 50;
                default -> 0;
            });

    }

    @EventHandler
    public void onSkillExpAdd(ExpIdExpGainEvent expIdExpGainEvent) {

        if (expIdExpGainEvent.getOfflinePlayer() instanceof Player player)
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent
                    .fromLegacyText(Utils.color("&2+"+expIdExpGainEvent.getExpAmount()+" "+expIdExpGainEvent
                            .getExpId().getDisplay())));

    }

}
