package com.SMPCore.mobs;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public enum MobType {



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


    MobType(EntityType entityType,SpawnCondition spawnCondition) {
        this.entityType = entityType;
        this.spawnCondition = spawnCondition;
    }

    public final EntityType entityType;
    public final SpawnCondition spawnCondition;


    public interface SpawnCondition {


        boolean canSpawn(Location location);

    }



}
