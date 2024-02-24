package com.SMPCore.Waypoints;

import com.MenuAPI.Utilities.FormattedNumber;
import com.MenuAPI.Utils;
import com.SMPCore.Events.TickedSMPEvent;
import com.SMPCore.Utilities.ParticleUtils;
import joptsimple.internal.Strings;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WaypointListener implements Listener {

    WaypointConfig waypointConfig;
    private final Map<Player, WaypointConfig.WayPointInfo> playerWayPoints = new HashMap<>();

    public Map<Player, WaypointConfig.WayPointInfo> getPlayerWayPoints() {
        return playerWayPoints;
    }

    public static WaypointListener Instance;

    public WaypointListener(JavaPlugin javaPlugin) {
        Instance = this;
        javaPlugin.getServer().getPluginManager().registerEvents(this,javaPlugin);
        javaPlugin.getCommand("waypoint").setExecutor(new CmdWaypoint());
        javaPlugin.getCommand("waypoints").setExecutor(new CmdWaypoints());
        waypointConfig = new WaypointConfig(javaPlugin);
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onTickEvent(TickedSMPEvent tickedSMPEvent) {

        playerWayPoints.forEach((key, value) -> {
            if (key != null && key.isOnline() && value.getLocation() != null)
                key.spigot()
                        .sendMessage(ChatMessageType.ACTION_BAR,TextComponent.fromLegacyText(Utils.color("&8[" + (value.getDisplayName() !=
                                null ? value.getDisplayName() : ("&e"+ Strings.join(Arrays.stream(value
                                                .getId().split("_"))
                                        .map(s -> s.toUpperCase().charAt(0)+
                                                s.substring(1).toLowerCase()).toArray(String[]::new),
                                " "))) + "&8] - &c" +
                                FormattedNumber
                                        .getInstance().getCommaFormattedNumber(value.getLocation().distance(key.getEyeLocation()), 1) + "m &8(&6" +
                                ParticleUtils
                                        .Direction.getDirection(key,
                                                value.getLocation()).getValue() + "&8)")));
        });

    }


    @EventHandler
    public void onMove(PlayerMoveEvent playerMoveEvent) {

        Player player = playerMoveEvent.getPlayer();
        Location from = playerMoveEvent.getFrom(),to =
                playerMoveEvent.getTo();

        if (playerWayPoints.containsKey(player) && !(from.getX() == to.getX() &&
                from.getY() == to.getY() &&
                from.getZ() == to.getZ())) {

            WaypointConfig.WayPointInfo wayPointInfo = playerWayPoints.get(player);
            if (wayPointInfo.getLocation() == null ||
            wayPointInfo.getLocation().distance(player.getLocation()) <= 5) {
                playerWayPoints.remove(player);
                player.sendMessage(Utils.color("&eYou have reached your waypoint destination!"));
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,TextComponent
                        .fromLegacyText(Utils.color("&eYou have reached your waypoint destination!")));
            }

        }

    }


    @EventHandler
    public void onQuit(PlayerQuitEvent playerQuitEvent) {
        playerWayPoints.remove(playerQuitEvent.getPlayer());
    }



}
