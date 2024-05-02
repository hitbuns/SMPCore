package com.SMPCore.skills;

import com.MenuAPI.Config;
import com.MenuAPI.Utils;
import com.SMPCore.skills.impl.CombatStatType;
import com.SMPCore.skills.impl.NonCombatStatType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AbilityMessageConfig extends Config {

    public static AbilityMessageConfig Instance;

    AbilityMessageConfig(JavaPlugin javaPlugin) {
        super(javaPlugin, javaPlugin.getDataFolder(), "ability-message.yml");

        ConfigurationSection configurationSection = getorAddConfigurationSection("descriptions");
        //System.out.println("FULL_DEBUG_1");
        for (CombatStatType value : CombatStatType.values()) {
            //System.out.println("FULL_DEBUG_2_"+value.name());
            try {
                //System.out.println("LIST_SIZE>>"+value.allPerks.size());
                value.allPerks.forEach((s, skillPerk) -> {
                    //System.out.println("CHECK SKILL DESCRIPTION>> " + s);
                    if (!configurationSection.contains(s)) {
                        //System.out.println("GENERATE SKILL DESCRIPTION>> " + s);
                        configurationSection.set(s, Arrays.asList("&eThis is an ability!"));
                    }
                });
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        //System.out.println("FULL_DEBUG_3");
        for (NonCombatStatType value : NonCombatStatType.values()) {
            //System.out.println("FULL_DEBUG_4_"+value.name());
            try {
                //System.out.println("LIST_SIZE>>"+value.allPerks.size());
                value.allPerks.forEach((s, skillPerk) -> {
                    //System.out.println("CHECK SKILL DESCRIPTION>> "+s);
                    if (!configurationSection.contains(s)) {
                        //System.out.println("GENERATE SKILL DESCRIPTION>> "+s);
                        configurationSection.set(s, Arrays.asList("&eThis is an ability!"));
                    }
                });
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        //System.out.println("FULL_DEBUG_5");

        save();

        //System.out.println("FULL_DEBUG_6");


    }

    public static void init(JavaPlugin javaPlugin) {

        if (Instance != null) return;

        Instance = new AbilityMessageConfig(javaPlugin);

    }

    public List<String> getMessage(AbilitySkillPerk abilitySkillPerk) {
        return getMessage(abilitySkillPerk.getClass().getSimpleName());
    }

    public List<String> getMessage(String key) {
        return Utils.color(new ArrayList<>(getorAddConfigurationSection("descriptions").getStringList(key)));
    }


}
