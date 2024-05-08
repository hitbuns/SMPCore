package com.SMPCore.skills;

import com.MenuAPI.BukkitEventCaller;
import com.MenuAPI.Utils;
import com.SMPCore.Events.ExpIdExpGainEvent;
import com.SMPCore.Events.ExpIdLevelUpEvent;
import com.SMPCore.Events.FarmHarvestEvent;
import com.SMPCore.Utilities.TempEntityDataHandler;
import com.SMPCore.configs.BlockDataConfig;
import com.SMPCore.mining.CustomBlockBreakEvent;
import com.SMPCore.skills.impl.AbilityIntentionType;
import com.SMPCore.skills.impl.CombatStatType;
import com.SMPCore.skills.impl.NonCombatStatType;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.FurnaceInventory;

import java.util.concurrent.TimeUnit;

public class SkillListener implements Listener {


    @EventHandler (priority = EventPriority.MONITOR,ignoreCancelled = true)
    public void cookExp(InventoryClickEvent inventoryClickEvent) {

        if (inventoryClickEvent.isCancelled()) return;

        if (inventoryClickEvent
                .getWhoClicked() instanceof Player player
                && inventoryClickEvent.getClickedInventory() instanceof FurnaceInventory furnaceInventory &&
        inventoryClickEvent.getCurrentItem() == furnaceInventory.getResult() && !Utils.isNullorAir(inventoryClickEvent.getCurrentItem())) {



        }


    }

    @EventHandler (priority = EventPriority.MONITOR,ignoreCancelled = true)
    public void defenseExp(EntityDamageEvent entityDamageEvent) {

        if (entityDamageEvent.isCancelled()) return;

        if (entityDamageEvent.getEntity() instanceof Player player) {

            double damage = entityDamageEvent.getDamage();
            TempEntityDataHandler.getorAdd(player).updateData("exp_next_tick_"+ CombatStatType.DEFENSE.name(),
                    Double.class,initial -> initial+switch (entityDamageEvent.getCause()) {
                        case FALL -> 0.2*damage;
                        case FIRE, FIRE_TICK -> 0.05*damage;
                        case THORNS -> 0.25*damage;
                        case ENTITY_ATTACK ->0.5*damage;
                        case LAVA -> 0.1*damage;
                        case PROJECTILE -> 0.75*damage;
                        case POISON -> 0.17*damage;
                        default -> 0;
                    },0D);

        }

        if (entityDamageEvent instanceof EntityDamageByEntityEvent entityDamageByEntityEvent && Utils.getAttacker(entityDamageByEntityEvent
                .getDamager()) instanceof Player player && entityDamageEvent.getEntity() instanceof LivingEntity
        livingEntity) {
            double finalDamage = entityDamageEvent
                    .getFinalDamage();
            Player v = livingEntity instanceof Player player1 ? player1 : null;

            TempEntityDataHandler.EntityData entityData = TempEntityDataHandler.getorAdd(player);

            if (livingEntity.getHealth() <= entityDamageEvent.getFinalDamage() && (v == null ||
                    entityData.playerCooldownHandler.isOnCoolDown("last_deathkill_on_"+
                            v.getName(), TimeUnit.MINUTES,2))) {
                PlayerDataHandler.addExp(player, CombatStatType.CONSTITUTION, ExpReason.GRIND, livingEntity.getMaxHealth() * (v != null ? 5 : 0.5));
            }

            entityData.updateData("exp_next_tick_"+ (entityDamageByEntityEvent.getDamager() instanceof Projectile projectile && projectile instanceof Arrow &&
                            projectile.getShooter() == player ? CombatStatType.DEXTERITY : CombatStatType
                            .STRENGTH).name(),
                    Double.class,initial -> initial+finalDamage*0.5,0D);




        }

    }

