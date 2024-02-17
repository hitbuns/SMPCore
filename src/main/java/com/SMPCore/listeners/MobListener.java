package com.SMPCore.listeners;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class MobListener implements Listener {


    @EventHandler
    public void onMobSpawn(EntitySpawnEvent entitySpawnEvent) {

        if (entitySpawnEvent.getEntity() instanceof LivingEntity livingEntity) {



        }

    }

}
