package com.SMPCore.commands.SubCommands;

import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.function.Predicate;


public abstract class SubCommand {

    public abstract String getId();
    public abstract String getUsage();
    public abstract void run(CommandSender commandSender,String cmd,String[] args);
    public abstract List<String> onTabComplete(CommandSender commandSender,String cmd,String[] args);
    public abstract Predicate<CommandSender> getPredicate();

}
