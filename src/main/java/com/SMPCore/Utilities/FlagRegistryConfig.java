package com.SMPCore.Utilities;

import com.MenuAPI.Config;
import com.SMPCore.gui.RegionFlagEditor;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class FlagRegistryConfig extends Config {

    public static FlagRegistryConfig Instance;


    public static void register(JavaPlugin javaPlugin) {

        new FlagRegistryConfig(javaPlugin);

        RegionFlagEditor.updateFlagsRegistry();

    }

    public FlagRegistryConfig(JavaPlugin javaPlugin) {
        super(javaPlugin, javaPlugin.getDataFolder(),"flag-registry.yml", "flag-registry-default.yml");

        Instance = this;

        if (!getBoolean("updated",false)) {


            WorldGuard.getInstance().getFlagRegistry().getAll().forEach(flag ->
                    set(flag.getName(),false));

            set("updated",true);

            save();

        }

    }


    public boolean isFlagEnabled(Flag<?> flag) {
        return flag != null && isFlagEnabled(flag.getName());
    }

    public boolean isFlagEnabled(String flagName) {
        return flagName != null && getBoolean(flagName,false);
    }



}
