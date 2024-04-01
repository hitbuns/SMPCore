package com.SMPCore.mining;

import com.MenuAPI.BukkitEventCaller;
import com.MenuAPI.Utils;
import com.SMPCore.Utilities.SoundPlayerUtils;
import com.SMPCore.skills.PlayerDataHandler;
import com.SMPCore.skills.impl.NonCombatStatType;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BrokenBlockHandlerList {



    private final static Map<Location, BrokenBlock> brokenBlocks = new HashMap<>();
    private static BrokenBlockHandlerList Instance;

    public static BrokenBlockHandlerList getInstance() {
        return Instance;
    }

    public BrokenBlockHandlerList() {
        Instance = this;
    }

    public static Map<Location, BrokenBlock> getBrokenBlocks() {
        return brokenBlocks;
    }

    public void createBrokenBlock(Block block) {
        createBrokenBlock(block, -1);
    }

    public void createBrokenBlock(Block block, int time) {
        if (isBrokenBlock(block.getLocation())) return;
        BrokenBlock brokenBlock;
        if (time == -1) brokenBlock = new BrokenBlock(block,100);
        else brokenBlock = new BrokenBlock(block, time);
        brokenBlocks.put(block.getLocation(), brokenBlock);
    }

    public void removeBrokenBlock(Location location) {
        brokenBlocks.remove(location);
    }

    public BrokenBlock getBrokenBlock(Location location) {
        createBrokenBlock(location.getBlock());
        return brokenBlocks.get(location);
    }

    public boolean isBrokenBlock(Location location) {
        return brokenBlocks.containsKey(location);
    }


    public static class BrokenBlock {


        public enum ToolType {


            SHOVEL(block1 -> block1 == BreakType.SHOVEL ? BreakType.SHOVEL : BreakType.NONE),
            PICKAXE(block1 -> block1 == BreakType.PICKAXE ? BreakType.PICKAXE : BreakType.NONE),
            AXE(block1 -> block1 == BreakType.AXE ? BreakType.AXE : BreakType.NONE),
            FIST(block1 -> BreakType.NONE)
            ;

            public final iToolListener toolListener;

            ToolType(iToolListener toolListener) {
                this.toolListener = toolListener;
            }

            public interface iToolListener {

                BreakType checkToolAgainst(BreakType block);

            }

            public static BreakType match(ToolType toolType,BreakType block) {
                return toolType.toolListener.checkToolAgainst(block);
            }

        }

        public enum BreakType {

            SHOVEL(),
            PICKAXE(),
            AXE(),
            NONE()
            ;


        }

        private int time;
        private int oldAnimation;
        private double damage = -1;
        private final Block block;
        private long lastDamage;

        public BrokenBlock(Block block, int time) {
            this.block = block;
            this.time = time;
            lastDamage = System.currentTimeMillis();
        }

        public void incrementDamage(Player from, double multiplier,BreakType breakType) {
            if (isBroken()) return;


            Bukkit.broadcastMessage(damage+"_"+getAnimation()+"_"+multiplier);

            double odds = 25+0.6* (switch (breakType) {
                case PICKAXE -> PlayerDataHandler.getLevel(from,
                        NonCombatStatType.MINING);
                case AXE -> PlayerDataHandler.getLevel(from,
                        NonCombatStatType.WOODCUTTING);
                case SHOVEL -> 100;
                default -> -15;
            });

            if (Utils.RNG(0,100) < 100-odds) return;

            damage += multiplier;
            int animation = getAnimation();

            if (animation != oldAnimation) {
                if (animation < 10) {
                    sendBreakPacket(animation,block);
                    lastDamage = System.currentTimeMillis();
                } else {

                    if (BukkitEventCaller.callEvent(new CustomBlockBreakEvent(from,block))) {
                        sendBreakPacket(0, block);
                        damage = 0;
                        lastDamage = System.currentTimeMillis();
                        return;
                    }

                    breakBlock(from);
                    return;
                }
            }

            oldAnimation = animation;
        }

        public boolean isBroken() {
            return getAnimation() >= 10;
        }

        public void breakBlock(Player breaker) {
            destroyBlockObject();
            SoundPlayerUtils.playBlockSound(block);
            if (breaker == null) return;
            breaker.breakBlock(block);
        }

        public void destroyBlockObject() {
            sendBreakPacket(-1,block);
            //  Here you have to remove your BrokenBlock using the BrokenBlocksService, on the next step
            BrokenBlockHandlerList.getInstance().removeBrokenBlock(block.getLocation());
        }

        public int getAnimation() {
            return (int) (damage / time * 11) - 1;
        }

        public void sendBreakPacket(int animation, Block block) {
            PacketPlayOutBlockBreakAnimation packetPlayOutBlockBreakAnimation = new PacketPlayOutBlockBreakAnimation(getBlockEntityId(block), getBlockPosition(block), animation);
            block.getWorld().getNearbyEntities(block.getLocation(),10,10,10).stream().map(entity -> {
                try {
                    return (Player) entity;
                } catch (Exception exception) {
                    return null;
                }
            }).filter(Objects::nonNull).forEach(player ->
                    ((CraftPlayer) player).getHandle().c.b(packetPlayOutBlockBreakAnimation));
//            ((CraftServer) Bukkit.getServer()).getHandle().a(null, block.getX(), block.getY(), block.getZ(), 120, ((CraftWorld) block.getLocation().getWorld()).getHandle().dimension,
//                    new PacketPlayOutBlockBreakAnimation(getBlockEntityId(block), getBlockPosition(block), animation));
        }


        private BlockPosition getBlockPosition(Block block) {
            return new BlockPosition(block.getX(), block.getY(), block.getZ());
        }

        private int getBlockEntityId(Block block) {
            return ((block.getX() & 0xFFF) << 20 | (block.getZ() & 0xFFF) << 8) | (block.getY() & 0xFF);
        }


    }

}
