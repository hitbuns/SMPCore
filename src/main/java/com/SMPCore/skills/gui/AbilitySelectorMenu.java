package com.SMPCore.skills.gui;

import com.MenuAPI.GUISystem.AbstractModifiableListMenu;
import com.MenuAPI.GUISystem.GUIClickRunnable;
import com.MenuAPI.GUISystem.iPage;
import com.MenuAPI.Utilities.DescriptionBuilder;
import com.MenuAPI.Utilities.ItemBuilder;
import com.MenuAPI.Utilities.impl.HeadUtils;
import com.SMPCore.skills.AbilitySkillPerk;
import com.SMPCore.skills.PlayerDataHandler;
import com.SMPCore.skills.impl.AbilityIntentionType;
import com.SMPCore.skills.impl.NonCombatStatType;
import com.SMPCore.skills.impl.iPerkContainer;
import org.bukkit.entity.Player;
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

        //IN PROGRESS

        return null;
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

        };
    }

    @Override
    public int getGUIId() {
        return 302;
    }

    @Override
    public void setupInventory() {

    }

    @Override
    public void onOpen(Player player) {

    }

    @Override
    public void onClose(Player player) {

    }
}
