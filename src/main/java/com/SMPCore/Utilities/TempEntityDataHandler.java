package com.SMPCore.Utilities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TempEntityDataHandler implements Listener {

    private final Map<Entity, EntityData> playerMap;
    public static TempEntityDataHandler Instance;


    public static EntityData getorAdd(Entity player) {
        EntityData entityData = Instance.playerMap.get(player);
        if (entityData == null) Instance.playerMap.put(player, entityData =
                new EntityData(player));
        return entityData;
    }

    public TempEntityDataHandler(JavaPlugin javaPlugin) {
        Instance = this;

        javaPlugin.getServer().getPluginManager().registerEvents(this,javaPlugin);

        playerMap = Bukkit.getOnlinePlayers().stream().collect(Collectors.toMap(
                player -> player, EntityData::new
        ));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent playerJoinEvent) {
        playerMap.put(playerJoinEvent.getPlayer(),new EntityData(playerJoinEvent.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent playerQuitEvent) {
        playerMap.remove(playerQuitEvent.getPlayer());
    }


    @EventHandler
    public void onDeath(EntityDeathEvent entityDeathEvent) {
        if (entityDeathEvent.getEntity() instanceof Player) return;

        playerMap.remove(entityDeathEvent.getEntity());

    }

    @EventHandler
    public void onRemove(EntityRemoveEvent entityRemoveEvent) {
        if (entityRemoveEvent.getEntity() instanceof Player) return;

        playerMap.remove(entityRemoveEvent.getEntity());
    }

    public static class EntityData {

        public Map<String,Object> map = new HashMap<>();
        public final CooldownHandler<Entity> playerCooldownHandler;
        private final Entity player;

        public <T> T get(String key,Class<T> clazz,T def) {
            try {
                return (T) map.getOrDefault(key,def);
            } catch (Exception exception) {
                return def;
            }
        }

        public <T> T updateData(String key,Class<T> clazz,Updater<T> updater,T def) {
            T object = updater.update(get(key,clazz,def));
            map.put(key,object);
            return object;
        }


        public EntityData(Entity player) {
            playerCooldownHandler = new CooldownHandler<>(this.player =player);
        }

        public Entity getPlayer() {
            return player;
        }
    }

    public interface Updater<T> {

        T update(T initial);

    }


}
