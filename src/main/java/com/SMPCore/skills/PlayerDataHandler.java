package com.SMPCore.skills;

import com.DatabaseAPI.MongoInteractor;
import com.MenuAPI.BukkitEventCaller;
import com.MenuAPI.Config;
import com.SMPCore.Events.ExpIdExpGainEvent;
import com.SMPCore.Events.ExpIdExpRemoveEvent;
import com.SMPCore.Events.ExpIdLevelUpEvent;
import com.SMPCore.Events.ExpIdSetLevelEvent;
import com.SMPCore.skills.impl.NonCombatStatType;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.lang.NonNull;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerDataHandler {

    static Config config;
    public static PlayerDataHandler Instance;
    public final Map<String,ExpIdContainer> expIdContainers;

    public static class ExpIdContainer {

        public final Map<String,ExpId> expIds;
        public final String keyId;

        public ExpIdContainer(String keyId,ExpId... expIds) {
            this.keyId = keyId;
            this.expIds = Arrays.stream(expIds).filter(Objects::nonNull)
                    .collect(Collectors.toMap(ExpId::getKey,
                            expId ->  expId));
        }

    }

    public PlayerDataHandler() {
        Reflections reflections = new Reflections("com.SMPCore.skills.impl");
        expIdContainers = new HashMap<>();
        reflections.getSubTypesOf(ExpId.class).forEach(aClass -> {

            if (aClass.isEnum()) {
                ExpIdContainer expIdContainer = new ExpIdContainer(aClass.getSimpleName(),aClass.getEnumConstants());
                expIdContainers.put(expIdContainer.keyId, expIdContainer);
                return;
            }

            try {
                ExpId expId = aClass.getDeclaredConstructor().newInstance();
                ExpIdContainer expIdContainer = new ExpIdContainer(expId.getClass().getSimpleName(),expId);
                expIdContainers.put(expIdContainer.keyId,expIdContainer);
            } catch (Exception e) {
                System.out.println("You must have a non-args constructor for it to be automatically registered into the ExpId system");
                throw new RuntimeException(e);
            }

        });
    }

    public ExpIdContainer getExpIdContainer(Class<?> clazz) {
        return expIdContainers.get(clazz.getSimpleName());
    }


    public static void init(JavaPlugin javaPlugin) {
        Instance = new PlayerDataHandler();

        config = new Config(javaPlugin, javaPlugin.getDataFolder(),
                "settings.yml", "default-settings.yml");
    }


    public static int getLevel(OfflinePlayer offlinePlayer,ExpId expId) {
        return offlinePlayer != null ? getPlayerData(offlinePlayer)
                .getInteger("player_level_"+expId.getKey(),1) :
                1;
    }

    public static double getExp(OfflinePlayer offlinePlayer,ExpId expId,ExpType expType) {
        return offlinePlayer != null ? (expType == ExpType.CURRENT ? getPlayerData(offlinePlayer)
                .get("player_exp_"+expId.getKey(),0D) :
                getGoalExp(expId,getLevel(offlinePlayer, expId))) :
                0;
    }

    static String getDocumentUUIDValue(@NonNull UUID uuid) {
        return String.valueOf(uuid);
    }

    static String getDocumentUUIDValue(@NonNull OfflinePlayer offlinePlayer) {
        return getDocumentUUIDValue(offlinePlayer.getUniqueId());
    }

    public static void addExp(OfflinePlayer offlinePlayer, ExpId expId, ExpReason expReason, double amount) {
        if (offlinePlayer != null) {
            ExpIdExpGainEvent statTypeExpGainEvent = new ExpIdExpGainEvent(offlinePlayer,expId,amount);
            if (!BukkitEventCaller.callEvent(statTypeExpGainEvent)) {
                setExp(offlinePlayer,expId,expReason,getExp(offlinePlayer,expId,ExpType.CURRENT)+statTypeExpGainEvent
                        .getExpAmount());
            }
        }
    }

    public static void setExp(OfflinePlayer offlinePlayer, ExpId expId,ExpReason expReason, double amount) {
        if (offlinePlayer != null) {
            double value = Math.max(0,amount);
            MongoInteractor.getInstance().update("player-data",
                    Filters.eq("uuid",getDocumentUUIDValue(offlinePlayer)),
                    Updates.set("player_exp_"+expId.getKey(),value));
            invokeCheck(offlinePlayer,expId,value);
        }
    }

    public static void removeExp(OfflinePlayer offlinePlayer, ExpId expId, ExpReason expReason, double amount) {
        if (offlinePlayer != null) {
            ExpIdExpRemoveEvent statTypeExpGainEvent = new ExpIdExpRemoveEvent(offlinePlayer,expId,amount);
            if (!BukkitEventCaller.callEvent(statTypeExpGainEvent)) {
                setExp(offlinePlayer,expId,expReason,Math.max(0,getExp(offlinePlayer,expId,ExpType.CURRENT)-statTypeExpGainEvent
                        .getSubtractedAmount()));
            }
        }
    }

    public static void setLevel(OfflinePlayer offlinePlayer,ExpId expId,int level) {
        if (offlinePlayer != null) {
            int a = Math.max(1, Math.min(level, getMaxLevel(expId)));
            BukkitEventCaller.callEvent(new ExpIdSetLevelEvent(offlinePlayer,expId,a));
            MongoInteractor.getInstance().update("player-data",
                    Filters.eq("uuid", getDocumentUUIDValue(offlinePlayer)),
                    Updates.set("player_level_" + expId.getKey(), a),
                    Updates.set("player_exp_" + expId.getKey(), 0.0));
        }
    }

    static void invokeCheck(OfflinePlayer offlinePlayer,ExpId expId) {
        invokeCheck(offlinePlayer,expId,-1);
    }

    static void invokeCheck(OfflinePlayer offlinePlayer,ExpId expId,double valueInput) {

        if (valueInput == 0) return;

        double currentExp = valueInput <= -1 ? getExp(offlinePlayer,expId,ExpType.CURRENT) : valueInput;

        int originalLevel = getLevel(offlinePlayer,expId),counter = 0;

        for (int i = originalLevel; i < getMaxLevel(expId); i++) {

            double goalExp = getGoalExp(expId,i);
            if (currentExp >= goalExp) {

                counter++;
                currentExp -= goalExp;

            } else break;

        }

        if (counter > 0) {
            ExpIdLevelUpEvent statTypeLevelUpEvent = new ExpIdLevelUpEvent(offlinePlayer,expId,
                    originalLevel,originalLevel+counter,currentExp);
            if (BukkitEventCaller.callEvent(statTypeLevelUpEvent)) return;

            setLevel(offlinePlayer,expId, statTypeLevelUpEvent.getNewLevel());
            setExp(offlinePlayer,expId,ExpReason.ADMIN,currentExp);

        }

    }

    public static double getGoalExp(ExpId expId,int level) {
        int a= Math.max(1,level);
        if (expId instanceof NonCombatStatType) {
            return Math.round(Math.pow(1.12, a - 1) + 65 * level + 20);
        } else return Math.round(Math.pow(1.18,a+1)+100*level+25);
    }

    public static int getMaxLevel(ExpId expId) {
        return config.getInt("settings.statType.max-level."+expId.getKey(),100);
    }

    public static Document getPlayerData(OfflinePlayer offlinePlayer) {
        return offlinePlayer != null ? getPlayerData(offlinePlayer.getUniqueId()) : null;
    }

    public static Document getPlayerData(UUID uuid) {
        return uuid != null ?
                        MongoInteractor.getInstance().getorAddDocument("player-data","uuid", getDocumentUUIDValue(uuid))
                : null;
    }

    public static void update(OfflinePlayer offlinePlayer, Bson... bsonValues) {
        if (offlinePlayer != null) update(offlinePlayer.getUniqueId(),bsonValues);
    }

    public static void update(UUID uuid,Bson... bsonValues) {
        if (uuid != null) {
            MongoInteractor.getInstance().update("player-data",Filters.eq(
                    "uuid",getDocumentUUIDValue(uuid)
            ),bsonValues);
        }
    }

    public enum ExpType {

        CURRENT,GOAL
        ;

    }

    public interface ExpId {

        String getKey();

        String getDisplay();

    }

}