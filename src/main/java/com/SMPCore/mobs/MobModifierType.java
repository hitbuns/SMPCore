package com.SMPCore.mobs;

import com.MenuAPI.Utils;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.UUID;

public enum MobModifierType {


    TOXIC((event, livingEntity, grade) -> {

    },"&2&l☠"),
    SWIFT((event, livingEntity, grade) -> {

    },"&b&l\uD83E\uDD7E"),
    REGEN((event, livingEntity, grade) -> {

    },"&d&l✙"),
    REINFORCED((event, livingEntity, grade) -> {
        livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).addModifier(new AttributeModifier("reinforced_max_"+UUID.randomUUID()+"_"
                +Utils.RNG_INT(0,1000),1.5, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
    },"&9&l\uD83D\uDEE1"),
    NORMAL((event, livingEntity, grade) -> {},"")
    ;

    MobModifierType(MobType.EventListener<EntitySpawnEvent> listener,String displayName) {
        this.listener = listener;
        this.displayName = Utils.color(displayName);
    }

    public final MobType.EventListener<EntitySpawnEvent> listener;
    public final String displayName;


}
