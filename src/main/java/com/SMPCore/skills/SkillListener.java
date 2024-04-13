package com.SMPCore.skills;

import com.MenuAPI.Utils;
import com.SMPCore.Events.ExpIdExpGainEvent;
import com.SMPCore.Events.ExpIdLevelUpEvent;
import com.SMPCore.Utilities.TempEntityDataHandler;
import com.SMPCore.mining.CustomBlockBreakEvent;
import com.SMPCore.skills.impl.NonCombatStatType;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
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
//        ItemStack itemStack = customBlockBreakEvent.player.getInventory().getItemInMainHand();
//        World world = customBlockBreakEvent.player.getWorld();

//        customBlockBreakEvent.setCancelled(true);
//        customBlockBreakEvent.block.setType(Material.AIR);
//
//        (Utils.isNullorAir( itemStack) ? customBlockBreakEvent.block.getDrops(itemStack) : customBlockBreakEvent
//                .block.getDrops(itemStack,customBlockBreakEvent.player)).stream().filter(Objects::nonNull).forEach(itemStack1 ->
//                world.dropItemNaturally(customBlockBreakEvent.block.getLocation(),itemStack1.clone()));

        if (material.name().endsWith("_ORE") || material.name().contains("STONE")) {
            double v = switch (material) {
                case COAL_ORE, DEEPSLATE_COAL_ORE -> 30;
                case COPPER_ORE, DEEPSLATE_COPPER_ORE -> 18;
                case GOLD_ORE, DEEPSLATE_GOLD_ORE, NETHER_GOLD_ORE -> 180;
                case REDSTONE_ORE, DEEPSLATE_REDSTONE_ORE -> 48;
                case LAPIS_ORE, DEEPSLATE_LAPIS_ORE -> 60;
                case EMERALD_ORE, DEEPSLATE_EMERALD_ORE -> 90;
                case DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE -> 300;
                case STONE, COBBLESTONE -> 1;
                default -> 0;
            };
            PlayerDataHandler.addExp(customBlockBreakEvent.player, NonCombatStatType.MINING, ExpReason.GRIND, v);

            if (v > 0) TempEntityDataHandler.getorAdd(customBlockBreakEvent.player).updateData("rageCurrent",Double.class,initial ->
                    initial+v/10,0D);
        }

    }

    @EventHandler
    public void onSkillExpAdd(ExpIdExpGainEvent expIdExpGainEvent) {

        if (expIdExpGainEvent.getOfflinePlayer() instanceof Player player)
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent
                    .fromLegacyText(Utils.color("&8&m===&e +"+expIdExpGainEvent.getExpAmount()+" "+ ChatColor.stripColor(expIdExpGainEvent
                            .getExpId().getDisplay())+" XP &8&m===").replace(".0 "," ")));

    }


    @EventHandler (ignoreCancelled = true,priority = EventPriority.MONITOR)
    public void onSkillLevelIncrease(ExpIdLevelUpEvent expIdLevelUpEvent) {

        if (expIdLevelUpEvent.getOfflinePlayer() instanceof Player player) {

            player.sendTitle(Utils.color("&8&m   &e "+expIdLevelUpEvent.getExpId().getDisplay()+" Level Up &8&m   "),
                    Utils.color("&bLvl. &e&m"+expIdLevelUpEvent
                            .getPreviousLevel()+"&b ➪ &a"+expIdLevelUpEvent.getNewLevel()),5,45,15);

        }

    }

}
