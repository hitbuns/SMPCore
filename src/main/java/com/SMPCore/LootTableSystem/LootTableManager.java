package com.SMPCore.LootTableSystem;

import com.MenuAPI.GUISystem.AbstractListMenu;
import com.MenuAPI.GUISystem.GUIClickRunnable;
import com.MenuAPI.GUISystem.iPage;
import com.MenuAPI.Utilities.DescriptionBuilder;
import com.MenuAPI.Utilities.ItemBuilder;
import com.MenuAPI.Utils;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LootTableManager extends AbstractListMenu<String> {

    public LootTableManager(Player player,iPage backPage) {
        super(player, "&7Loot Table Entries Manager", backPage, false,String.class);

        updateEntries();

        init();
    }

    String[] strings;

    void updateEntries() {
        this.strings = RewardsAPI.getInstance().getorAddConfigurationSection("rewards").getKeys(false).toArray(String[]::new);
    }

    @Override
    public ItemStack a(int i) {

        if (i > strings.length) return getEmptySlotPane();

        String s = strings[i-1];
        ItemStack itemStack = new ItemBuilder(Material.BARREL).setDisplayName("&8[&6#"+i+"&8] &7Id: &e"+s)
                .setLore(DescriptionBuilder.init().addLore("&7Left-click to modify this loot table collection",
                        "&7Right-click to delete this collection").build()).build(false);

        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setString("clickValue",s);

        return nbtItem.getItem();
    }

    @Override
    public String[] getIndexes() {
        return strings;
    }

    @Override
    public GUIClickRunnable getDefaultRunnable() {
        return guiClickEvent -> {

            ItemStack itemStack = guiClickEvent.getCurrentItem();

            if (Utils.isNullorAir(itemStack)) return;

            NBTItem nbtItem = new NBTItem(itemStack);
            String value = nbtItem.getString("clickValue");

            if (value == null || value.equalsIgnoreCase("")) return;

            switch (guiClickEvent.getClickType()) {
                case LEFT,SHIFT_LEFT -> openPage(new LootTableCreator(getPlayer(),value,this));
                case RIGHT,SHIFT_RIGHT -> openPage(new LootTableCollectionRemovePrompt(getPlayer(),value,this));

            }



        };
    }

    @Override
    public int getGUIId() {
        return 38;
    }
}
