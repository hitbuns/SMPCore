package com.SMPCore;

import com.SMPCore.Utilities.*;
import com.SMPCore.Waypoints.WaypointListener;
import com.SMPCore.commands.CmdClaim;
import com.SMPCore.commands.CmdSkills;
import com.SMPCore.commands.CmdWarps;
import com.SMPCore.configs.CraftExpConfig;
import com.SMPCore.listeners.EventListener;
import com.SMPCore.listeners.MobListener;
import com.SMPCore.mining.DurabilityListener;
import com.SMPCore.mobs.MobTicker;
import com.SMPCore.skills.PlayerDataHandler;
import com.SMPCore.skills.SkillListener;
import com.earth2me.essentials.Essentials;
import com.github.fierioziy.particlenativeapi.api.ParticleNativeAPI;
import com.github.fierioziy.particlenativeapi.api.particle.type.ParticleType;
import com.github.fierioziy.particlenativeapi.core.ParticleNativeCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Main extends JavaPlugin {
    ParticleNativeAPI particleNativeAPI;
    Map<String,ParticleHolder> particleTypes;
    public Essentials essentials;

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

        Bukkit.getWorlds().forEach(world -> world.getLivingEntities().stream().filter(Entity::isCustomNameVisible)
                .forEach(Entity::remove));

        Instance = this;

        new QuartzArmorHandler(this);

        PlayerDataHandler.init(this);

        CraftExpConfig.init(this);

        essentials = (Essentials) getServer().getPluginManager().getPlugin("Essentials");
        essentials.getCommand("warps").setExecutor(null);

        particleNativeAPI = ParticleNativeCore.loadAPI(this);

        particleTypes = Arrays.stream(particleNativeAPI.LIST_1_13.getClass().getFields()).sorted((o1, o2) ->
                String.CASE_INSENSITIVE_ORDER.compare(o1.getName(),o2.getName())).map(field -> {
            try {
                return new ParticleHolder((ParticleType) field.get(particleNativeAPI.LIST_1_13),field.getName());
            } catch (Exception e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toMap(ParticleHolder::getId, particleHolder -> particleHolder));


        new TempEntityDataHandler(this);

        new WorldGuardAPI();


        FlagRegistryConfig.register(this);


        new MobTicker();

        registerCommands();
        registerListeners();


    }

    @Override
    public void onDisable() {

        if (ExecutorLimitTask.scheduledExecutorService != null) ExecutorLimitTask
                .scheduledExecutorService.shutdownNow();

    }

    void registerCommands() {
        getCommand("protectarea").setExecutor(new CmdClaim());
        getCommand("skills").setExecutor(new CmdSkills());
        getCommand("warps").setExecutor(new CmdWarps());
    }

    void registerListeners() {
        new WaypointListener(this);

        getServer().getPluginManager().registerEvents(new EventListener(),this);
        getServer().getPluginManager().registerEvents(new MobListener(),this);

        new DurabilityListener(this);
        getServer().getPluginManager().registerEvents(new SkillListener(),this);

    }

}