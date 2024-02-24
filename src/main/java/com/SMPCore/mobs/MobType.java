package com.SMPCore.mobs;

import com.MenuAPI.Utils;
import com.SMPCore.Main;
import com.SMPCore.Utilities.MobUtils;
import com.mongodb.lang.Nullable;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public enum MobType {

    //ZOMBIE
    WALKER(EntityType.ZOMBIE,location -> 100,(event,livingEntity,grade)-> {
        livingEntity.addPotionEffect(PotionEffectType.SLOW.createEffect(500,2));
        livingEntity.addPotionEffect(PotionEffectType.REGENERATION.createEffect(100,1));
    },"&eWalker",10,2,0,2.5,0.25,0.02)


    ;

    private static final Map<EntityType,MobTypeContainer> map  = Arrays.stream(EntityType.values())
            .map(entityType1 -> {

                try {
                    return new MobTypeContainer(entityType1,Arrays.stream(MobType.values()).filter(mobType -> mobType.entityType == entityType1)
                            .collect(Collectors.toMap(Enum::name, mobType -> mobType)));
                } catch (Exception exception) {
                    return null;
                }

            }).filter(Objects::nonNull).collect(Collectors.toMap(mobTypeContainer -> mobTypeContainer.entityType,
                    mobTypeContainer -> mobTypeContainer));


    MobType(EntityType entityType,SpawnCondition spawnCondition,EventListener<EntitySpawnEvent> spawnListener,String displayName,double health,double damage,double moveSpd,
            double healthIncrement,double damageIncrement,double moveSpdIncrement) {
        this.entityType = entityType;
        this.spawnCondition = spawnCondition;
        this.spawnListener = spawnListener;
        this.health = Math.max(1,health);
        this.damage = Math.max(0,damage);
        this.moveSpd = moveSpd;
        this.healthIncrement = healthIncrement;
        this.damageIncrement = damageIncrement;
        this.moveSpdIncrement = moveSpdIncrement;
        this.displayName = Utils.color(displayName);
    }

    public static LivingEntity updateEntity(@Nullable EntitySpawnEvent entitySpawnEvent, LivingEntity livingEntity, MobType mobType, MobModifierType mobModifierType, int grade) {
        return mobType.updateEntity(entitySpawnEvent,livingEntity,mobModifierType,grade);
    }

    public LivingEntity updateEntity(@Nullable EntitySpawnEvent entitySpawnEvent,LivingEntity livingEntity,MobModifierType mobModifierType,int grade) {

        if (livingEntity == null) return null;

        grade = Math.min(6,Math.max(1,grade));

        AttributeInstance maxHealth = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        maxHealth.setBaseValue(health);
        maxHealth.addModifier(new AttributeModifier("grade_maxhealth_"+livingEntity.getEntityId()+"_"+grade,healthIncrement*(grade-1), AttributeModifier.Operation.ADD_NUMBER));

        livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).addModifier(new AttributeModifier("grade_attackdamage_"+livingEntity.getEntityId()+"_"+grade,
                Math.max(damage+damageIncrement*(grade-1),1), AttributeModifier.Operation.ADD_NUMBER));
        livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(new AttributeModifier("grade_movespd_"+livingEntity.getEntityId()+"_"+grade,
                moveSpd+moveSpdIncrement*(grade-1), AttributeModifier.Operation.MULTIPLY_SCALAR_1));

        int finalGrade = grade;
        livingEntity.setMetadata("grade",new LazyMetadataValue(Main.Instance, LazyMetadataValue.CacheStrategy.NEVER_CACHE,() -> finalGrade));
        livingEntity.setMetadata("mobType",new LazyMetadataValue(Main.Instance, LazyMetadataValue.CacheStrategy.NEVER_CACHE, this::name));
        livingEntity.setMetadata("mobModifierType",new LazyMetadataValue(Main.Instance, LazyMetadataValue.CacheStrategy.NEVER_CACHE, mobModifierType::name));

        if (mobModifierType.listener != null) mobModifierType.listener.onSpawn(entitySpawnEvent,livingEntity,grade);

        if (spawnListener != null) spawnListener.onSpawn(entitySpawnEvent,livingEntity,grade);

        MobUtils.updateEntity(livingEntity);

        return livingEntity;

    }

    public final EntityType entityType;
    public final SpawnCondition spawnCondition;
    public final EventListener<EntitySpawnEvent> spawnListener;
    public final double health,damage,moveSpd,
    healthIncrement,damageIncrement,moveSpdIncrement;
    public final String displayName;


    public interface SpawnCondition {

        double canSpawn(Location location);


    }

    public interface EventListener<T extends Event> {

        void onSpawn(T event, LivingEntity livingEntity, int grade);

    }



}
