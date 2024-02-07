package com.SMPCore.gui;

import com.MenuAPI.GUISystem.AbstractModifiableListMenu;
import com.MenuAPI.GUISystem.GUIClickRunnable;
import com.MenuAPI.GUISystem.iPage;
import com.MenuAPI.Utilities.DecorationUtils;
import com.MenuAPI.Utilities.DescriptionBuilder;
import com.MenuAPI.Utilities.ItemBuilder;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.RegionGroupFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class StateFlagGroupEditor extends AbstractModifiableListMenu<RegionGroup> {

    StateFlag.State state;
    StateFlagEditor stateFlagEditor;

    public StateFlagGroupEditor(Player player, StateFlagEditor stateFlagEditor, StateFlag.State state) {
        super(player, "Selecting target group for flag id "+stateFlagEditor.stateFlag.getName(), stateFlagEditor.regionFlagEditor, 3, RegionGroup.class);

        this.stateFlagEditor = stateFlagEditor;
        this.state =state;

        init(9,17,22,10,11,12,14,15,16);

        unregisterPreviousButton();
        unregisterNextButton();

        DecorationUtils.decorate(getInventory(),Material.BLACK_STAINED_GLASS_PANE,false,9,17);
    }

    static final RegionGroup[] regionGroups = RegionGroup.values();
    static final Map<RegionGroup, Color> regionColors = Arrays.stream(regionGroups)
            .collect(Collectors.toMap(regionGroup -> regionGroup,regionGroup -> switch (regionGroup) {
                case ALL -> Color.fromRGB(227, 227, 227);
                case NONE -> Color.fromRGB(31, 31, 31);
                case OWNERS -> Color.fromRGB(110, 4, 4);
                case MEMBERS -> Color.fromRGB(50, 166, 8);
                case NON_OWNERS -> Color.fromRGB(89, 30, 217);
                case NON_MEMBERS -> Color.fromRGB(13, 150, 191);
            }));


    @Override
    public ItemStack a(int i) {

        if (i > regionGroups.length) return getDefaultFillItem().clone();

        RegionGroup regionGroup = regionGroups[i-1];

        ItemStack itemStack = new ItemBuilder(Material.LEATHER_CHESTPLATE)
                .setDisplayName("&eGroup: &a"+regionGroup.name()).color(regionColors.getOrDefault(regionGroup,Color.fromRGB(255,255,255)))
                .setLore(DescriptionBuilder.init().addLore("&7>| Click to select this as the target",
                        "   &7group that is affected by this worldguard flag").build()).build(false);

        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setInteger("clickValue",i-1);

        return nbtItem.getItem();
    }

    @Override
    public RegionGroup[] getIndexes() {
        return regionGroups;
    }

    ItemStack itemStack = new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
            .setDisplayName("&cX").build(false);

    @Override
    public ItemStack getDefaultFillItem() {
        return itemStack;
    }

    @Override
    public GUIClickRunnable onGUIClick() {
        return guiClickEvent -> {

            stateFlagEditor.regionFlagEditor.protectedRegion.setFlag(stateFlagEditor.stateFlag, state);
            stateFlagEditor.regionFlagEditor.protectedRegion.setFlag(stateFlagEditor.stateFlag.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);

            openPage(stateFlagEditor.regionFlagEditor);


        };
    }

    @Override
    public int getGUIId() {
        return 103;
    }

    @Override
    public void setupInventory() {

        DecorationUtils.border(getInventory(),Material.BLACK_STAINED_GLASS_PANE);
        DecorationUtils.fillItem(getInventory(),Material.GRAY_STAINED_GLASS_PANE);




    }

    @Override
    public void onOpen(Player player) {

    }

    @Override
    public void onClose(Player player) {

    }
}
