package com.SMPCore.listeners;

import com.SMPCore.mobs.MobType;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

public class MobListener implements Listener {


    @EventHandler (ignoreCancelled = true,priority = EventPriority.MONITOR)
    public void onMobSpawn(EntitySpawnEvent entitySpawnEvent) {

        if (entitySpawnEvent.getEntity() instanceof LivingEntity livingEntity) {


        }


    }


    @EventHandler (priority = EventPriority.MONITOR,ignoreCancelled = true)
    public void onDamage(EntityDamageEvent entityDamageEvent) {

        if (entityDamageEvent.getEntity() instanceof LivingEntity livingEntity) {

        }


    }

}
