package com.SMPCore.commands;

import com.MenuAPI.Utils;
import com.SMPCore.LootTableSystem.LootTableMainMenu;
import com.SMPCore.commands.SubCommands.SubCommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdLootTable extends SubCommandExecutor {

    @Override
    public boolean onCommandNoArgs(CommandSender commandSender, String cmd) {

        if (!(commandSender instanceof Player player && commandSender.hasPermission("staff.admin"))) {
            commandSender.sendMessage(Utils.color("&cYou do not have permission to run this command!"));
            return false;
        }

        new LootTableMainMenu(player,null).open(player);

        return false;
    }
}
