package com.SMPCore.commands;

import com.MenuAPI.Utils;
import com.SMPCore.skills.gui.AbilityMainMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdAbility implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {


        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage(Utils.color("&cYou do not have permission to use this command!"));
            return true;
        }

        new AbilityMainMenu(player,null).open(player);

        return true;
    }


}
