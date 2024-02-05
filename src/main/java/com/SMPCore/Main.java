package com.SMPCore;

import com.SMPCore.Utilities.FlagRegistryConfig;
import com.SMPCore.Utilities.WorldGuardAPI;
import com.SMPCore.commands.CmdClaim;
import com.SMPCore.listeners.EventListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {


    public static Main Instance;

    @Override
    public void onEnable() {

        Instance = this;

        new WorldGuardAPI();


        FlagRegistryConfig.register(this);

        registerCommands();
        registerListeners();

    }

    void registerCommands() {
        getCommand("protectarea").setExecutor(new CmdClaim());
    }

    void registerListeners() {
        getServer().getPluginManager().registerEvents(new EventListener(),this);
    }

}