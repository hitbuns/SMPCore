package com.SMPCore.mobs;

import com.MenuAPI.Utils;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class MobTypeContainer {


    public final EntityType entityType;
    public final Map<String,MobType> container;
    MobType[] values;

    public MobTypeContainer(EntityType entityType,Map<String,MobType> container) {
        this.entityType = entityType;
        this.container = container != null ? container : new HashMap<>();
        assert container != null;
        values = container.values().toArray(MobType[]::new);
    }

    public double totalChance(Location location) {
        return container.values().stream().mapToDouble(mobType -> mobType.spawnCondition != null ? mobType
                .spawnCondition.canSpawn(location) : 0).sum();
    }

    public MobType random(Location location) {
        double totalChance = totalChance(location),random = Utils.RNG_INT(0,totalChance),next = 0, v = totalChance-random;

        for (MobType mobType : values) {
            double a = (mobType.spawnCondition != null ? mobType.spawnCondition.canSpawn(location) : 0);

            if (a == 0) continue;

            if (v-next <= a) return mobType;
            next += a;
        }
        return values.length == 0 ? null : values[Utils.RNG_INT(0,values.length-1)];
    }


    public MobType getMobStats(String key) {
        return key != null ? container.getOrDefault(key,null) : null;
    }

    public EntityType getEntityType() {
        return entityType;
    }



}
