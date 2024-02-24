package com.SMPCore.commands.SubCommands;

import com.MenuAPI.Utils;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class SubCommandHelp extends SubCommand{

    final SubCommandExecutor subCommandExecutor;
    final Predicate<CommandSender> commandSenderPredicate;

    SubCommandHelp(SubCommandExecutor subCommandExecutor,Predicate<CommandSender> commandSenderPredicate) {
        this.subCommandExecutor = subCommandExecutor;
        this.commandSenderPredicate = commandSenderPredicate;

    }

    @Override
    public String getId() {
        return "help";
    }

    @Override
    public String getUsage() {
        return "help [page]";
    }

    @Override
    public void run(CommandSender commandSender, String cmd, String[] args) {

        if (args.length != 1) {
            commandSender.sendMessage(Utils.color("&cUsage: /"+cmd+" help [page]"));
            return;
        }

        try {

            int page = Integer.parseInt(args[0]);

            subCommandExecutor.sendUsage(commandSender, cmd, page);
        } catch (Exception exception) {
            commandSender.sendMessage(Utils.color("&cUsage: /"+cmd+" help [page]"));
        }

    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String cmd, String[] args) {
        return args.length == 1 ? Collections.singletonList("page:") : null;
    }

    @Override
    public Predicate<CommandSender> getPredicate() {
        return commandSenderPredicate;
    }
}
