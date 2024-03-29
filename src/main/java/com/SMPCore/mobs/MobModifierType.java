package com.SMPCore.mobs;

import com.MenuAPI.Utils;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public enum MobModifierType {


    TOXIC((event, livingEntity, grade) -> {

    },"&2&l☠",(event, livingEntity) -> {

        if (event instanceof EntityDamageByEntityEvent entityDamageByEntityEvent && Utils.getAttacker(entityDamageByEntityEvent
                .getDamager()) == livingEntity && entityDamageByEntityEvent
                .getEntity() instanceof LivingEntity livingEntity1) {

            livingEntity1.addPotionEffect(PotionEffectType.POISON.createEffect(70,MobType
                    .getGrade(livingEntity)));
        }

    }),
    SWIFT((event, livingEntity, grade) -> {
        livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
                .addModifier(new AttributeModifier("swift_max_"+UUID.randomUUID()+"_"
                +Utils.RNG_INT(0,1000),1.3+0.05*grade, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
    },"&b&l\uD83E\uDD7E",null),
    REGEN((event, livingEntity, grade) -> {
        MobTicker.Instance.regen.add(livingEntity);
    },"&d&l✙",null),
    REINFORCED((event, livingEntity, grade) -> {
        livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).addModifier(new AttributeModifier("reinforced_max_"+UUID.randomUUID()+"_"
                +Utils.RNG_INT(0,1000),1.5, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
    },"&9&l\uD83D\uDEE1",null),
    NORMAL((event, livingEntity, grade) -> {},"",null)
    ;

    MobModifierType(MobType.EventListener<EntitySpawnEvent> listener, String displayName, MobType.EventHook eventHook) {
        this.listener = listener;
        this.displayName = Utils.color(displayName);
        this.eventHook =eventHook != null ? eventHook : (event, livingEntity) -> {};
    }

    public final MobType.EventListener<EntitySpawnEvent> listener;
    public final String displayName;
    public final MobType.EventHook eventHook;


}
