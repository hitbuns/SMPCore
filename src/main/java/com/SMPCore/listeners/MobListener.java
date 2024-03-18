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
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

public class MobListener implements Listener {


    @EventHandler (ignoreCancelled = true,priority = EventPriority.MONITOR)
    public void onMobSpawn(EntitySpawnEvent entitySpawnEvent) {

        if (entitySpawnEvent.getEntity() instanceof LivingEntity livingEntity) {

            MobTypeContainer mobTypeContainer = MobType.get(livingEntity.getType());
            if (mobTypeContainer != null ) {
                Location location = livingEntity.getLocation();
                MobType mobType = mobTypeContainer.random(location);

                if (mobType == null) return;

                mobType.updateEntity(entitySpawnEvent,livingEntity, Utils.RNG_INT(0,100) >= 90 ?
                        MobModifierType.values()[Utils.RNG_INT(0,MobModifierType.values()
                                .length-1)] : MobModifierType.NORMAL,mobType
                        .yGrader.getGrade(location));

                TempEntityDataHandler
                        .getorAdd(livingEntity).playerCooldownHandler.setOnCoolDown("active_spawn");


            }

        }


    }


    @EventHandler (priority = EventPriority.MONITOR,ignoreCancelled = true)
    public void onDamage(EntityDamageEvent entityDamageEvent) {

        if (entityDamageEvent.getEntity() instanceof LivingEntity livingEntity) {


            MobType mobType = MobType.getMobType(livingEntity);

            if (mobType != null) {

                mobType.eventHook.onEvent(entityDamageEvent,livingEntity);

                MobUtils.updateEntity(livingEntity,livingEntity
                        .getHealth()-entityDamageEvent.getFinalDamage());
            }


        }

        if (entityDamageEvent instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {


            LivingEntity livingEntity = Utils.getAttacker(entityDamageByEntityEvent
                    .getDamager());

            if (livingEntity == null) return;

            MobType mobType = MobType.getMobType(livingEntity);

            if (mobType != null) {

                mobType.eventHook.onEvent(entityDamageEvent,livingEntity);
            }

        }


    }


    @EventHandler
    public void onTickedSMPEvent(TickedSMPEvent tickedSMPEvent) {


            Bukkit.getWorlds().forEach(world -> world.getLivingEntities()
                    .stream().filter(livingEntity -> livingEntity
                            .hasMetadata("mobType"))
                    .forEach(livingEntity -> {

                        MobType mobType = MobType.getMobType(livingEntity);

                        if (mobType != null) {

                                mobType.eventHook.onEvent(tickedSMPEvent,
                                        livingEntity);
                        }


                    }));
    }

}
