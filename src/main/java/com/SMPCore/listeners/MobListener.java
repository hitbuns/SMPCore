package com.SMPCore.listeners;

import com.MenuAPI.Utils;
import com.SMPCore.Events.TickedSMPEvent;
import com.SMPCore.Main;
import com.SMPCore.Utilities.MobUtils;
import com.SMPCore.Utilities.TempEntityDataHandler;
import com.SMPCore.mobs.MobModifierType;
import com.SMPCore.mobs.MobType;
import com.SMPCore.mobs.MobTypeContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

import java.util.concurrent.TimeUnit;

public class MobListener implements Listener {

    @EventHandler (priority = EventPriority.HIGHEST,ignoreCancelled = true)
    public void onLaunchProjectile(EntityDamageByEntityEvent entityDamageByEntityEvent) {

        if (entityDamageByEntityEvent.getDamager() instanceof Projectile projectile && projectile.hasMetadata("damage")) {
            entityDamageByEntityEvent.setDamage(projectile.getMetadata("damage").get(0).asDouble());
        }

    }

    @EventHandler (priority = EventPriority.MONITOR,ignoreCancelled = true)
    public void onShootArrow(EntityShootBowEvent entityExplodeEvent) {

        LivingEntity livingEntity = entityExplodeEvent.getEntity();



            MobType mobType = MobType.getMobType(livingEntity);
            MobModifierType mobModifierType = MobType.getMobTypeModifier(livingEntity);

            if (mobType != null) {

                mobType.eventHook.onEvent(entityExplodeEvent,
                        livingEntity);

            }

            if (mobModifierType != null) mobModifierType.eventHook.onEvent(entityExplodeEvent,livingEntity);


    }

    @EventHandler (priority = EventPriority.MONITOR,ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent entityExplodeEvent) {

        if (entityExplodeEvent.getEntity() instanceof LivingEntity livingEntity) {


            MobType mobType = MobType.getMobType(livingEntity);
            MobModifierType mobModifierType = MobType.getMobTypeModifier(livingEntity);

            if (mobType != null) {

                mobType.eventHook.onEvent(entityExplodeEvent,
                        livingEntity);

            }

            if (mobModifierType != null) mobModifierType.eventHook.onEvent(entityExplodeEvent,livingEntity);


        }

    }


    @EventHandler (ignoreCancelled = true,priority = EventPriority.MONITOR)
    public void onMobSpawn(EntitySpawnEvent entitySpawnEvent) {

        if (entitySpawnEvent.getEntity() instanceof LivingEntity livingEntity) {

            MobTypeContainer mobTypeContainer = MobType.get(livingEntity.getType());
            if (mobTypeContainer != null) {

                TempEntityDataHandler.getorAdd(livingEntity).playerCooldownHandler.setOnCoolDown("vanilla_spawn");

                Location location = livingEntity.getLocation();

                Bukkit.getScheduler().runTaskLater(Main.Instance,()-> {

                    if (livingEntity.isDead() || livingEntity.getHealth() <= 0 || MobType.getMobType(livingEntity) != null) {
                        return;
                    }

                    MobType mobType = mobTypeContainer.random(location);

                    if (mobType == null) return;

                    mobType.updateEntity(entitySpawnEvent,livingEntity, Utils.RNG_INT(0,100) >= 90 ?
                            MobModifierType.values()[Utils.RNG_INT(0,MobModifierType.values()
                                    .length-1)] : MobModifierType.NORMAL,mobType
                            .yGrader.getGrade(location));

                    TempEntityDataHandler
                            .getorAdd(livingEntity).playerCooldownHandler.setOnCoolDown("active_spawn");
                },5L);


            }

        }


    }


    @EventHandler (priority = EventPriority.MONITOR,ignoreCancelled = true)
    public void onDamage(EntityDamageEvent entityDamageEvent) {

        if (entityDamageEvent.getEntity() instanceof LivingEntity livingEntity) {

            if (TempEntityDataHandler.getorAdd(livingEntity).playerCooldownHandler.isOnCoolDown("vanilla_spawn",
                    TimeUnit.MILLISECONDS,5*50L)) {
                entityDamageEvent.setCancelled(true);
                return;
            }

            MobType mobType = MobType.getMobType(livingEntity);
            MobModifierType mobModifierType = MobType.getMobTypeModifier(livingEntity);

            if (mobType != null) {

                mobType.eventHook.onEvent(entityDamageEvent,
                        livingEntity);

            }

            if (mobModifierType != null) mobModifierType.eventHook.onEvent(entityDamageEvent,livingEntity);


        }

        if (entityDamageEvent instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {


            LivingEntity livingEntity = Utils.getAttacker(entityDamageByEntityEvent
                    .getDamager());

            if (livingEntity == null) return;

            MobType mobType = MobType.getMobType(livingEntity);
            MobModifierType mobModifierType = MobType.getMobTypeModifier(livingEntity);

            if (mobType != null) {

                mobType.eventHook.onEvent(entityDamageByEntityEvent,
                        livingEntity);

            }

            if (mobModifierType != null) mobModifierType.eventHook.onEvent(entityDamageByEntityEvent,livingEntity);

        }


    }


    @EventHandler
    public void onTickedSMPEvent(TickedSMPEvent tickedSMPEvent) {


            Bukkit.getWorlds().forEach(world -> world.getLivingEntities()
                    .stream().filter(livingEntity -> livingEntity
                            .hasMetadata("mobType"))
                    .forEach(livingEntity -> {

                        MobType mobType = MobType.getMobType(livingEntity);
                        MobModifierType mobModifierType = MobType.getMobTypeModifier(livingEntity);

                        if (mobType != null) {

                                mobType.eventHook.onEvent(tickedSMPEvent,
                                        livingEntity);

                        }

                        if (mobModifierType != null) mobModifierType.eventHook.onEvent(tickedSMPEvent,livingEntity);


                    }));
    }

}
