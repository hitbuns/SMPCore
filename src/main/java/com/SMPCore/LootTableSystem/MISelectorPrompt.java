package com.SMPCore.LootTableSystem;

import com.MenuAPI.ChatMessageResponseSystem.ChatMessageResponderHandlerList;
import com.MenuAPI.Configs.SaveItemConfig;
import com.MenuAPI.GUISystem.AbstractListMenu;
import com.MenuAPI.GUISystem.Button;
import com.MenuAPI.GUISystem.GUIClickRunnable;
import com.MenuAPI.Utilities.DescriptionBuilder;
import com.MenuAPI.Utilities.ItemBuilder;
import com.MenuAPI.Utils;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MISelectorPrompt extends AbstractListMenu<String> implements ChatMessageResponderHandlerList.ChatMessageResponder {

    LootTableEntryModifier lootTableEntryModifier;

    public MISelectorPrompt(Player player,LootTableEntryModifier lootTableEntryModifier) {
        super(player, "Select a reward item",lootTableEntryModifier, false, String.class);

        this.lootTableEntryModifier = lootTableEntryModifier;

        init();

        registerPriority(new Button(
                40,guiClickEvent -> {

            switch (guiClickEvent.getClickType()) {
                case LEFT,SHIFT_LEFT -> {
                    close(getPlayer(),true);
                    ChatMessageResponderHandlerList.getInstance().addMessageResponders(getPlayer(),this);
                    getPlayer().sendMessage(Utils.color("&ePlease provide a valid search result input. To cancel this process, type 'cancel'"));
                }
                case RIGHT,SHIFT_RIGHT -> applySearchResult(null);
            }

        },false));

        getInventory().setItem(40,new ItemBuilder(Material.SUNFLOWER)
                .setDisplayName("&aSearch Result: &a"+(searchResult != null ?
                        searchResult.toLowerCase() : "ALL")).setLore(DescriptionBuilder.init().addLore("&7>| Left Click to narrow search result",
                        "&7>| Right Click to clear search result").build())
                .build(false));

    }

    void applySearchResult(String searchResult) {
        this.searchResult = searchResult;
        if (searchResult != null)
            strings = SaveItemConfig.getInstance().getListIds()
                    .stream().filter(s -> s.toLowerCase().contains(searchResult)).sorted(String.CASE_INSENSITIVE_ORDER).toArray(String[]::new);
        else strings = SaveItemConfig.getInstance().getListIds()
                .stream().sorted(String.CASE_INSENSITIVE_ORDER).toArray(String[]::new);
        update();
    }

    String[] strings = SaveItemConfig.getInstance().getListIds()
            .stream().sorted(String.CASE_INSENSITIVE_ORDER).toArray(String[]::new);

    @Override
    public ItemStack a(int i) {

        if (i > strings.length) return getEmptySlotPane();

        String s = strings[i-1];
        ItemStack itemStack = SaveItemConfig.getInstance().loadItem(s);

        if (Utils.isNullorAir(itemStack)) return new ItemBuilder(Material.BARRIER).setDisplayName("&cNot a valid item!").build(false);

        itemStack = itemStack.clone();
        ItemMeta meta = itemStack.getItemMeta();

        if (meta == null) return itemStack;

        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        lore.addAll(Utils.color(Arrays.asList("","&eLeft-click to select this item as the reward")));
        meta.setLore(lore);
        itemStack.setItemMeta(meta);

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

            if (Utils.isNullorAir( itemStack)) return;

            NBTItem nbtItem = new NBTItem(itemStack);
            String s = nbtItem.getString("clickValue");

            if (s == null) return;

            lootTableEntryModifier.descriptiveLootTableEntry.setCommand("mi load "+s+" %player% 1");
            openPage(lootTableEntryModifier);

        };
    }

    @Override
    public int getGUIId() {
        return 37;
    }

    String searchResult;

    @Override
    public boolean run(Player player, String input) {

        if (input.equalsIgnoreCase("cancel")) {
            ChatMessageResponderHandlerList.getInstance().setGUIOpenable(player,true);
            open(player);
            return false;
        }


        applySearchResult(input.toLowerCase());

        ChatMessageResponderHandlerList.getInstance().setGUIOpenable(player,true);
        open(player);
        return true;
    }

    @Override
    public void onOpen(Player player) {
        super.onOpen(player);
        update();
    }
}
