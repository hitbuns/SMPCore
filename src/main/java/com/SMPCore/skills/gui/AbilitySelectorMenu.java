package com.SMPCore.skills.gui;

import com.MenuAPI.GUISystem.AbstractModifiableListMenu;
import com.MenuAPI.GUISystem.Button;
import com.MenuAPI.GUISystem.GUIClickRunnable;
import com.MenuAPI.GUISystem.iPage;
import com.MenuAPI.Utilities.DecorationUtils;
import com.MenuAPI.Utilities.DescriptionBuilder;
import com.MenuAPI.Utilities.ItemBuilder;
import com.MenuAPI.Utilities.impl.HeadUtils;
import com.MenuAPI.Utils;
import com.SMPCore.skills.AbilityMessageConfig;
import com.SMPCore.skills.AbilitySkillPerk;
import com.SMPCore.skills.PlayerDataHandler;
import com.SMPCore.skills.impl.AbilityIntentionType;
import com.SMPCore.skills.impl.NonCombatStatType;
import com.SMPCore.skills.impl.iPerkContainer;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.mongodb.client.model.Updates;
import de.tr7zw.nbtapi.NBTItem;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class AbilitySelectorMenu extends AbstractModifiableListMenu<AbilitySkillPerk> {

    final iPage backPage;
    static final Map<AbilityIntentionType,AbilitySkillPerk[]> abilities = new HashMap<>();
    final AbilityIntentionType abilityIntentionType;
    AbilitySkillPerk[] abilitySkillPerks;

    public AbilitySelectorMenu(Player player,AbilityIntentionType abilityIntentionType, iPage backPage) {
        super(player, "Ability Selector - ", backPage, 5, AbilitySkillPerk.class);
        this.backPage = backPage;

       abilitySkillPerks = abilities.get(this.abilityIntentionType = abilityIntentionType);
       if (abilitySkillPerks == null) {

           List<AbilitySkillPerk> abilitySkillPerkList = new ArrayList<>();
           for (PlayerDataHandler.ExpId expId : abilityIntentionType.expIds) {
               if (expId instanceof iPerkContainer perkContainer) abilitySkillPerkList.addAll(perkContainer.getPerks().values().stream()
                       .map(skillPerk -> {

                           try {
                               return (AbilitySkillPerk) skillPerk;
                           } catch (Exception e) {
                               return null;
                           }


                       }).toList());
           }

           abilities.put(abilityIntentionType, abilitySkillPerks = abilitySkillPerkList.toArray(AbilitySkillPerk[]::new));

       }

       init(30,32,31,10,11,12,13,14,15,16,19,20,21,22,23,24,25);

       registerPriority(new Button(31,guiClickEvent -> closePage(backPage),
               false));

    }

    @Override
    public ItemStack a(int i) {

        if (i > getIndexes().length) return getDefaultFillItem().clone();

        AbilitySkillPerk abilitySkillPerk = getIndexes()[i-1];

        if (!abilitySkillPerk.playerPredicate.test(getPlayer())) {

            return new ItemBuilder(HeadUtils.getItemHead("http://textures.minecraft.net/texture/66963f79e5f01536d04be18c99330cdacdee91b3232baebe58baca05f4640d1a"))
                    .setDisplayName("&e"+abilitySkillPerk.getDisplayName()).setLore(DescriptionBuilder.init().addLore("",abilitySkillPerk
                            .playerPredicate.message(getPlayer())).build()).build(false);

        }

        String v = abilitySkillPerk.getClass().getSimpleName();
        Document document = PlayerDataHandler.getPlayerData(getPlayer());
        boolean a = v.equalsIgnoreCase(document.getString("ability_"+abilityIntentionType
                .name()+"_PRIMARY")), b = v.equalsIgnoreCase(document
                .getString("ability_"+abilityIntentionType
                        .name()+"_SECONDARY"));

        ItemBuilder itemBuilder = new ItemBuilder(HeadUtils.getItemHead("http://textures.minecraft.net/texture/66963f79e5f01536d04be18c99330cdacdee91b3232baebe58baca05f4640d1a"))
                .setDisplayName("&e"+abilitySkillPerk.getDisplayName()).setLore(DescriptionBuilder.init()
                        .addLore(AbilityMessageConfig.Instance.getMessage(abilitySkillPerk).toArray(String[]::new))
                        .addLore("","&7Left-Click to select this as the primary ability",
                                "&7Right-Click to select this as the secondary ability").build()
                ).setGlowing(a || b);

        if (a) itemBuilder.addLore("&a&m    &a PRIMARY SELECTED &a&m    ");
        if (b) itemBuilder.addLore("&a&m    &a SECONDARY SELECTED &a&m    ");

        ItemStack itemStack1 = itemBuilder.build(false);

        NBTItem nbtItem = new NBTItem(itemStack1);
        nbtItem.setInteger("clickValue",i-1);

        return nbtItem.getItem();
    }

    @Override
    public AbilitySkillPerk[] getIndexes() {
        return abilitySkillPerks;
    }

    ItemStack itemStack = new ItemBuilder(HeadUtils.getItemHead("http://textures.minecraft.net/texture/4c3bdff88b94bef239b90e184f33f95733659ef137f3a55eb9b7df167679336c"))
            .setDisplayName("&cNo more abilities...").build(false);

    @Override
    public ItemStack getDefaultFillItem() {
        return itemStack;
    }

    @Override
    public GUIClickRunnable onGUIClick() {
        return guiClickEvent -> {

            ItemStack itemStack1 = guiClickEvent.getCurrentItem();
            ClickType clickType = guiClickEvent.getClickType();
            if (Utils.isNullorAir(itemStack1)) return;

            NBTItem nbtItem = new NBTItem(itemStack1);
            if (nbtItem.hasKey("clickValue")) {

                AbilitySkillPerk abilitySkillPerk = abilitySkillPerks[nbtItem.getInteger("clickValue")];
                switch (clickType) {
                    case LEFT -> PlayerDataHandler.update(getPlayer(), Updates.setOnInsert("ability_"+abilityIntentionType
                            .name()+"_PRIMARY",abilitySkillPerk.getClass().getSimpleName()));
                    case RIGHT -> PlayerDataHandler.update(getPlayer(), Updates.setOnInsert("ability_"+abilityIntentionType
                            .name()+"_SECONDARY",abilitySkillPerk.getClass().getSimpleName()));
                }

                update();

            }

        };
    }

    @Override
    public int getGUIId() {
        return 302;
    }

    @Override
    public void setupInventory() {

        DecorationUtils.border(getInventory(), Material.BLACK_STAINED_GLASS_PANE);
        DecorationUtils.fillItem(getInventory(),Material.GRAY_STAINED_GLASS_PANE);

        update();

    }

    @Override
    public void update() {
        super.update();

        updateInfoButton();
    }


    void updateInfoButton() {
        Document document = PlayerDataHandler.getPlayerData(getPlayer());
        String primary = document.getString("ability_"+abilityIntentionType
                .name()+"_PRIMARY"),secondary = document.getString("ability_"+abilityIntentionType
                .name()+"_SECONDARY");
        getInventory().setItem(4,new ItemBuilder(HeadUtils
                .getItemHead("http://textures.minecraft.net/texture/a8d5cb12219a3f5e9bb68c8914c443c2de160eff00cf3e730fbaccd8db6918fe")).setDisplayName("&8&l&m   &8[&aSelection Info&8]&8&l&m   ")
                .setLore(DescriptionBuilder.init()
                        .addLore("&ePRIMARY &b[Q]&e: "+(primary != null ? "&a"+primary : "&e<NOT SET>"),
                                "&eSECONDARY &b[Shift + Q]&e: "+(secondary != null ? "&a"+secondary : "&e<NOT SET>"))
                        .build()).setGlowing(true).build(false));
    }

    @Override
    public void onOpen(Player player) {

    }

    @Override
    public void onClose(Player player) {

    }
}
