package com.SMPCore.Waypoints;

import com.SMPCore.commands.SubCommands.SubCommandExecutor;
import org.bukkit.command.CommandSender;

public class CmdWaypoint extends SubCommandExecutor {

    //CMD: /waypoint <add/set> <player>


    @Override
    public boolean onCommandNoArgs(CommandSender commandSender, String cmd) {
        return true;
    }
}
