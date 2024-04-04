package com.SMPCore.gui;

import com.MenuAPI.GUISystem.AbstractModifiableListMenu;
import com.MenuAPI.GUISystem.GUIClickRunnable;
import com.MenuAPI.GUISystem.iPage;
import com.MenuAPI.Utilities.BukkitLimitTask;
import com.MenuAPI.Utilities.DecorationUtils;
import com.MenuAPI.Utilities.DescriptionBuilder;
import com.MenuAPI.Utilities.ItemBuilder;
import com.MenuAPI.Utilities.impl.HeadUtils;
import com.MenuAPI.Utils;
import com.SMPCore.Main;
import com.SMPCore.Utilities.TempEntityDataHandler;
import com.earth2me.essentials.commands.WarpNotFoundException;
import de.tr7zw.nbtapi.NBTItem;
import net.ess3.api.InvalidWorldException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WarpGUI extends AbstractModifiableListMenu<String> {

    static String[] warps;

    public WarpGUI(Player player,  iPage backPage) {
        super(player, "Warps", backPage, 4, String.class);

        if (warps == null) warps = Main.Instance.essentials.getWarps().getList().stream().sorted((o1, o2) -> {
            try {
                World.Environment environment1 = Main.Instance.essentials.getWarps().getWarp(o1).getWorld().getEnvironment(),
                        environment2 = Main.Instance.essentials.getWarps().getWarp(o2).getWorld().getEnvironment();

                return Integer.compare(
                        switch (environment1) {
                            case NORMAL -> 0;
                            case CUSTOM -> -1;
                            case NETHER -> -2;
                            case THE_END -> -3;
                        },
                        switch (environment2) {
                            case NORMAL -> 0;
                            case CUSTOM -> -1;
                            case NETHER -> -2;
                            case THE_END -> -3;
                        }
                );
            } catch (WarpNotFoundException | InvalidWorldException e) {
                return -3;
            }
        }).toArray(String[]::new);

        TempEntityDataHandler.EntityData entityData = TempEntityDataHandler.getorAdd(player);

        TeleportRequestHandler teleportRequestHandler = entityData.get("teleportRequest", TeleportRequestHandler.class,null);

        if (teleportRequestHandler != null) {
            teleportRequestHandler.setCancelled(true);
            entityData.updateData("teleportRequest", TeleportRequestHandler.class, initial ->
                    null, null);
        }

        init();
    }

    void init() {


        init(30,32,31,11,12,13,14,15,21,22,23);

        getInventory().setItem(30,new ItemBuilder(HeadUtils.getItemHead(
                "http://textures.minecraft.net/texture/bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9"
        )).setDisplayName("&cPrevious").build(false));

        getInventory().setItem(32,new ItemBuilder(HeadUtils.getItemHead(
                "http://textures.minecraft.net/texture/19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf"
        )).setDisplayName("&aNext").build(false));


    }

    @Override
    public ItemStack a(int i) {

        if (i > getIndexes().length) return getDefaultFillItem().clone();

        String s = warps[i-1];
        try {
            Block location = Main.Instance.essentials.getWarps()
                    .getWarp(s).getBlock();
            World world = location.getLocation().getWorld();
            ItemStack itemStack1 = new ItemBuilder(HeadUtils.getItemHead(switch (world.getEnvironment()) {
                case NORMAL -> "http://textures.minecraft.net/texture/597e4e27a04afa5f06108265a9bfb797630391c7f3d880d244f610bb1ff393d8";
                case NETHER -> "http://textures.minecraft.net/texture/71c5db83105d6a5141b8385b1ef409415517e5e75cfbce329ef35d45b07ab9ac";
                case THE_END -> "http://textures.minecraft.net/texture/8a108a0a7a387859f2c44fb9702cf73dbafee3ecfdc4f5def46c0d651b7a49f7";
                case CUSTOM -> "http://textures.minecraft.net/texture/badc048a7ce78f7dad72a07da27d85c0916881e5522eeed1e3daf217a38c1a";
            })).setDisplayName("&eWarp: &a"+s).setLore(DescriptionBuilder
                    .init()
                            .addLore("&7Location: &a("+world.getName()+","+location.getX()+","+
                                    location.getY()+","+location.getZ())
                    .build()).build(false);
            NBTItem nbtItem = new NBTItem(itemStack1);
            nbtItem.setString("warpValue",s);

            return nbtItem.getItem();
        } catch (WarpNotFoundException | InvalidWorldException ignored) {

        }


        return new ItemBuilder(HeadUtils.getItemHead("http://textures.minecraft.net/texture/3ed1aba73f639f4bc42bd48196c715197be2712c3b962c97ebf9e9ed8efa025")).setDisplayName("&4[!] &cFailed to load warp!").build(false);
    }

    @Override
    public String[] getIndexes() {
        return warps;
    }

    @Override
    public ItemStack getDefaultFillItem() {
        return itemStack;
    }

    ItemStack itemStack = new ItemBuilder(HeadUtils.getItemHead("http://textures.minecraft.net/texture/3ed1aba73f639f4bc42bd48196c715197be2712c3b962c97ebf9e9ed8efa025")).setDisplayName("&cNo more warps...").build(false);

    @Override
    public GUIClickRunnable onGUIClick() {
        return guiClickEvent -> {

            ItemStack itemStack1 = guiClickEvent.getCurrentItem();

            if (Utils.isNullorAir(itemStack1)) return;

            NBTItem nbtItem = new NBTItem(itemStack1);
            if (nbtItem.hasKey("warpValue")) {

                String s = nbtItem.getString("warpValue");

                if (!getPlayer().hasPermission("essentials.warps."+s)) {
                    getPlayer().sendMessage(Utils.color("&cYou lack permission 'essentials.warps."+s+"' to use this warp!"));
                    return;
                }

                try {
                    Location location = Main.Instance.essentials.getWarps().getWarp(s).getBlock().getLocation().clone();
                    location.setY(location.getY()+1);
                    new TeleportRequestHandler(getPlayer(),s,location);
                    close(getPlayer());
                } catch (WarpNotFoundException | InvalidWorldException e) {
                    throw new RuntimeException(e);
                }

            }

        };
    }

    @Override
    public int getGUIId() {
        return 200;
    }

    @Override
    public void setupInventory() {

        DecorationUtils.border(getInventory(), Material.BLACK_STAINED_GLASS_PANE);
        DecorationUtils.fillItem(getInventory(),Material.GRAY_STAINED_GLASS_PANE);

    }

    @Override
    public void onOpen(Player player) {

    }

    @Override
    public void onClose(Player player) {

    }

    public static class TeleportRequestHandler extends BukkitLimitTask {

        final Player player;
        final Location location;
        final String warp;
        private boolean cancelled = false;

        public TeleportRequestHandler(Player player,String warp,Location location) {
            super(0, 20, 7);
            this.player = player;
            this.location = location;
            this.warp = warp;
            TempEntityDataHandler.getorAdd(player).updateData("teleportRequest", TeleportRequestHandler.class,initial -> this,null);
        }

        public void setCancelled(boolean cancelled) {
            this.cancelled =cancelled;
            if (cancelled) {
                setCountMax(getCountMax());
                player.sendMessage(Utils.color("&cTeleport was cancelled!"));
                player.sendTitle(null,Utils.color("&cTeleport was cancelled!"),10,30,10);
            }
        }

        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void run() {
            super.run();

            if (cancelled) {
                return;
            }

            long v = getCountMax()-getCurrentCount();
            if (v <= 0) {
                player.teleport(location);
                return;
            }

            player.sendTitle(Utils.color("&eTeleporting to "+warp+" in"),
                    Utils.color((v > 5 ? "&a" : v > 3 ? "&6" : "&c")+String.valueOf(v).replace("L","")),5,40,10);


        }
    }


}
