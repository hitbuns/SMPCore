package com.SMPCore;

import com.SMPCore.Utilities.FlagRegistryConfig;
import com.SMPCore.Utilities.TempPlayerDataHandler;
import com.SMPCore.Utilities.WorldGuardAPI;
import com.SMPCore.Waypoints.WaypointListener;
import com.SMPCore.commands.CmdClaim;
import com.SMPCore.listeners.EventListener;
import com.SMPCore.mobs.MobTicker;
import com.github.fierioziy.particlenativeapi.api.ParticleNativeAPI;
import com.github.fierioziy.particlenativeapi.api.particle.type.ParticleType;
import com.github.fierioziy.particlenativeapi.core.ParticleNativeCore;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Main extends JavaPlugin {
    ParticleNativeAPI particleNativeAPI;
    Map<String,ParticleHolder> particleTypes;

    public ParticleNativeAPI getParticleNativeAPI() {
        return particleNativeAPI;
    }

    public Map<String,ParticleHolder> getParticleTypes() {
        return particleTypes;
    }

    public static Main Instance;


    public static class ParticleHolder {

        private final ParticleType particleType;
        private final String id;

        public ParticleHolder(ParticleType particleType, String id) {
            this.particleType = particleType;
            this.id = id;
        }

        public ParticleType getParticleType() {
            return particleType;
        }

        public String getId() {
            return id;
        }
    }

    @Override
    public void onEnable() {

        Instance = this;

        particleNativeAPI = ParticleNativeCore.loadAPI(this);

        particleTypes = Arrays.stream(particleNativeAPI.LIST_1_13.getClass().getFields()).sorted((o1, o2) ->
                String.CASE_INSENSITIVE_ORDER.compare(o1.getName(),o2.getName())).map(field -> {
            try {
                return new ParticleHolder((ParticleType) field.get(particleNativeAPI.LIST_1_13),field.getName());
            } catch (Exception e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toMap(ParticleHolder::getId, particleHolder -> particleHolder));


        new TempPlayerDataHandler(this);

        new WorldGuardAPI();


        FlagRegistryConfig.register(this);


        new MobTicker();

        registerCommands();
        registerListeners();


    }

    @Override
    public void onDisable() {

    }

    void registerCommands() {
        getCommand("protectarea").setExecutor(new CmdClaim());
    }

    void registerListeners() {
        new WaypointListener(this);

        getServer().getPluginManager().registerEvents(new EventListener(),this);
    }

}