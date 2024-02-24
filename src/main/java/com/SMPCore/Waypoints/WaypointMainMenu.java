package com.SMPCore.Waypoints;

import com.MenuAPI.GUISystem.*;
import com.MenuAPI.Utilities.DecorationUtils;
import com.MenuAPI.Utilities.DescriptionBuilder;
import com.MenuAPI.Utilities.ItemBuilder;
import com.MenuAPI.Utilities.impl.HeadUtils;
import com.MenuAPI.Utils;
import de.tr7zw.nbtapi.NBTItem;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WaypointMainMenu extends AbstractModifiableListMenu<WaypointConfig.WayPointInfo> {

    public WaypointMainMenu(Player player, iPage backPage) {
        super(player, "Waypoints", backPage,3, WaypointConfig.WayPointInfo.class);

        wayPointInfos = WaypointConfig.getInstance().getWayPointRegistry().values().toArray(WaypointConfig.WayPointInfo[]::new);

        init(21,23,22,11,12,13,14,15);

        update();

    }

    void applyUpdates() {
        wayPointInfos = WaypointConfig.getInstance().getWayPointRegistry().values().toArray(WaypointConfig.WayPointInfo[]::new);
        update();
    }

    @Override
    public int getGUIId() {
        return 250;
    }

    ItemStack itemStack = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
            .setDisplayName("&cNo Further Waypoints")
            .setLore(DescriptionBuilder
                    .init()
                    .addLore("&7&oThis is not a waypoint!").build()).build(false);
    WaypointConfig.WayPointInfo[] wayPointInfos;

    @Override
    public ItemStack a(int i) {

        if (i > getIndexes().length) return getDefaultFillItem();

        WaypointConfig.WayPointInfo wayPointInfo = getIndexes()[i-1];
        ItemStack itemStack1 = wayPointInfo.getDisplayIcon();

        NBTItem nbtItem = new NBTItem(itemStack1);
        nbtItem.setString("clickValue", wayPointInfo.getId());

        return nbtItem.getItem();
    }

    @Override
    public WaypointConfig.WayPointInfo[] getIndexes() {
        return wayPointInfos;
    }

    @Override
    public ItemStack getDefaultFillItem() {
        return itemStack.clone();
    }

    @Override
    public GUIClickRunnable onGUIClick() {
        return guiClickEvent -> {

            ItemStack itemStack1 = guiClickEvent.getCurrentItem();

            if (Utils.isNullorAir(itemStack1)) return;

            NBTItem nbtItem = new NBTItem(itemStack1);

            if (nbtItem.hasKey("clickValue")) {
                try {
                    String id = nbtItem.getString("clickValue");
                    WaypointConfig.WayPointInfo wayPointInfo = WaypointConfig.getInstance()
                            .getWayPointRegistry().get(id);
                    switch (guiClickEvent.getClickType()) {
                        case LEFT,SHIFT_LEFT -> {
                            close(getPlayer(),true);
                            WaypointListener.Instance.getPlayerWayPoints()
                                    .put(getPlayer(),wayPointInfo);
                            getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                    TextComponent.fromLegacyText(Utils.color("&eYou have set your waypoint!")));
                        }
                        case RIGHT -> openPage(new WaypointRemovalPrompt(getPlayer(),wayPointInfo,
                                 null));
                    }
                } catch (Exception ignored) {

                }
            }

        };
    }

    @Override
    public void setupInventory() {

        DecorationUtils.decorate(getInventory(),Material.GRAY_STAINED_GLASS_PANE,
                0,27);
        DecorationUtils.fillItem(getInventory(),Material.BLACK_STAINED_GLASS_PANE);

        if (getPlayer().hasPermission("staff.admin")) {
            registerPriority(new ItemAssignedButton(new ItemBuilder(HeadUtils
                    .getItemHead("http://textures.minecraft.net/texture/171d8979c1878a05987a7faf21b56d1b744f9d068c74cffcde1ea1edad5852"))
                    .setDisplayName("&e&oAdd a new waypoint").build(false),
                    new Button(18,guiClickEvent -> {
                        close(getPlayer(),true);
                        getPlayer().sendMessage(Utils.color("&eTo create a new waypoint. Use the command /waypoint <location/name/material> <id> [value]"));
                    },false)));
        }

        registerPriority(new ItemAssignedButton(new ItemBuilder(Material.BARRIER)
                .setDisplayName("&cCancel current waypoint")
                .setLore(DescriptionBuilder
                        .init()
                        .addLore("&7>| Click to cancel your current waypoint").build()).build(false),
                new Button(9,guiClickEvent -> {

                    close(getPlayer());
                    WaypointListener.Instance.getPlayerWayPoints().remove(getPlayer());
                    getPlayer().sendMessage(Utils.color("&cYou have cancelled your current waypoint!"));

                },false)));
    }

    @Override
    public void onOpen(Player player) {
        applyUpdates();
    }

    @Override
    public void onClose(Player player) {

    }
}
