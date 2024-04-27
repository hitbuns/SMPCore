package com.SMPCore.listeners;

import be.razerstorm.customcrafting.events.PushRecipeToServerEvent;
import be.razerstorm.customcrafting.events.RecipeRemoveEvent;
import com.MenuAPI.Utilities.BukkitEventCaller;
import com.MenuAPI.Utilities.FormattedNumber;
import com.MenuAPI.Utils;
import com.SMPCore.Events.DropTriggerEvent;
import com.SMPCore.Events.TickedSMPEvent;
import com.SMPCore.Main;
import com.SMPCore.Utilities.CooldownHandler;
import com.SMPCore.Utilities.TempEntityDataHandler;
import com.SMPCore.configs.CraftExpConfig;
import com.SMPCore.gui.WarpGUI;
import com.SMPCore.skills.AbilitySkillPerk;
import com.SMPCore.skills.PlayerDataHandler;
import com.SMPCore.skills.impl.AbilityIntentionType;
import com.SoundAnimation.SoundAPI;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.world.World;
import de.tr7zw.nbtapi.NBTItem;
import me.clip.placeholderapi.PlaceholderAPI;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.query.QueryMode;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class EventListener implements Listener {

//    @EventHandler
//    public void onJoin(PlayerJoinEvent playerJoinEvent) {
//
//        playerJoinEvent.getPlayer().setResourcePack("https://www.dropbox.com/scl/fi/jhos5u893isxuair15gaa/quartzpack.zip?rlkey=if30890t05j20yv4yraxobnz6&dl=0");
//
//    }

    @EventHandler(priority = EventPriority.MONITOR,ignoreCancelled = true)
    public void onDamageTaken(EntityDamageByEntityEvent entityDamageByEntityEvent) {

        if (entityDamageByEntityEvent.getEntity() instanceof Player player) {
            TempEntityDataHandler.EntityData entityData = TempEntityDataHandler.getorAdd(player);
            entityData.playerCooldownHandler.setOnCoolDown("rageTickDownDelay");
            entityData.updateData("rageCurrent",Double.class,initial -> Math.max(Math.min(100,initial+0.03*entityDamageByEntityEvent.getDamage()),0),0D);
        }

        if (Utils.getAttacker(entityDamageByEntityEvent.getDamager()) instanceof Player player) {
            TempEntityDataHandler.EntityData entityData = TempEntityDataHandler.getorAdd(player);
            entityData.playerCooldownHandler.setOnCoolDown("rageTickDownDelay");
            entityData.updateData("rageCurrent",Double.class,initial -> Math.max(Math.min(100,initial+0.2*entityDamageByEntityEvent.getDamage()),0),0D);
        }

    }



    @EventHandler (priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent playerChatEvent) {
        if (playerChatEvent.isCancelled()) return;


        final String[] message = {playerChatEvent.getMessage()};
        Player[] players = Bukkit.getOnlinePlayers().stream().filter(player -> {
            if (message[0].contains(player.getName())) {
                message[0] = message[0].replace(player.getName(), PlaceholderAPI.setPlaceholders(player,"%katsu_player_"+player.getName()+"_small%")+" @"+player.getName());
                return true;
            }

            return false;
        }).toArray(Player[]::new);

        SoundAPI.playSound("notification_pling",players);
        playerChatEvent.setMessage(message[0]);

//
//
//        Main.Instance.getServer().getLogger().info("TEST_!@#$3");
//
//        Main.Instance.getServer().getLogger().info(playerChatEvent.getFormat());
        Player yapper = playerChatEvent.getPlayer();
        String s = Utils.color(PlaceholderAPI.setPlaceholders(yapper, "%katsu_player_"+yapper.getName()+"_small%"
        )+" "+ Main.api.getGroupManager().getGroup(Main.api.getUserManager().getUser(yapper.getUniqueId()).getPrimaryGroup())
                .getCachedData().getMetaData().getPrefix()+" &6%s &8➢ &e%s");
        playerChatEvent.setFormat(String.format(s,yapper.getDisplayName(),message[0]));
//
//        Main.Instance.getServer().getLogger().info("TEST_!@#$4");
//        Main.Instance.getServer().getLogger().info(playerChatEvent.getFormat());

    }


    @EventHandler (priority = EventPriority.HIGHEST)
    public void onDrop(PlayerDropItemEvent playerDropItemEvent) {

        Player player = playerDropItemEvent.getPlayer();
        playerDropItemEvent.setCancelled(true);
        CooldownHandler<Entity> cooldownHandler = TempEntityDataHandler.getorAdd(player).playerCooldownHandler;
        if (!cooldownHandler.isOnCoolDown("dropTrigger_ability_cooldown",TimeUnit.MILLISECONDS,100)) {
            cooldownHandler.reduceCoolDown("dropTrigger_ability_cooldown",TimeUnit.MILLISECONDS,100);
            BukkitEventCaller.callEvent(new DropTriggerEvent(player,playerDropItemEvent.getItemDrop().getItemStack()));
        }

    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onInvDrop(InventoryClickEvent playerDropItemEvent) {
        if (playerDropItemEvent.getAction().name().contains("DROP_")) {

            boolean b = playerDropItemEvent.getAction().name()
                    .contains("CURSOR");
            ItemStack itemStack = b ? playerDropItemEvent.getCursor() : playerDropItemEvent
                    .getCurrentItem(),dropped = Utils.isNullorAir(itemStack) ? null :
                    itemStack.clone();

            if (playerDropItemEvent.getAction().name().contains("ONE"))
                dropped.setAmount(1);

            itemStack.setAmount(itemStack.getAmount()-dropped.getAmount());

            Item item = playerDropItemEvent.getWhoClicked().getWorld().dropItemNaturally(playerDropItemEvent
                    .getWhoClicked().getEyeLocation(),dropped);
            item.setVelocity(playerDropItemEvent.getWhoClicked()
                    .getEyeLocation().getDirection().clone().normalize().multiply(0.3));

            if (b) {
                playerDropItemEvent.setCursor(itemStack.getAmount() > 0 ? itemStack :
                        null);
            } else playerDropItemEvent.setCurrentItem(itemStack.getAmount() > 0 ? itemStack :
                    null);
            playerDropItemEvent.setCancelled(true);

            if (playerDropItemEvent.getWhoClicked() instanceof Player player)
                TempEntityDataHandler.getorAdd(player).playerCooldownHandler.setOnCoolDown("dropTrigger_ability_cooldown");
        }
    }

    @EventHandler (ignoreCancelled = true,priority = EventPriority.MONITOR)
    public void onItemDamage(PlayerItemDamageEvent playerItemDamageEvent) {

        ItemStack itemStack = playerItemDamageEvent.getItem();

        if (Utils.isNullorAir(itemStack)) return;

        NBTItem nbtItem = new NBTItem(itemStack);

        if (nbtItem.getInteger("CustomModelData") == 12925)
            playerItemDamageEvent.setDamage((int) Math.round(playerItemDamageEvent.getDamage()*0.1));
    }


    @EventHandler
    public void onWarpCheckMove(PlayerMoveEvent playerMoveEvent) {

        TempEntityDataHandler.EntityData entityData = TempEntityDataHandler.getorAdd(playerMoveEvent.getPlayer());
        WarpGUI.TeleportRequestHandler teleportRequestHandler = entityData.get("teleportRequest", WarpGUI.TeleportRequestHandler.class,null);

        if (teleportRequestHandler == null) return;

        Location from = playerMoveEvent.getFrom(), to = playerMoveEvent
                .getTo();

        if (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()) {

            teleportRequestHandler.setCancelled(true);
            entityData.updateData("teleportRequest", WarpGUI.TeleportRequestHandler.class,initial -> null,null);

        }

    }

    @EventHandler (ignoreCancelled = true,priority = EventPriority.MONITOR)
    public void onTeleport(PlayerTeleportEvent playerTeleportEvent) {

        TempEntityDataHandler.EntityData entityData = TempEntityDataHandler.getorAdd(playerTeleportEvent.getPlayer());
        WarpGUI.TeleportRequestHandler teleportRequestHandler = entityData.get("teleportRequest", WarpGUI.TeleportRequestHandler.class,null);

        if (teleportRequestHandler == null) return;

        entityData.updateData("teleportRequest", WarpGUI.TeleportRequestHandler.class,initial -> null,null);

    }

    @EventHandler
    public void onClaimWandInteract(PlayerInteractEvent playerInteractEvent) {

        Player player = playerInteractEvent.getPlayer();

        AbilityIntentionType.allPerks.forEach((s, skillPerk) -> skillPerk.onEvent(playerInteractEvent, player));

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        Action action = playerInteractEvent.getAction();
        if (Utils.isNullorAir(itemStack) || action == Action.PHYSICAL || action.name().contains("AIR")) return;

        NBTItem nbtItem = new NBTItem(itemStack);
        if (nbtItem.hasKey("claimTool") && nbtItem.getBoolean("claimTool")) {

            LocalSession localSession = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player));

            World world = BukkitAdapter.adapt(player.getWorld());

            if (!(localSession.getRegionSelector(world) instanceof CuboidRegionSelector)) {
                localSession.setRegionSelector(world, new CuboidRegionSelector());
            }

            CuboidRegionSelector regionSelector = (CuboidRegionSelector) localSession.getRegionSelector(world);

            Block block = playerInteractEvent.getClickedBlock();
            assert block != null;

            TempEntityDataHandler.EntityData entityData = TempEntityDataHandler.getorAdd(player);
            if (entityData.playerCooldownHandler.isOnCoolDown("claim_tool_use_"+action.name(), TimeUnit.SECONDS,1)) return;

            if (action == Action.LEFT_CLICK_BLOCK) {

                regionSelector.selectPrimary(BlockVector3.at(block.getX(),block.getY(),block.getZ()),null);
                player.sendMessage(Utils.color("&4[Claim Region Selector] &7Successfully set pos1 to x: &a"+block
                        .getX()+"&7, y: &a"+block.getY()+"&7, z: &a"+block.getZ()));

            } else if (action == Action.RIGHT_CLICK_BLOCK) {

                regionSelector.selectSecondary(BlockVector3.at(block.getX(),block.getY(),block.getZ()),null);
                player.sendMessage(Utils.color("&4[Claim Region Selector] &7Successfully set pos2 to x: &a"+block
                        .getX()+"&7, y: &a"+block.getY()+"&7, z: &a"+block.getZ()));

            }

            entityData.playerCooldownHandler.setOnCoolDown("claim_tool_use_"+action.name());


        }

    }


    @EventHandler
    public void onDropWand(PlayerDropItemEvent playerDropItemEvent) {

        ItemStack itemStack = playerDropItemEvent.getItemDrop().getItemStack();

        if (Utils.isNullorAir(itemStack)) return;

        NBTItem nbtItem = new NBTItem(itemStack);
        if (nbtItem.hasKey("claimTool") && nbtItem.getBoolean("claimTool")) {

            playerDropItemEvent.getItemDrop().setPickupDelay(Integer.MAX_VALUE);
            playerDropItemEvent.getItemDrop().remove();

        }

    }

    @EventHandler
    public void onRecipeCreate(PushRecipeToServerEvent pushRecipeToServerEvent) {
        if (pushRecipeToServerEvent.recipe instanceof CraftingRecipe recipe) {


            ConfigurationSection configurationSection = CraftExpConfig.Instance.getorAddConfigurationSection("crafts");

            String key = recipe.getKey().getKey();

            if (!configurationSection.contains(key)) {

                ConfigurationSection configurationSection1 = CraftExpConfig.Instance.getorAddConfigurationSection(configurationSection,key);
                configurationSection1.set("expGain",0);
                configurationSection1.set("requiredLevelCrafting",0);
                CraftExpConfig.Instance.save();
            }


        }
    }

    @EventHandler
    public void onRecipeRemove(RecipeRemoveEvent recipeRemoveEvent) {

        if (recipeRemoveEvent.recipe instanceof CraftingRecipe recipe) {


            ConfigurationSection configurationSection = CraftExpConfig.Instance.getorAddConfigurationSection("crafts");

            String key = recipe.getKey().getKey();

            configurationSection.set(key,null);
            CraftExpConfig.Instance.save();


        }
    }


    @EventHandler (ignoreCancelled = true,priority = EventPriority.MONITOR)
    public void onDropTriggerEvent(DropTriggerEvent dropTriggerEvent) {

        ItemStack itemStack = dropTriggerEvent.getItemStack();

        if (Utils.isNullorAir( itemStack)) return;
        Player player = dropTriggerEvent.getPlayer();
        boolean sneak = player.isSneaking();

        AbilityIntentionType abilityIntentionType = getAbilityIntentionType(itemStack);

        if (abilityIntentionType == null) return;

        if (AbilityIntentionType.allPerks.get(PlayerDataHandler.getPlayerData(player).getString("ability_"+abilityIntentionType
                .name()+"_"+(sneak ? "SECONDARY" : "PRIMARY")))  instanceof AbilitySkillPerk abilitySkillPerk) {

            abilitySkillPerk.onAbilityActivate(dropTriggerEvent,player,!sneak);

        }

    }

    public static AbilityIntentionType getAbilityIntentionType(ItemStack itemStack) {
        String material = itemStack.getType().name();

        return material.contains("_PICKAXE") ? AbilityIntentionType.MINING : material.contains("_AXE") ?
                AbilityIntentionType.AXE : material.contains("_SWORD") ? AbilityIntentionType.SWORD : material.contains("_HOE") ? AbilityIntentionType.FARMING :
                itemStack.getType() == Material.BOW || itemStack.getType() == Material.CROSSBOW ? AbilityIntentionType.RANGED_COMBAT : itemStack.getType() == Material.ENCHANTED_BOOK ?
                        AbilityIntentionType.ENCHANTING : null;
    }


    int c = 0;

    @EventHandler
    public void onTick(TickedSMPEvent tickedSMPEvent) {

        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        players.forEach(player -> AbilityIntentionType.allPerks.forEach((s, skillPerk) -> skillPerk.onEvent(tickedSMPEvent, player)));

        if (++c >= 7) {
            players.forEach(player -> {

                TempEntityDataHandler.EntityData entityData = TempEntityDataHandler.getorAdd(player);
                if (!(entityData.playerCooldownHandler.isOnCoolDown("rageLastUse",
                        TimeUnit.SECONDS,2)) && !(entityData.playerCooldownHandler
                        .isOnCoolDown("rageTickDownDelay",TimeUnit.SECONDS,3))) {
                    entityData.updateData("rageCurrent",
                            Double.class, initial -> {
                        double v = initial-0.4;
                        return Math.max(0, Math.min(100, v));
                            }, 0D);
                }
                updateRage(player);
            });
            c = 0;
        }

    }

    static void updateRage(Player player) {
        double progress= TempEntityDataHandler.getorAdd(player)
                .get("rageCurrent",Double.class,0D);
        double v = progress/100.0;
        BossBar bossBar = getorAddBossBar(player);
        bossBar.setColor( v >= 0.65 ? BarColor.RED : v >= 0.35 ? BarColor.YELLOW :
                BarColor.GREEN);
        bossBar.setProgress(Math.max(0,Math.min(1,v)));
        bossBar.removeFlag(BarFlag.CREATE_FOG);
        bossBar.setTitle(Utils.color("&e&lRage: "+(v >= 0.75 ? "&a" : v >= 0.55 ? "&e" :
                v >= 0.35 ? "&6" : v >= 0.15 ? "&c" : "&4")+FormattedNumber
                .getInstance().getCommaFormattedNumber(progress,1)+"/100"));

    }


    static BossBar getorAddBossBar(Player player) {
        NamespacedKey namespacedKey = NamespacedKey.minecraft("bossbar"+String.valueOf(player.getUniqueId()).replace("_",""));
        BossBar bossBar = Bukkit.getBossBar(namespacedKey);

        if (bossBar == null) {
            double progress= TempEntityDataHandler.getorAdd(player)
                    .get("rageCurrent",Double.class,0D);
            double v = progress/100.0;
            bossBar = Bukkit.createBossBar(namespacedKey,
                    Utils.color("&e&lRage: "+(v >= 0.75 ? "&a" : v >= 0.55 ? "&e" :
                            v >= 0.35 ? "&6" : v >= 0.15 ? "&c" : "&4")+ FormattedNumber
                            .getInstance().getCommaFormattedNumber(progress,1)+"/100"), v >= 0.65 ? BarColor.RED : v >= 0.35 ? BarColor.YELLOW :
                    BarColor.GREEN, BarStyle.SEGMENTED_6, BarFlag.CREATE_FOG);
            bossBar.setProgress(Math.max(0,Math.min(1,v)));
        }

        if (!bossBar.getPlayers().contains(player)) bossBar.addPlayer(player);

        return bossBar;
    }


}
