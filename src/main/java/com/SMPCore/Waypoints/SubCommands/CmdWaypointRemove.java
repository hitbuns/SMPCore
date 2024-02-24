package com.SMPCore.Waypoints.SubCommands;

import com.MenuAPI.Utils;
import com.SMPCore.Waypoints.WaypointConfig;
import com.SMPCore.Waypoints.WaypointRemovalPrompt;
import com.SMPCore.commands.SubCommands.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CmdWaypointRemove extends SubCommand {
    @Override
    public String getId() {
        return "remove";
    }

    @Override
    public String getUsage() {
        return "remove <wayPointId>";
    }

    @Override
    public void run(CommandSender commandSender, String cmd, String[] args) {


        if (!(commandSender.hasPermission("staff.admin"))) {
            commandSender.sendMessage(Utils.color("&cYou do not have permission to run this command!"));
            return;
        }

        if (args.length == 0) return;

        WaypointConfig.WayPointInfo wayPointInfo = WaypointConfig.getInstance()
                .getWayPointRegistry().get(args[0]);

        if (wayPointInfo == null) {
            commandSender.sendMessage(Utils.color("&cPlease provide a valid waypoint id to remove"));
            return;
        }

        if (commandSender instanceof Player player) {
            new WaypointRemovalPrompt(player,wayPointInfo,null).open(player);
            return;
        }

        WaypointConfig.getInstance().deregister(wayPointInfo.getId());

    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String cmd, String[] args) {
        return args.length == 1 ? WaypointConfig.getInstance().getWayPointRegistry()
                .keySet().stream().filter(s -> s.toLowerCase()
                        .startsWith(args[args.length-1].toLowerCase())).collect(Collectors.toList()) : null;
    }

    @Override
    public Predicate<CommandSender> getPredicate() {
        return commandSender -> commandSender.hasPermission("staff.admin");
    }
}