    @EventHandler (priority = EventPriority.MONITOR,ignoreCancelled = true)
    public void onMove(PlayerMoveEvent playerMoveEvent) {
        Player player = playerMoveEvent.getPlayer();
        if (playerMoveEvent.isCancelled() || !player.isSprinting()) return;

        TempEntityDataHandler.EntityData entityData = TempEntityDataHandler.getorAdd(player);

        if (!entityData.get("disable_sprint_gain_exp",
                Boolean.class,false)) {

            entityData.updateData("exp_next_tick_"+ CombatStatType.DEFENSE.name(),
                    Double.class,initial -> initial+0.025,0D);

        }


    }


    @EventHandler (ignoreCancelled = true,priority = EventPriority.MONITOR)
    public void onPlace(BlockPlaceEvent blockPlaceEvent) {

        BlockDataConfig.Instance.saveBlock(blockPlaceEvent.getBlock());

    }

    @EventHandler
    public void onPistonMove(BlockPistonExtendEvent blockPistonEvent) {
        BlockDataConfig.Instance.saveBlock(blockPistonEvent.getBlock());
    }

    @EventHandler
    public void onPistonMove(BlockPistonRetractEvent blockPistonEvent) {
        BlockDataConfig.Instance.saveBlock(blockPistonEvent.getBlock());
    }


    @EventHandler (ignoreCancelled = true,priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent blockBreakEvent) {
        BlockDataConfig.Instance.removeBlock(blockBreakEvent.getBlock());
    }

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

        AbilityIntentionType.allPerks.forEach((s, skillPerk) -> skillPerk.onEvent(customBlockBreakEvent, customBlockBreakEvent.player));

        if (material.name().endsWith("_WOOD") && BlockDataConfig.Instance.isBlockEncoded(customBlockBreakEvent.block)) {

            double v = 5;

            PlayerDataHandler.addExp(customBlockBreakEvent.player, NonCombatStatType.WOODCUTTING, ExpReason.GRIND, v);

            TempEntityDataHandler.getorAdd(customBlockBreakEvent.player).updateData("rageCurrent",Double.class,initial ->
                    Math.max(0,Math.min(initial+v/10,100)),0D);

            return;
        }


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
                    Math.max(0,Math.min(initial+v/10,100)),0D);
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

    @EventHandler (ignoreCancelled = true,priority = EventPriority.MONITOR)
    public void onFarmHarvest(FarmHarvestEvent farmHarvestEvent) {
        if (farmHarvestEvent.isCancelled()) return;

        AbilityIntentionType.allPerks.forEach((s, skillPerk) -> skillPerk.onEvent(farmHarvestEvent, farmHarvestEvent.getPlayer()));

    }


    //FARMING
    @EventHandler (priority = EventPriority.MONITOR,ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent blockBreakEvent) {
        if (blockBreakEvent.isCancelled()) return;

        Player player = blockBreakEvent.getPlayer();
        Block block = blockBreakEvent.getBlock();

        BlockData blockData = block.getBlockData();

        if (!(blockData instanceof Ageable ageable)) return;

        if (ageable.getAge() != ageable.getMaximumAge()) return;

        Material material = block.getType();

        if (material == Material.FIRE || material == Material.CAVE_VINES || material == Material.BAMBOO || material.name().contains("SAPLING")) return;

        FarmHarvestEvent farmHarvestEvent = new FarmHarvestEvent(blockBreakEvent,player,3,1);
        if (BukkitEventCaller.callEvent(farmHarvestEvent)) {
            blockBreakEvent.setCancelled(true);
            return;
        }


        PlayerDataHandler.addExp(player,NonCombatStatType.FARMING,ExpReason.GRIND,Math.max(0,farmHarvestEvent.exp));

        block.getDrops(player.getInventory().getItemInMainHand()).stream().filter(itemStack -> !Utils.isNullorAir(itemStack)).peek(itemStack -> itemStack.setAmount((int) Math.round(Math.max(0,(itemStack
                .getAmount()*farmHarvestEvent.amountMultiplier*(1+0.05*PlayerDataHandler.getLevel(player,NonCombatStatType.FARMING)))-1)))).filter(itemStack -> itemStack.getAmount() >= 1).forEach(
                        itemStack -> block.getWorld().dropItemNaturally(block.getLocation(),itemStack)
        );




    }

}
