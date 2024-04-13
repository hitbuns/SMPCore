package com.SMPCore.mining;

import com.MenuAPI.Utils;
import com.SMPCore.mobs.EquipmentHandler;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;

public class DurabilityListener implements Listener {

    BrokenBlockHandlerList brokenBlockHandlerList = new BrokenBlockHandlerList();

    PotionEffect potionEffect = new PotionEffect(PotionEffectType.SLOW_DIGGING,999999,-1,false,false);

    @EventHandler
    public void onBlockMine(BlockDamageEvent blockDamageEvent) {
        ItemStack itemStack = blockDamageEvent.getPlayer().getInventory().getItemInMainHand();
        if (itemStack != null && itemStack.getType().name().contains("PICKAXE"))
            blockDamageEvent.getPlayer().addPotionEffect(potionEffect);
    }

//    @EventHandler
//    public void onToolSwitch(PlayerItemHeldEvent playerItemHeldEvent) {
//        PlayerInventory playerInventory = playerItemHeldEvent.getPlayer().getInventory();
//        ItemStack toItem = playerInventory.getItem(playerItemHeldEvent.getNewSlot());
//        if (toItem != null && !toItem.getType().name().contains("PICKAXE")) playerItemHeldEvent.getPlayer()
//                .removePotionEffect(PotionEffectType.SLOW_DIGGING);
//    }
//
//    @EventHandler (ignoreCancelled = false,priority = EventPriority.MONITOR)
//    public void onDrop(PlayerDropItemEvent playerDropItemEvent) {
//        playerDropItemEvent.getPlayer()
//                .removePotionEffect(PotionEffectType.SLOW_DIGGING);
//    }


//    @EventHandler
//    public void onJoin(PlayerJoinEvent playerJoinEvent) {
//
//    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {

        if (!blockConfig.enabled(event.getBlock())) {
            event.getPlayer().removePotionEffect(PotionEffectType.SLOW_DIGGING);
            return;
        }

        if (!event.getPlayer().hasPotionEffect(PotionEffectType.SLOW_DIGGING)) event.getPlayer().addPotionEffect(potionEffect);
        brokenBlockHandlerList.createBrokenBlock(event.getBlock(), blockConfig.timeToBreak(event.getBlock()));
    }

    Set<Material> transparentBlocks = new HashSet<>();

    public DurabilityListener(JavaPlugin javaPlugin) {

        new BrokenBlockHandlerList();
        blockConfig = BlockConfig.init(javaPlugin);

        transparentBlocks.add(Material.WATER);
        transparentBlocks.add(Material.AIR);

        javaPlugin.getServer().getPluginManager().registerEvents(this,javaPlugin);
    }

    BlockConfig blockConfig;

    @EventHandler
    public void onPlayerAnimation(PlayerAnimationEvent event) {
        EntityPlayer entityplayer = ((CraftPlayer) event.getPlayer()).getHandle();
        Block block = entityplayer.getBukkitEntity().getTargetBlock(transparentBlocks, 5);
        Location blockPosition = block.getLocation();

        if (!brokenBlockHandlerList.isBrokenBlock(blockPosition)) return;

        Player player = entityplayer.getBukkitEntity();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        Location location = player.getEyeLocation();

        double distanceX = blockPosition.getX() - location.getX();
        double distanceY = blockPosition.getY() - location.getY();
        double distanceZ = blockPosition.getZ() - location.getZ();

        if (distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ >= 1024.0D) return;


        BrokenBlockHandlerList.BrokenBlock.ToolType toolType = Utils.isNullorAir(itemStack) ? BrokenBlockHandlerList.BrokenBlock.ToolType.FIST :
                itemStack.getType().name().endsWith("_PICKAXE") ? BrokenBlockHandlerList.BrokenBlock.ToolType.PICKAXE :
                        itemStack.getType().name().endsWith("_AXE") ? BrokenBlockHandlerList.BrokenBlock.ToolType.AXE : itemStack
                                .getType().name().endsWith("_SHOVEL") ? BrokenBlockHandlerList.BrokenBlock.ToolType.SHOVEL : BrokenBlockHandlerList.BrokenBlock.ToolType.FIST;

        BrokenBlockHandlerList.BrokenBlock.BreakType breakType1 = blockConfig.pickaxe(block) ? BrokenBlockHandlerList.BrokenBlock.BreakType.PICKAXE :
                blockConfig.axe(block) ? BrokenBlockHandlerList.BrokenBlock.BreakType.AXE : blockConfig
                        .shovel(block) ? BrokenBlockHandlerList.BrokenBlock.BreakType.SHOVEL : BrokenBlockHandlerList.BrokenBlock.BreakType.NONE;

        BrokenBlockHandlerList.BrokenBlock.BreakType breakType = BrokenBlockHandlerList.BrokenBlock.ToolType.match(toolType,breakType1);


        EquipmentHandler.Material material = null;

        try {
            material = EquipmentHandler.Material.valueOf(itemStack.getType().name().split("_")[0]);
        } catch (Exception ignored) {
        }

        double multiplier = (1 + (breakType != BrokenBlockHandlerList.BrokenBlock.BreakType.NONE ? switch (material) {
            case WOODEN -> 0.5;
            case STONE -> 1.25;
            case GOLDEN -> 1.5;
            case IRON -> 2;
            case DIAMOND -> 2.6;
            case NETHERITE -> 3.2;
            default -> 0;
        } : 0) )*(1+0.25*(Utils.isNullorAir(itemStack) ? 0 : itemStack.getEnchantmentLevel(Enchantment.DIG_SPEED)));

        brokenBlockHandlerList.getBrokenBlock(blockPosition).incrementDamage(player, multiplier*(1+0.2*(player.hasPotionEffect(
                PotionEffectType.FAST_DIGGING
        ) ? player.getPotionEffect(PotionEffectType.FAST_DIGGING).getAmplifier()+1 : 0)),breakType);
    }


}