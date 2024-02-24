package com.SMPCore.Waypoints.SubCommands;

import com.MenuAPI.Utils;
import com.SMPCore.Waypoints.WaypointConfig;
import com.SMPCore.Waypoints.WaypointUpdatePrompt;
import com.SMPCore.commands.SubCommands.SubCommand;
import joptsimple.internal.Strings;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CmdWaypointAdd extends SubCommand {

    @Override
    public String getId() {
        return "add";
    }

    @Override
    public String getUsage() {
        return "add <id> <materialIcon> <displayName>";
    }

    @Override
    public void run(CommandSender commandSender, String cmd, String[] args) {

        if (!(commandSender instanceof Player player && commandSender.hasPermission("staff.admin"))) {
            commandSender.sendMessage(Utils.color("&cYou do not have permission to run this command!"));
            return;
        }


        if (args.length < 3) {
            commandSender.sendMessage(Utils.color("/"+cmd+" "+getUsage()));
            return;
        }

        String id = args[0];

        try {

            Material material = Material.valueOf(args[1].toUpperCase());
            String displayName = Strings.join(Arrays.copyOfRange(args,2,args.length)," ");

            WaypointConfig.WayPointInfo wayPointInfo = new WaypointConfig.WayPointInfo(id,displayName,
                    player.getLocation(),material);
            if (WaypointConfig.getInstance().getWayPointRegistry().containsKey(id)) {
                new WaypointUpdatePrompt(player,wayPointInfo).open(player);
                return;
            }


            WaypointConfig.getInstance().register(wayPointInfo,false);
            player.sendMessage(Utils.color("&a["+wayPointInfo.getId()+"] &eYou have successfully created this waypoint!"));

        } catch (Exception exception) {
            commandSender.sendMessage(Utils.color("/"+cmd+" "+getUsage()));
        }

    }

    String[] materials = Arrays.stream(Material.values()).filter(material ->
            !(material.isAir()) && material.isBlock())
            .map(Enum::name).toArray(String[]::new);
    List<String> list = Collections.singletonList("displayName: ...");

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String cmd, String[] args) {
        return switch (args.length) {
            case 1 -> WaypointConfig.getInstance().getWayPointRegistry().keySet()
                    .stream().filter(s -> s.toLowerCase().startsWith(args[args.length-1].toLowerCase()))
                    .collect(Collectors.toList());
            case 2 -> Arrays.stream(materials).filter(s ->
                    s.toLowerCase().startsWith(args[args.length-1].toLowerCase()))
                    .collect(Collectors.toList());
            default -> list;
        };
    }

    @Override
    public Predicate<CommandSender> getPredicate() {
        return commandSender -> commandSender.hasPermission("staff.admin");
    }



}
