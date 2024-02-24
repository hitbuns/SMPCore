package com.SMPCore.commands.SubCommands;



import com.SMPCore.Waypoints.CmdWaypoint;
import com.SMPCore.Waypoints.SubCommands.CmdWaypointAdd;
import com.SMPCore.Waypoints.SubCommands.CmdWaypointRemove;
import com.SMPCore.Waypoints.SubCommands.CmdWaypointSet;

import java.util.function.Supplier;

public enum SubCommandType {

    //EVENT

    WAY_POINT_GIVE(CmdWaypoint.class, CmdWaypointAdd::new),
    WAY_POINT_ADD(CmdWaypoint.class, CmdWaypointRemove::new),
    WAY_POINT_SET(CmdWaypoint.class, CmdWaypointSet::new)

    ;


    SubCommandType(Class<? extends SubCommandExecutor> clazz,Supplier<SubCommand> subCommandSupplier) {
        this(clazz,subCommandSupplier.get());
    }

    SubCommandType(Class<? extends SubCommandExecutor> clazz,SubCommand subCommand) {
        this.clazz = clazz;
        this.subCommand = subCommand;
    }

    private final Class<? extends SubCommandExecutor> clazz;
    private final SubCommand subCommand;

    public Class<? extends SubCommandExecutor> getClazz() {
        return clazz;
    }

    public SubCommand getSubCommand() {
        return subCommand;
    }
}
