package com.SMPCore.gui;

import com.MenuAPI.GUISystem.AbstractClickableGUI;
import com.MenuAPI.GUISystem.Button;
import com.MenuAPI.GUISystem.ItemAssignedButton;
import com.MenuAPI.Utilities.DecorationUtils;
import com.MenuAPI.Utilities.DescriptionBuilder;
import com.MenuAPI.Utilities.ItemBuilder;
import com.MenuAPI.Utils;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BooleanFlagEditor extends AbstractClickableGUI {

    RegionFlagEditor regionFlagEditor;
    BooleanFlag stateFlag;

    public BooleanFlagEditor(Player player, RegionFlagEditor regionFlagEditor, BooleanFlag stateFlag) {
        super(player, "Editing "+regionFlagEditor.protectedRegion.getId()+" flag id '"+stateFlag.getName()+"'", 3, false);

        this.regionFlagEditor = regionFlagEditor;
        this.stateFlag = stateFlag;

        init();

    }

    void init() {

        registerDefaultButtons();

        setupInventory();
    }

    @Override
    public int getGUIId() {
        return 102;
    }

    void update() {
        boolean state = regionFlagEditor.protectedRegion.getFlag(stateFlag).booleanValue();

        getInventory().setItem(10,new ItemBuilder(Material.ITEM_FRAME)
                .setDisplayName("&e"+stateFlag.getName()).setLore(DescriptionBuilder.init()
                        .addLore("&7Value: &a"+state).build())
                .build(false));
    }

    @Override
    public void registerDefaultButtons() {
        getDefaultMap().put("default",new Button(-1, guiClickEvent ->
        {

            ItemStack itemStack = guiClickEvent.getCurrentItem();
            if (Utils.isNullorAir(itemStack)) return;
            NBTItem nbtItem = new NBTItem(itemStack);

            if (nbtItem.hasKey("clickValue")) {

                regionFlagEditor.protectedRegion.setFlag(stateFlag, nbtItem.getBoolean("clickValue"));
                update();

            }


        },false));
        setDefaultAction("default");
    }

    @Override
    public void setupInventory() {
        DecorationUtils.border(getInventory(),Material.BLACK_STAINED_GLASS_PANE);
        DecorationUtils.fillItem(getInventory(),Material.GRAY_STAINED_GLASS_PANE);

        for (int i = 0; i < 2; i++) {
            boolean state = i == 0;
            ItemStack itemStack = new ItemBuilder(state ? Material.LIME_STAINED_GLASS_PANE :
                    Material.RED_STAINED_GLASS_PANE).setDisplayName("&e"+stateFlag.getName()).setLore(DescriptionBuilder
                    .init().addLore("&7>| Click to select modify this flag to this state.").build()).build(false);
            NBTItem nbtItem = new NBTItem(itemStack);
            nbtItem.setBoolean("clickValue",state);
            getInventory().setItem(12+i,nbtItem.getItem());
        }

        registerPriority(new ItemAssignedButton(new ItemBuilder(Material.ARROW)
                .setDisplayName("&cBack").build(false),new Button(22,guiClickEvent -> openPage(regionFlagEditor),
                false)));

    }

    @Override
    public void onOpen(Player player) {

    }

    @Override
    public void onClose(Player player) {

    }

}
