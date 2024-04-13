package com.SMPCore.skills.impl;

import com.SMPCore.skills.PlayerDataHandler;
import com.SMPCore.skills.SkillPerk;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum NonCombatStatType implements PlayerDataHandler.ExpId,iPerkContainer {

    FARMING("&bFarming"),
    COOKING("&aCooking"),
    FISHING("&2Fishing"),
    WOODCUTTING("&bWoodcutting"),
    MINING("&cMining"),
    CRAFTING("&3Crafting"),
    ENCHANTING("&4Enchanting"),
    SMITHING("&dSmithing"),
    BARTER("&7Bartering")
    ;

    NonCombatStatType(String displayKey) {
        this.key = "noncombat_statType_"+this.name();
        this.displayKey = displayKey;
    }

    static {
        Arrays.stream(NonCombatStatType.values()).forEach(combatStatType -> {

            try {

                Reflections reflections= new Reflections("com.SMPCore.perks."+combatStatType.name());
                reflections.getSubTypesOf(SkillPerk.class).forEach(aClass -> {

                    try {

                        SkillPerk skillPerk = aClass
                                .getDeclaredConstructor().newInstance();
                        combatStatType.allPerks.put(aClass.getSimpleName(),skillPerk);
                        if (AbilityIntentionType.allPerks == null) AbilityIntentionType.allPerks = new HashMap<>();
                        AbilityIntentionType.allPerks.put(aClass.getSimpleName(),skillPerk);

                    } catch (Exception exception) {
                        System.out.println("["+combatStatType.name()+"]StatType Skill Perk must have a no-args constructor!");
                        throw new RuntimeException(exception);
                    }


                });

            } catch (Exception exception) {
                System.out.println(combatStatType.name()+" Combat Stat Type did not have any perks to register!");
            }

        });
    }


    public final Map<String, SkillPerk> allPerks = new HashMap<>();
    final String key,displayKey;

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getDisplay() {
        return displayKey;
    }

    @Override
    public Map<String, SkillPerk> getPerks() {
        return allPerks;
    }
}
