package com.SMPCore.gui;

import com.MenuAPI.GUISystem.AbstractClickableGUI;
import com.MenuAPI.GUISystem.Button;
import com.MenuAPI.GUISystem.ItemAssignedButton;
import com.MenuAPI.Utilities.DecorationUtils;
import com.MenuAPI.Utilities.DescriptionBuilder;
import com.MenuAPI.Utilities.ItemBuilder;
import com.MenuAPI.Utils;
import com.sk89q.worldguard.protection.flags.StateFlag;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StateFlagEditor extends AbstractClickableGUI {

    RegionFlagEditor regionFlagEditor;
    StateFlag stateFlag;

    public StateFlagEditor(Player player, RegionFlagEditor regionFlagEditor, StateFlag stateFlag) {
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
        StateFlag.State state = regionFlagEditor.protectedRegion.getFlag(stateFlag);

        if (state == null) state = stateFlag.getDefault();

        getInventory().setItem(10,new ItemBuilder(Material.ITEM_FRAME)
                .setDisplayName("&e"+stateFlag.getName()).setLore(DescriptionBuilder.init()
                                .addLore("&7Value: &a"+(state != null ? state.name() :
                                        "<NOT SET>")).build())
                .build(false));
    }

    @Override
    public void registerDefaultButtons() {
        getDefaultMap().put("default",new Button(-1,guiClickEvent ->
        {

            ItemStack itemStack = guiClickEvent.getCurrentItem();
            if (Utils.isNullorAir(itemStack)) return;
            NBTItem nbtItem = new NBTItem(itemStack);

            if (nbtItem.hasKey("clickValue")) {


                openPage(new StateFlagGroupEditor(getPlayer(),this,StateFlag.State.valueOf(nbtItem.getString("clickValue").toUpperCase())));


            }


        },false));
        setDefaultAction("default");
    }

    @Override
    public void setupInventory() {
        DecorationUtils.border(getInventory(),Material.BLACK_STAINED_GLASS_PANE);
        DecorationUtils.fillItem(getInventory(),Material.GRAY_STAINED_GLASS_PANE);

        StateFlag.State[] values = StateFlag.State.values();
        for (int i = 0; i < values.length; i++) {
            StateFlag.State state = values[i];
            ItemStack itemStack = new ItemBuilder(switch (state) {
                case DENY -> Material.RED_STAINED_GLASS_PANE;
                case ALLOW -> Material.LIME_STAINED_GLASS_PANE;
            }).setDisplayName("&e"+stateFlag.getName()).setLore(DescriptionBuilder
                    .init().addLore("&7>| Click to select modify this flag to this state.").build()).build(false);
            NBTItem nbtItem = new NBTItem(itemStack);
            nbtItem.setString("clickValue",state.name());
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
