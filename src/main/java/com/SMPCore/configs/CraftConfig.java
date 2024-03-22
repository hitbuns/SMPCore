package com.SMPCore.configs;

import com.MenuAPI.Config;
import com.SMPCore.gui.CraftAdminListManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CraftConfig extends Config {


    public static CraftConfig Instance;

    /*

    crafts:
        %crafting-method-enum%:
            %key%:
                recipeResult: %itemStack%

     */

    public static void init(JavaPlugin javaPlugin) {
        new CraftConfig(javaPlugin);
    }

    public CraftConfig(JavaPlugin javaPlugin) {
        super(javaPlugin, javaPlugin.getDataFolder(), "crafts-(DO-NOT-EDIT).yml");

        Instance = this;
    }

    public String[] getKeys(CraftAdminListManager.CraftType craftType) {
        return getorAddConfigurationSection(getorAddConfigurationSection("crafts"),craftType
                .name()).getKeys(false).toArray(String[]::new);
    }

}
