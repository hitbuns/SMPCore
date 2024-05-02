package com.SMPCore.LootTableSystem;

import com.MenuAPI.ChatMessageResponseSystem.ChatMessageResponderHandlerList;
import com.MenuAPI.GUISystem.AbstractClickableGUI;
import com.MenuAPI.GUISystem.Button;
import com.MenuAPI.GUISystem.ButtonCreator;
import com.MenuAPI.Utilities.DecorationUtils;
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
import java.util.Comparator;
import java.util.List;

public class MaterialSelectorGUI extends AbstractClickableGUI implements ChatMessageResponderHandlerList.ChatMessageResponder {

    LootTableEntryModifier lootTableEntryModifier;

    public MaterialSelectorGUI(Player player, LootTableEntryModifier lootTableEntryModifier) {
        super(player, "Select a material to display for the reward",6,false);

        this.lootTableEntryModifier = lootTableEntryModifier;

        init();
    }

    @Override
    public int getGUIId() {
        return 9;
    }

    void init() {
        registerDefaultButtons();

        registerPriority(ButtonCreator.create(guiClickEvent -> backPage(),false,45,46,47,48).build());
        registerPriority(ButtonCreator.create(guiClickEvent -> nextPage(),false,50,51,52,53).build());
        registerPriority(new Button(49, guiClickEvent -> closePage(lootTableEntryModifier),false),
                new Button(40,guiClickEvent -> {
                    if (guiClickEvent.getClickType().isLeftClick()) {
                        ChatMessageResponderHandlerList.getInstance().addMessageResponders(getPlayer(), this);
                        close(getPlayer(), true);
                    } else if (searchQuery == null &&
                            guiClickEvent.getClickType().isRightClick()) {
                        resetQuery();
                    }


                },false));

        setupInventory();
    }

    void openPage(int page) {
        this.page = Math.max(0,page);

        clearPage();

        int counter = 0;


        for (int i = this.page*36; i < searchResult.length; i++) {
            if (counter < 36) getInventory().setItem(counter,a(i+1));
            counter++;
        }

    }

    Material[] searchResult = Arrays.stream(Material.values()).sorted(Comparator.comparing(Enum::name))
            .toArray(Material[]::new);

    void resetQuery() {
        searchQuery = null;
        searchResult = Arrays.stream(Material.values()).sorted(Comparator.comparing(Enum::name))
                .toArray(Material[]::new);

        updatePage();
    }

    ItemStack itemStack = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).build(false),
    barrier = new ItemBuilder(Material.BARRIER).setDisplayName("&7cNot A Valid Item").setLore(DescriptionBuilder
    .init().addLore(Utils.color("&eNot a valid material of choice")).build()).build(false);

    ItemStack a(int count) {
        if (searchResult.length < count) return itemStack.clone();

        Material material = searchResult[count-1];
        ItemStack itemStack = new ItemStack(material,1);

        if (itemStack == null) return this.itemStack.clone();

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return this.barrier.clone();
        meta.setDisplayName(Utils.color("&e"+material.name()));
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        lore.add("");
        lore.add(Utils.color("&7Left-click to select this item as the display material"));

        meta.setLore(lore);
        itemStack.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setString("clickValue",material.name());

        return nbtItem.getItem();
    }

    void nextPage() {
        this.page ++;
        openPage(this.page);
    }

    void backPage() {
        this.page--;
        openPage(this.page);
    }

    void clearPage() {
        DecorationUtils.decorate(getInventory(), Material.GRAY_STAINED_GLASS_PANE,0,36);
    }

    private int page = 0;

    public int getPage() {
        return page+1;
    }

    @Override
    public void registerDefaultButtons() {
        getDefaultMap().put("default", new Button(-1,guiClickEvent -> {
            ItemStack itemStack = guiClickEvent.getCurrentItem();
            if (Utils.isNullorAir(itemStack)) return;

            try {

                NBTItem nbtItem = new NBTItem(itemStack);

                this.lootTableEntryModifier.descriptiveLootTableEntry.setMaterial( Material.valueOf(nbtItem.getString("clickValue").toUpperCase()));

                openPage(this.lootTableEntryModifier);

            } catch (Exception ignored) {}

        },false));
        setDefaultAction("default");
    }

    @Override
    public void setupInventory() {

        DecorationUtils.decorate(getInventory(), Material.BLACK_STAINED_GLASS_PANE,36,45);

        openPage(this.page);

        DecorationUtils.decorate(getInventory(),new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE)
                .setDisplayName("&ePrevious Page").build(false),45,49);
        getInventory().setItem(49,new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .setDisplayName("&c&lClose Page").setLore(DescriptionBuilder.init()
                        .addLore("&e&oClick to close the menu").build()).build(false));
        DecorationUtils.decorate(getInventory(),new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                .setDisplayName("&aNext Page").build(false),50,54);
        getInventory().setItem(53,new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                .setDisplayName("&aNext Page").build(false));


    }

    void updatePage() {
        openPage(this.page = 0);
    }

    String searchQuery;

    @Override
    public void onOpen(Player player) {

        getInventory().setItem(40,new ItemBuilder(Material.COMPASS).setDisplayName("&7Search Result: "+
                (searchQuery != null ? "&e"+searchQuery : "&aALL")).setLore(DescriptionBuilder
        .init().addLore("&7&oLeft-click to do a search query",
                        "&7&oRight-click to clear search query").build()).build(false));

    }

    @Override
    public void onClose(Player player) {
        closePage(this.lootTableEntryModifier);
    }

    @Override
    public boolean run(Player player, String s) {

        this.searchQuery = s.toUpperCase();
        this.searchResult = Arrays.stream(Material.values()).filter(material ->
                material.name().startsWith(searchQuery)).toArray(Material[]::new);
        updatePage();
        ChatMessageResponderHandlerList.getInstance()
                .setGUIOpenable(getPlayer(),true);
        open(player);

        return true;
    }
}
