package com.SMPCore.gui;

import com.MenuAPI.GUISystem.AbstractListMenu;
import com.MenuAPI.GUISystem.GUIClickRunnable;
import com.MenuAPI.GUISystem.iPage;
import com.MenuAPI.Utilities.DescriptionBuilder;
import com.MenuAPI.Utilities.ItemBuilder;
import com.SMPCore.configs.CraftConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CraftAdminListManager extends AbstractListMenu<String> {

    CraftType craftType;

    public enum CraftType {

        WORKBENCH(9),
        FURNACE(1),
        ANVIL(2)
        ;

        CraftType(int inputRequired) {
            this.inputRequired = inputRequired;
        }

        public final int inputRequired;

    }

    public CraftAdminListManager(Player player,iPage backPage) {
        this(player,CraftType.WORKBENCH,backPage);
    }

    public CraftAdminListManager(Player player,CraftType craftType,iPage backPage) {
        super(player, "Manage Craft Entries", backPage, false, String.class);
        this.craftType = craftType;

        updateKeys();
    }

    void updateKeys() {
        this.keys = CraftConfig.Instance.getKeys(craftType);
    }

    String[] keys;


    @Override
    public ItemStack a(int i) {

        if (i > keys.length) return getEmptySlotPane();

        String key = keys[i-1];

        ItemStack itemStack = new ItemBuilder(Material.NAME_TAG)
                .setDisplayName("&eRecipe: &a"+key)
                .setLore(DescriptionBuilder
                        .init().addLore("&7&oLeft-click to ")
                        .build()).build(false);

        return null;
    }

    @Override
    public String[] getIndexes() {
        return keys;
    }

    @Override
    public GUIClickRunnable getDefaultRunnable() {
        return guiClickEvent -> {

        };
    }

    @Override
    public int getGUIId() {
        return 1002;
    }
}
