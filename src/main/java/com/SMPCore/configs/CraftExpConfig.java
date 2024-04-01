package com.SMPCore.configs;

import com.MenuAPI.Config;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.java.JavaPlugin;

public class CraftExpConfig extends Config {

    public static CraftExpConfig Instance;

    /*

    crafts:
        %namespacekey%:
            expGain: 0
            requiredLevelCrafting: 0

     */

    public static void init(JavaPlugin javaPlugin) {

        Instance = new CraftExpConfig(javaPlugin);

    }

    public CraftExpConfig(JavaPlugin javaPlugin) {
        super(javaPlugin, javaPlugin.getDataFolder(),"craft-exp.yml");

        ConfigurationSection configurationSection = getorAddConfigurationSection("crafts");

        javaPlugin.getServer().recipeIterator().forEachRemaining(recipe -> {

            if (recipe instanceof CraftingRecipe craftingRecipe) {

                String key = craftingRecipe.getKey().getKey();

                if (!configurationSection.contains(key)) {

                    ConfigurationSection configurationSection1 = getorAddConfigurationSection(configurationSection,key);
                    configurationSection1.set("expGain",0);
                    configurationSection1.set("requiredLevelCrafting",0);

                }


            }

        });

        save();

    }


    public double expGain(String key) {
        try {
            return key != null ? getConfigurationSection("crafts").getConfigurationSection(key)
                    .getDouble("expGain",0) : 0;
        } catch (Exception exception) {
            return 0;
        }
    }

    public double expGain(NamespacedKey namespacedKey) {
        return namespacedKey != null ? expGain(namespacedKey.getKey()) : 0;
    }

    public double expGain(Recipe recipe) {
        return recipe instanceof CraftingRecipe craftingRecipe ? expGain(craftingRecipe
                .getKey()) : 0;
    }

    public int requiredLevel(String key) {
        try {
            return key != null ? getConfigurationSection("crafts").getConfigurationSection(key)
                    .getInt("requiredLevelCrafting",0) : 0;
        } catch (Exception exception) {
            return 0;
        }
    }

    public int requiredLevel(NamespacedKey namespacedKey) {
        return namespacedKey != null ? requiredLevel(namespacedKey.getKey()) : 0;
    }

    public int requiredLevel(Recipe recipe) {
        return recipe instanceof CraftingRecipe craftingRecipe ? requiredLevel(craftingRecipe
                .getKey()) : 0;
    }

}
