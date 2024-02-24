package com.SMPCore.Waypoints.SubCommands;

import com.MenuAPI.Utils;
import com.SMPCore.Waypoints.WaypointConfig;
import com.SMPCore.Waypoints.WaypointListener;
import com.SMPCore.commands.SubCommands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CmdWaypointSet extends SubCommand {


    @Override
    public String getId() {
        return "set";
    }

    @Override
    public String getUsage() {
        return "set <waypointId>";
    }

    @Override
    public void run(CommandSender commandSender, String cmd, String[] args) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(Utils.color("&cYou cannot use this command in console!"));
            return;
        }


        WaypointConfig.WayPointInfo wayPointInfo = WaypointConfig
                .getInstance().getWayPointRegistry().get(args[0]);


        if (wayPointInfo == null) {
            commandSender.sendMessage(Utils.color("&cYou must provide a valid waypoint id!"));
            return;
        }

        WaypointListener.Instance.getPlayerWayPoints().put(player,wayPointInfo);
        player.sendMessage(Utils.color("&6You have set your waypoint to "+wayPointInfo
                .getDisplayName()));
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String cmd, String[] args) {
        return args.length == 1 ? WaypointConfig.getInstance().getWayPointRegistry().keySet()
                .stream().filter(s1 -> s1.startsWith(args[0])).collect(Collectors.toList()) : null;
    }

    @Override
    public Predicate<CommandSender> getPredicate() {
        return null;
    }
}
