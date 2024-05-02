package com.SMPCore.skills.gui;

import com.MenuAPI.GUISystem.AbstractClickableGUI;
import com.MenuAPI.GUISystem.Button;
import com.MenuAPI.GUISystem.ItemAssignedButton;
import com.MenuAPI.GUISystem.iPage;
import com.MenuAPI.Utilities.DecorationUtils;
import com.MenuAPI.Utilities.DescriptionBuilder;
import com.MenuAPI.Utilities.ItemBuilder;
import com.MenuAPI.Utilities.impl.HeadUtils;
import com.SMPCore.skills.impl.AbilityIntentionType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class AbilityMainMenu extends AbstractClickableGUI {

    final iPage backPage;

    public AbilityMainMenu(Player player, iPage backPage) {
        super(player, "Ability Selector", 4, false);
        this.backPage = backPage;

        init();
    }

    void init() {
        registerDefaultButtons();

        registerPriority(new ItemAssignedButton(new ItemBuilder(Material.DIAMOND_ORE)
                .setGlowing(true).setDisplayName("&bMining Abilities").setLore(DescriptionBuilder.init()
                        .addLore("&7&m    &e Click to select a mining ability &7&m    ")
                        .build()).build(false), new Button(11,guiClickEvent ->
                openPage(new AbilitySelectorMenu(getPlayer(), AbilityIntentionType.MINING,this)),false)),
                new ItemAssignedButton(new ItemBuilder(Material.WHEAT)
                        .setGlowing(true).setDisplayName("&bFarming Abilities").setLore(DescriptionBuilder.init()
                                .addLore("&7&m    &e Click to select a farming ability &7&m    ")
                                .build()).build(false), new Button(12,guiClickEvent ->
                        openPage(new AbilitySelectorMenu(getPlayer(), AbilityIntentionType.FARMING,this)),false)),
                        new ItemAssignedButton(new ItemBuilder(Material.IRON_SWORD)
                                .setGlowing(true).setDisplayName("&bSword Abilities").setLore(DescriptionBuilder.init()
                                        .addLore("&7&m    &e Click to select a sword ability &7&m    ")
                                        .build()).build(false), new Button(13,guiClickEvent ->
                                openPage(new AbilitySelectorMenu(getPlayer(), AbilityIntentionType.SWORD,this)),false)),
                                new ItemAssignedButton(new ItemBuilder(Material.BOW)
                                        .setGlowing(true).setDisplayName("&bRanged Combat Abilities").setLore(DescriptionBuilder.init()
                                                .addLore("&7&m    &e Click to select a ranged combat ability &7&m    ")
                                                .build()).build(false), new Button(14,guiClickEvent ->
                                        openPage(new AbilitySelectorMenu(getPlayer(), AbilityIntentionType.RANGED_COMBAT,this)),false)),
                new ItemAssignedButton(new ItemBuilder(Material.IRON_CHESTPLATE)
                        .setGlowing(true).setDisplayName("&bPassive Defense Abilities").setLore(DescriptionBuilder.init()
                                .addLore("&7&m    &e Click to select a passive defense ability &7&m    ")
                                .build()).build(false), new Button(15,guiClickEvent ->
                        openPage(new AbilitySelectorMenu(getPlayer(), AbilityIntentionType.DEFENSE_PASSIVE,this)),false)),
                new ItemAssignedButton(new ItemBuilder(Material.ENCHANTED_BOOK)
                        .setGlowing(true).setDisplayName("&bEnchanting Abilities").setLore(DescriptionBuilder.init()
                                .addLore("&7&m    &e Click to select a enchanting ability &7&m    ")
                                .build()).build(false), new Button(20,guiClickEvent ->
                        openPage(new AbilitySelectorMenu(getPlayer(), AbilityIntentionType.ENCHANTING,this)),false)),
                new ItemAssignedButton(new ItemBuilder(Material.IRON_AXE)
                        .setGlowing(true).setDisplayName("&bWoodcutting & Melee Axe Abilities").setLore(DescriptionBuilder.init()
                                .addLore("&7&m    &e Click to select a woodcutting or axe ability &7&m    ")
                                .build()).build(false), new Button(21,guiClickEvent ->
                        openPage(new AbilitySelectorMenu(getPlayer(), AbilityIntentionType.AXE,this)),false)),
                new ItemAssignedButton(new ItemBuilder(Material.FISHING_ROD)
                        .setGlowing(true).setDisplayName("&bFishing Abilities").setLore(DescriptionBuilder.init()
                                .addLore("&7&m    &e Click to select a fishing ability &7&m    ")
                                .build()).build(false), new Button(23,guiClickEvent ->
                        openPage(new AbilitySelectorMenu(getPlayer(), AbilityIntentionType.FISHING,this)),false)),
                new ItemAssignedButton(new ItemBuilder(Material.DIAMOND_SHOVEL)
                        .setGlowing(true).setDisplayName("&bExcavating Abilities").setLore(DescriptionBuilder.init()
                                .addLore("&7&m    &e Click to select a excavating ability &7&m    ")
                                .build()).build(false), new Button(24,guiClickEvent ->
                        openPage(new AbilitySelectorMenu(getPlayer(), AbilityIntentionType.EXCAVATION,this)),false))
        );

        setupInventory();
    }

    @Override
    public int getGUIId() {
        return 300;
    }

    @Override
    public void registerDefaultButtons() {
        getDefaultMap().put("default",new Button(-1,guiClickEvent -> {},false));
        setDefaultAction("default");
    }

    @Override
    public void setupInventory() {

        DecorationUtils.border(getInventory(), Material.BLACK_STAINED_GLASS_PANE);
        DecorationUtils.fillItem(getInventory(),Material.GRAY_STAINED_GLASS_PANE);

        registerPriority(new ItemAssignedButton(new ItemBuilder(HeadUtils
                .getItemHead("http://textures.minecraft.net/texture/3ed1aba73f639f4bc42bd48196c715197be2712c3b962c97ebf9e9ed8efa025"))
                .setDisplayName("&cBack").setGlowing(true).build(false),new Button(31, guiClickEvent -> openPage(backPage),
                false)));

    }

    @Override
    public void onOpen(Player player) {

    }

    @Override
    public void onClose(Player player) {

    }
}
