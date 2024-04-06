package com.SMPCore.listeners;

import com.MenuAPI.ItemAdder;
import com.MenuAPI.Utilities.BukkitEventCaller;
import com.MenuAPI.Utilities.impl.HeadUtils;
import com.MenuAPI.Utils;
import com.SMPCore.Events.DropTriggerEvent;
import com.SMPCore.Utilities.CooldownHandler;
import com.SMPCore.Utilities.TempEntityDataHandler;
import com.SMPCore.gui.WarpGUI;
import com.SoundAnimation.SoundAPI;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.world.World;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public class EventListener implements Listener {

//    @EventHandler
//    public void onJoin(PlayerJoinEvent playerJoinEvent) {
//
//        playerJoinEvent.getPlayer().setResourcePack("https://www.dropbox.com/scl/fi/jhos5u893isxuair15gaa/quartzpack.zip?rlkey=if30890t05j20yv4yraxobnz6&dl=0");
//
//    }


    @EventHandler
    public void onChat(AsyncPlayerChatEvent playerChatEvent) {
        final String[] message = {playerChatEvent.getMessage()};
        Player[] players = Bukkit.getOnlinePlayers().stream().filter(player -> {
            if (message[0].contains(player.getName())) {
                message[0] = message[0].replace(player.getName(),"@"+player.getName());
                return true;
            }

            return false;
        }).toArray(Player[]::new);

        SoundAPI.playSound("notification_pling",players);
        playerChatEvent.setMessage(message[0]);

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
    public void onJoin(PlayerJoinEvent playerJoinEvent) {
        ItemAdder.addItem(playerJoinEvent
                .getPlayer(), HeadUtils.getItemHead("http://textures.minecraft.net/texture/dd4226d3d5b102c0ee4bac7d8db599177c9a4ce7bb45bb47faf67fe0c543bd04"));
    }

}
