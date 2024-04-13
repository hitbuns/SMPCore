package com.SMPCore.skills.impl;

import com.SMPCore.skills.AbilitySkillPerk;
import com.SMPCore.skills.PlayerDataHandler;
import com.SMPCore.skills.SkillPerk;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum CombatStatType implements PlayerDataHandler.ExpId,iPerkContainer {

    STRENGTH("&cStrength"),
    DEXTERITY("&aDexterity"),
    CONSTITUTION("&dConstitution"),
    AGILITY("&bAgility"),
    DEFENSE("&eDefense")
    ;

    CombatStatType(String displayKey) {
        this.key = "combat_statType_"+this.name();
        this.displayKey = displayKey;
    }

    public static void init() {
        System.out.println("DEBUG_12_A");
        Arrays.stream(CombatStatType.values()).forEach(combatStatType -> {
            System.out.println("DEBUG_12_1");
            try {

                System.out.println("DEBUG_12_2");
                Reflections reflections= new Reflections("com.SMPCore.skills.perks."+combatStatType.name());
                reflections.getSubTypesOf(SkillPerk.class).forEach(aClass -> {

                    try {

                        System.out.println("DEBUG_12_3_"+aClass.getSimpleName());

                        if (!aClass.equals(AbilitySkillPerk.class)) {

                            SkillPerk skillPerk = aClass
                                    .getConstructor().newInstance();
                            combatStatType.allPerks.put(aClass.getSimpleName(), skillPerk);

//                        if (AbilityIntentionType.allPerks == null) AbilityIntentionType.allPerks = new HashMap<>();
                            AbilityIntentionType.allPerks.put(aClass.getSimpleName(), skillPerk);

                        }

                        System.out.println("DEBUG_12_4");
                    } catch (Exception exception) {
                        System.out.println("["+combatStatType.name()+"]StatType Skill Perk must have a no-args constructor!");
                        throw new RuntimeException(exception);
                    }


                });

            } catch (Exception exception) {
                exception.printStackTrace();
                System.out.println(combatStatType.name()+" Combat Stat Type did not have any perks to register!");
            }

        });
        System.out.println("DEBUG_12_B");
    }


    final String key,displayKey;
    public final Map<String, SkillPerk> allPerks = new HashMap<>();

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
