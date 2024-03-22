package com.SMPCore.mobs;

import com.MenuAPI.Utilities.ItemBuilder;
import com.MenuAPI.Utils;
import com.SMPCore.Events.TickedSMPEvent;
import com.SMPCore.Main;
import com.SMPCore.Utilities.CooldownHandler;
import com.SMPCore.Utilities.MobUtils;
import com.SMPCore.Utilities.ParticleUtils;
import com.SMPCore.Utilities.TempEntityDataHandler;
import com.SoundAnimation.SoundAPI;
import com.mongodb.lang.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public enum MobType {

    //ZOMBIE
    WALKER(EntityType.ZOMBIE,(event,livingEntity) -> {

        if (event instanceof TickedSMPEvent) {

            TempEntityDataHandler.EntityData entityData = TempEntityDataHandler
                    .getorAdd(livingEntity);

            CooldownHandler<Entity> cooldownHandler = entityData.playerCooldownHandler;

            if  (!(cooldownHandler.isOnCoolDown("active_spawn",TimeUnit.SECONDS,
                            3) && cooldownHandler.isOnCoolDown("fast",
                            TimeUnit.SECONDS,5))) {
                cooldownHandler.setOnCoolDown("fast");

                livingEntity.addPotionEffect(PotionEffectType.SPEED.createEffect(60,Math.min(2,cooldownHandler
                        .isOnCoolDown("last_fast",TimeUnit.SECONDS,
                                30) ? entityData
                        .updateData("fastCount",int.class,initial -> ++initial,1) : 0)));

                cooldownHandler.setOnCoolDown("last_fast");


            }

        }

    },location -> 100,(event,livingEntity,grade)-> {
        livingEntity.addPotionEffect(PotionEffectType.SLOW.createEffect(500,2));
        livingEntity.addPotionEffect(PotionEffectType.REGENERATION.createEffect(100,1));



    },"&eWalker",10,2,0,2.5,0.25,0.02),
    BRUTE(EntityType.ZOMBIE,(event,livingEntity) -> {

        if (event instanceof EntityDamageEvent entityDamageEvent &&
        entityDamageEvent.getEntity() == livingEntity &&
        TempEntityDataHandler.getorAdd(livingEntity)
                .updateData("bone_plating",
                        Integer.class,initial -> initial-1,0) > 0) {

            entityDamageEvent.setDamage(entityDamageEvent.getDamage()*(0.7-0.05*MobType
                    .getGrade(livingEntity)));
            ParticleUtils.drawSphere(livingEntity.getEyeLocation(),
                    Main.Instance.getParticleNativeAPI().LIST_1_13.COMPOSTER,
                    2.5,10,3, Bukkit
                            .getOnlinePlayers().toArray(Player[]::new));


        }

        if (event instanceof EntityDamageByEntityEvent entityDamageByEntityEvent &&
        entityDamageByEntityEvent.getEntity() == livingEntity) {

            TempEntityDataHandler.EntityData entityData = TempEntityDataHandler.getorAdd(livingEntity);

            if (entityData.get("explode_stage",Boolean.class,false)) return;

            int grade = MobType.getGrade(livingEntity);

            int delay = 120-5*grade;

            livingEntity.addPotionEffect(PotionEffectType.SPEED.createEffect(delay,1));
            entityData.playerCooldownHandler.setOnCoolDown("explode_timeStamp");
            entityData.updateData("explode_stage",Boolean.class,initial ->
                    true,true);
            entityData.updateData("itemStack_helmet",ItemStack.class,initial ->
                    livingEntity.getEquipment()
                            .getHelmet(),null);


            livingEntity.getEquipment().setHelmet(new ItemBuilder(Material.TNT).build(false));



        }


        if (event instanceof TickedSMPEvent) {

            TempEntityDataHandler.EntityData entityData = TempEntityDataHandler
                    .getorAdd(livingEntity);

            if (!entityData.get("explode_stage",Boolean.class,false)) return;

            int grade = MobType.getGrade(livingEntity);
            if (!entityData.playerCooldownHandler.isOnCoolDown("explode_timeStamp",
                    TimeUnit.MILLISECONDS,50*(120-5L*MobType
                            .getGrade(livingEntity))))  {
                double health = livingEntity.getHealth(),distance = 3+grade;

                Main.Instance.getParticleNativeAPI()
                                .LIST_1_8.EXPLOSION_LARGE.packet(true,livingEntity
                                .getEyeLocation()).sendTo(Bukkit.getOnlinePlayers());
                List<Player> players = ParticleUtils.getNearbyPlayers(livingEntity
                        .getEyeLocation(),distance,player ->
                        player.getEyeLocation().distance(player
                                .getLocation()) <= distance);
                SoundAPI.playSound(livingEntity,"brute_explode");
                players.forEach(player -> {
                    double v = player.getLocation().distance(livingEntity
                            .getEyeLocation());
                    player.damage(health*((v/(2+v))));
                    player.setVelocity(livingEntity
                            .getEyeLocation()
                            .toVector().subtract(player
                                    .getLocation()
                                    .toVector()).normalize().multiply(-0.75*(distance-v)));
                });

                livingEntity.damage(health);

                entityData.updateData("explode_stage",Boolean.class,initial ->
                        false,false);
                livingEntity.getEquipment()
                        .setHelmet(entityData.get("itemStack_helmet",ItemStack.class,null));

                return;
            }

            if (entityData.updateData("explode_particle_counter",Integer.class,
                    initial -> initial >= 31 ? 0 : initial+1,0) >= 10) {
                long totalTimeFrame = 50*(120-5L*MobType
                        .getGrade(livingEntity)), milliSeconds = entityData.playerCooldownHandler.getTimeLeftOnCooldown("explode_timeStamp",
                        TimeUnit.MILLISECONDS,totalTimeFrame,TimeUnit.MILLISECONDS);
                double progress = (double) milliSeconds / totalTimeFrame;
                Color color = Color.fromRGB(Math.max(0,Math.min( (int) (progress < 0.5 ? 0 : Math.round(255-(progress-0.5)*1000)),255)),
                        Math.max(0,Math.min( (int) (progress < 0.65 ? 255 : Math.round(255-(progress-0.65)*750)),255)),
                        Math.max(0,Math.min( (int) (progress > 0.85 ? 0 : Math.round(0+(0.85-progress)*750)),255)));
                ParticleUtils
                        .makeCircle(location ->
                                        Main.Instance
                                                .getParticleNativeAPI()
                                                .LIST_1_13.DUST
                                                .color(color,1)
                                                .packet(true,location),livingEntity.getLocation()
                                ,10,50,3+grade,Bukkit.getOnlinePlayers()
                                        .toArray(Player[]::new));
                SoundAPI.playSound(livingEntity,"brute_plan_to_explode", (float) (progress*2f), (float) (progress*2f));
            }
        }


    },location -> location
            .getY() <= 65 ? 50 : 0,(event,livingEntity,grade)-> {

        TempEntityDataHandler.getorAdd(livingEntity)
                .updateData("bone_plating",Integer.class,initial -> 3,3);

    },"&cBrute",30,1,-0.3,5,0.35,0.01,
            25,25,25,25,25,25)


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


    public static Map<EntityType,MobTypeContainer> getContainers() {
        return map;
    }

    public static MobType getMobType(LivingEntity livingEntity) {
        return livingEntity.hasMetadata("mobType") ? MobType.valueOf(livingEntity
                .getMetadata("mobType").get(0).asString().toUpperCase()) : null;
    }


    public static MobTypeContainer get(EntityType entityType) {
        return map.get(entityType);
    }

    MobType(EntityType entityType,EventHook eventHook,SpawnCondition spawnCondition,EventListener<EntitySpawnEvent> spawnListener,String displayName,double health,double damage,double moveSpd,
            double healthIncrement,double damageIncrement,double moveSpdIncrement,double helmetOdds,double chestplateOdds,double leggingOdds,double bootOdds,double offHandOdds,double mainHandOdds) {
        this(entityType,eventHook,spawnCondition,spawnListener,displayName,health,damage,moveSpd,healthIncrement,damageIncrement,
                moveSpdIncrement,null,helmetOdds,chestplateOdds,leggingOdds,bootOdds,offHandOdds,mainHandOdds);
    }

    MobType(EntityType entityType,EventHook eventHook,SpawnCondition spawnCondition,EventListener<EntitySpawnEvent> spawnListener,String displayName,double health,double damage,double moveSpd,
            double healthIncrement,double damageIncrement,double moveSpdIncrement,YGrader yGrader) {
        this(entityType,eventHook,spawnCondition,spawnListener,displayName,health,damage,moveSpd,healthIncrement,damageIncrement,
                moveSpdIncrement,yGrader,0,0,0,0,0,0);
    }

    MobType(EntityType entityType,EventHook eventHook,SpawnCondition spawnCondition,EventListener<EntitySpawnEvent> spawnListener,String displayName,double health,double damage,double moveSpd,
            double healthIncrement,double damageIncrement,double moveSpdIncrement) {
        this(entityType,eventHook,spawnCondition,spawnListener,displayName,health,damage,moveSpd,healthIncrement,damageIncrement,
                moveSpdIncrement,null,0,0,0,0,0,0);
    }

    MobType(EntityType entityType,EventHook eventHook,SpawnCondition spawnCondition,EventListener<EntitySpawnEvent> spawnListener,String displayName,double health,double damage,double moveSpd,
            double healthIncrement,double damageIncrement,double moveSpdIncrement,YGrader yGrader,
            double helmetOdds,double chestplateOdds,double leggingOdds,double bootOdds,double offHandOdds,double mainHandOdds) {
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
        this.yGrader = yGrader != null ? yGrader :
                location -> Math.min(Math.max(Utils.RNG_INT(location
                        .getY() >= 70 ? 1 : 1+(70-location.getY())/20,location.getY() >= 85 ? 1 : 1+(85-location
                        .getY())/12),1),6);
        this.equipmentHandler = new HashMap<>();
        equipmentHandler.put(EquipmentSlot.HEAD,helmetOdds);
        equipmentHandler.put(EquipmentSlot.CHEST,chestplateOdds);
        equipmentHandler.put(EquipmentSlot.LEGS,leggingOdds);
        equipmentHandler.put(EquipmentSlot.FEET,bootOdds);
        equipmentHandler.put(EquipmentSlot.HAND,mainHandOdds);
        equipmentHandler.put(EquipmentSlot.OFF_HAND,offHandOdds);


        this.eventHook = eventHook != null ? eventHook : (event,livingEntity) -> {};
    }

    public static LivingEntity updateEntity(@Nullable EntitySpawnEvent entitySpawnEvent, LivingEntity livingEntity, MobType mobType, MobModifierType mobModifierType, int grade) {
        return mobType.updateEntity(entitySpawnEvent,livingEntity,mobModifierType,grade);
    }

    public static int getGrade(LivingEntity livingEntity) {
        return livingEntity != null && livingEntity.hasMetadata("grade")
                ? livingEntity.getMetadata("grade").get(0).asInt() : 0;
    }

    public LivingEntity updateEntity(@Nullable EntitySpawnEvent entitySpawnEvent,LivingEntity livingEntity,MobModifierType mobModifierType,int grade) {

        if (livingEntity == null) return null;

        grade = Math.min(6,Math.max(1,grade));

        AttributeInstance maxHealth = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        maxHealth.setBaseValue(health);
        maxHealth.addModifier(new AttributeModifier("grade_maxhealth_"+livingEntity.getEntityId()+"_"+grade,healthIncrement*(grade-1), AttributeModifier.Operation.ADD_NUMBER));

//        livingEntity.setMaxHealth(health+healthIncrement*(grade-1));
//
        livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).addModifier(new AttributeModifier("grade_attackdamage_"+livingEntity.getEntityId()+"_"+grade,
                Math.max(damage+damageIncrement*(grade-1),1), AttributeModifier.Operation.ADD_NUMBER));
        livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).addModifier(new AttributeModifier("grade_movespd_"+livingEntity.getEntityId()+"_"+grade,
                moveSpd+moveSpdIncrement*(grade-1), AttributeModifier.Operation.MULTIPLY_SCALAR_1));

        int finalGrade = grade;
        livingEntity.setMetadata("grade",new LazyMetadataValue(Main.Instance, LazyMetadataValue.CacheStrategy.NEVER_CACHE,() -> finalGrade));
        livingEntity.setMetadata("mobType",new LazyMetadataValue(Main.Instance, LazyMetadataValue.CacheStrategy.NEVER_CACHE, this::name));
        livingEntity.setMetadata("mobModifierType",new LazyMetadataValue(Main.Instance, LazyMetadataValue.CacheStrategy.NEVER_CACHE, mobModifierType::name));
        livingEntity.setMetadata("displayName",new LazyMetadataValue(Main.Instance,
                LazyMetadataValue.CacheStrategy.NEVER_CACHE,()->
                mobModifierType.displayName+" "+displayName +" "+
                MobUtils.getGrade(finalGrade)));

        if (mobModifierType.listener != null) mobModifierType.listener.onSpawn(entitySpawnEvent,livingEntity,grade);

        if (spawnListener != null) spawnListener.onSpawn(entitySpawnEvent,livingEntity,grade);

        EntityEquipment entityEquipment = livingEntity.getEquipment();

        ItemStack itemStack = entityEquipment.getItemInMainHand();


        Map<EquipmentSlot, ItemStack> equipment = EquipmentHandler.randomEquipment(livingEntity.getLocation().getY(),
                equipmentHandler,!Utils.isNullorAir(itemStack) && (itemStack.getType()
                == Material.BOW || itemStack.getType() == Material.CROSSBOW));

        entityEquipment.setHelmet(equipment.get(EquipmentSlot.HEAD));
        entityEquipment.setChestplate(equipment.get(EquipmentSlot.CHEST));
        entityEquipment.setLeggings(equipment.get(EquipmentSlot.LEGS));
        entityEquipment.setBoots(equipment.get(EquipmentSlot.FEET));
        entityEquipment.setItemInMainHand(equipment.get(EquipmentSlot.HAND));
        entityEquipment.setItemInOffHand(equipment.get(EquipmentSlot.OFF_HAND));

        livingEntity.setHealth(livingEntity.getMaxHealth());

        MobUtils.updateEntity(livingEntity);

        return livingEntity;

    }

    public final EventHook eventHook;
    public final EntityType entityType;
    public final SpawnCondition spawnCondition;
    public final EventListener<EntitySpawnEvent> spawnListener;
    public final double health,damage,moveSpd,
    healthIncrement,damageIncrement,moveSpdIncrement;
    public final String displayName;
    public final YGrader yGrader;
    public final Map<EquipmentSlot,Double> equipmentHandler;


    public interface SpawnCondition {

        double canSpawn(Location location);


    }

    public interface EventListener<T extends Event> {

        void onSpawn(T event, LivingEntity livingEntity, int grade);

    }


    public interface YGrader {

        int getGrade(Location location);

    }

    public interface EventHook {

        void onEvent(Event event,LivingEntity livingEntity);

    }



}
