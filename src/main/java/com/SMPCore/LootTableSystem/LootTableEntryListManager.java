package com.SMPCore.LootTableSystem;

import com.MenuAPI.GUISystem.AbstractListMenu;
import com.MenuAPI.GUISystem.Button;
import com.MenuAPI.GUISystem.GUIClickRunnable;
import com.MenuAPI.Utilities.DescriptionBuilder;
import com.MenuAPI.Utilities.ItemBuilder;
import com.MenuAPI.Utils;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LootTableEntryListManager extends AbstractListMenu<RewardsAPI.LootTableEntry.DescriptiveLootTableEntry> {

    final LootTableCreator lootTableCreator;

    public LootTableEntryListManager(Player player, LootTableCreator lootTableCreator) {
        super(player, "Loot Table Id = "+lootTableCreator.id, lootTableCreator, false, RewardsAPI.LootTableEntry.DescriptiveLootTableEntry.class);


        this.lootTableCreator = lootTableCreator;
        updateEntries();

        init();


        getInventory().setItem(40,new ItemBuilder(Material.SUNFLOWER).setDisplayName("&6Add new Loot Table Entry")
                .setLore(DescriptionBuilder.init().addLore("&7Click to create a new loot table entry").build())
                .build(false));

        registerPriority(new Button(40,guiClickEvent -> {

            lootTableCreator.list.add(new RewardsAPI.LootTableEntry.DescriptiveLootTableEntry(null,100,
                    Material.BARREL,"&7This is a default description"));
            updateEntries();
            update();

        },false));


    }

    @Override
    public ItemStack a(int i) {

        if (descriptiveLootTableEntries == null || descriptiveLootTableEntries.length == 0 ||
        i > descriptiveLootTableEntries.length) return getEmptySlotPane();

        RewardsAPI.LootTableEntry.DescriptiveLootTableEntry descriptiveLootTableEntry = descriptiveLootTableEntries[i-1];

        ItemStack itemStack = new ItemBuilder(descriptiveLootTableEntry.getMaterial() != null ?
                descriptiveLootTableEntry.getMaterial() : Material.BARREL).setDisplayName("&6#"+i)
                .setLore(DescriptionBuilder.init().addLore("&7Command: ",
                        (descriptiveLootTableEntry.getCommand() != null ?
                                ("&e/"+descriptiveLootTableEntry.getCommand()) : "&cNo Command Set"),"&7Description: ",descriptiveLootTableEntry.getDescriptor()).build()).build(false);

        if (Utils.isNullorAir(itemStack)) return getEmptySlotPane();

        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setInteger("clickValue",i-1);


        return nbtItem.getItem();
    }

    RewardsAPI.LootTableEntry.DescriptiveLootTableEntry[] descriptiveLootTableEntries;

    void updateEntries() {
        descriptiveLootTableEntries = lootTableCreator.list.toArray(RewardsAPI.LootTableEntry.DescriptiveLootTableEntry[]::new);
    }



    @Override
    public RewardsAPI.LootTableEntry.DescriptiveLootTableEntry[] getIndexes() {
        return descriptiveLootTableEntries;
    }


    @Override
    public GUIClickRunnable getDefaultRunnable() {
        return guiClickEvent -> {

            ItemStack itemStack = guiClickEvent.getCurrentItem();

            if (Utils.isNullorAir(itemStack)) return;

            NBTItem nbtItem = new NBTItem(itemStack);
            if (nbtItem.hasKey("clickValue") && nbtItem.getType("clickValue") == NBTType.NBTTagInt) {

                int value = nbtItem.getInteger("clickValue");
                RewardsAPI.LootTableEntry.DescriptiveLootTableEntry descriptiveLootTableEntry = getIndexes()[value];
                switch (guiClickEvent.getClickType()) {
                    case LEFT,SHIFT_LEFT ->
                            openPage(new LootTableEntryModifier(getPlayer(),descriptiveLootTableEntry, this));
                    case RIGHT,SHIFT_RIGHT ->
                            openPage(new RemoveDescriptiveLootTableEntryPrompt(getPlayer(),this,
                            descriptiveLootTableEntry));
                }

            }


        };
    }

    @Override
    public int getGUIId() {
        return 35;
    }
}
