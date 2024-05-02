package com.SMPCore.commands.SubCommands;

import com.SMPCore.LootTableSystem.LootTableMainMenu;
import com.SMPCore.LootTableSystem.LootTableManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Predicate;

public class SubCommandLootTableManager extends SubCommand{

    @Override
    public String getId() {
        return "manager";
    }

    @Override
    public String getUsage() {
        return "manager";
    }

    @Override
    public void run(CommandSender commandSender, String cmd, String[] args) {

        Player player = (Player) commandSender;

        new LootTableManager(player,new LootTableMainMenu(player,null)).open(player);

    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String cmd, String[] args) {
        return null;
    }

    @Override
    public Predicate<CommandSender> getPredicate() {
        return commandSender -> commandSender instanceof Player && commandSender.hasPermission("staff.admin");
    }
}
