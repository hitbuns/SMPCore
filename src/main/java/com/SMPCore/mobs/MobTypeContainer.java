package com.SMPCore.mobs;

import org.bukkit.entity.EntityType;

import java.util.Map;

public class MobTypeContainer {


    public final EntityType entityType;
    private final Map<String,MobType> container;


    public MobTypeContainer(EntityType entityType,Map<String,MobType> container) {
        this.entityType = entityType;
        this.container = container;
    }


    public MobType getMobStats(String key) {
        return key != null ? container.getOrDefault(key,null) : null;
    }

    public EntityType getEntityType() {
        return entityType;
    }
}
