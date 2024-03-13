package com.SMPCore.listeners;

import com.MenuAPI.Utils;
import com.SMPCore.Utilities.TempEntityDataHandler;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.world.World;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public class EventListener implements Listener {

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

}
