package com.SMPCore.Utilities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TempPlayerDataHandler implements Listener {

    private final Map<Player,PlayerData> playerMap;
    public static TempPlayerDataHandler Instance;


    public static PlayerData getorAdd(Player player) {
        PlayerData playerData = Instance.playerMap.get(player);
        if (playerData == null) Instance.playerMap.put(player,playerData =
                new PlayerData(player));
        return playerData;
    }

    public TempPlayerDataHandler(JavaPlugin javaPlugin) {
        Instance = this;

        javaPlugin.getServer().getPluginManager().registerEvents(this,javaPlugin);

        playerMap = Bukkit.getOnlinePlayers().stream().collect(Collectors.toMap(
                player -> player, PlayerData::new
        ));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent playerJoinEvent) {
        playerMap.put(playerJoinEvent.getPlayer(),new PlayerData(playerJoinEvent.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent playerQuitEvent) {
        playerMap.remove(playerQuitEvent.getPlayer());
    }

    public static class PlayerData {

        public Map<String,Object> map = new HashMap<>();
        public final CooldownHandler<Player> playerCooldownHandler;
        private final Player player;

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


        public PlayerData(Player player) {
            playerCooldownHandler = new CooldownHandler<>(this.player =player);
        }

        public Player getPlayer() {
            return player;
        }
    }

    public interface Updater<T> {

        T update(T initial);

    }


}
