package com.SMPCore;

import com.SMPCore.Utilities.FlagRegistryConfig;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {


    public static Main Instance;

    @Override
    public void onEnable() {

        Instance = this;


        FlagRegistryConfig.register(this);

        registerCommands();
        registerListeners();

    }

    void registerCommands() {

    }

    void registerListeners() {

    }

}