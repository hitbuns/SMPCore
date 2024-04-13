package com.SMPCore.skills;

import com.MenuAPI.Config;
import com.MenuAPI.Utils;
import com.SMPCore.skills.impl.CombatStatType;
import com.SMPCore.skills.impl.NonCombatStatType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AbilityMessageConfig extends Config {

    public static AbilityMessageConfig Instance;

    AbilityMessageConfig(JavaPlugin javaPlugin) {
        super(javaPlugin, javaPlugin.getDataFolder(), "ability-message.yml");

        ConfigurationSection configurationSection = getorAddConfigurationSection("descriptions");

        for (CombatStatType value : CombatStatType.values()) {
            value.allPerks.forEach((s, skillPerk) -> {
                if (!configurationSection.contains(s)) {
                    configurationSection.set(s, List.of("&eThis is an ability!"));
                }
            });
        }

        for (NonCombatStatType value : NonCombatStatType.values()) {
            value.allPerks.forEach((s, skillPerk) -> {
                if (!configurationSection.contains(s)) {
                    configurationSection.set(s, List.of("&eThis is an ability!"));
                }
            });
        }

        save();


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
