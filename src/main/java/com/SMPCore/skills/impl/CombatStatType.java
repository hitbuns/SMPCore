package com.SMPCore.skills.impl;

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

    static {
        Arrays.stream(CombatStatType.values()).forEach(combatStatType -> {

            try {

                Reflections reflections= new Reflections("com.SMPCore.perks."+combatStatType.name());
                reflections.getSubTypesOf(SkillPerk.class).forEach(aClass -> {

                    try {

                        combatStatType.allPerks.put(aClass.getSimpleName(),aClass
                                .getDeclaredConstructor().newInstance());

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
