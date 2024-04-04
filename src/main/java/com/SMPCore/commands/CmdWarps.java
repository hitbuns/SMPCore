package com.SMPCore.commands;

import com.SMPCore.Main;
import com.SMPCore.gui.WarpGUI;
import joptsimple.internal.Strings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdWarps implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("Warps: "+ Strings.join(Main.Instance.essentials.getWarps().getList().toArray(String[]::new),","));
            return true;
        }

        new WarpGUI(player,null).open(player);

        return true;
    }
}
