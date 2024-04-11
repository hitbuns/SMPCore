package com.SMPCore.commands;

import com.MenuAPI.Utils;
import com.SMPCore.skills.PlayerDataHandler;
import com.SMPCore.skills.gui.SkillGUI;
import com.SMPCore.skills.impl.CombatStatType;
import com.SMPCore.skills.impl.NonCombatStatType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdSkills implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        OfflinePlayer target = strings.length > 0 ? Bukkit.getOfflinePlayer(strings[0]) : commandSender instanceof Player
                player ? player : null;

        if (target == null) {
            commandSender.sendMessage(Utils.color("&cUser is not online!"));
            return  true;
        }

        if (!(commandSender instanceof Player player)) {
            System.out.println("Skill Player Profile - "+target.getName());
            for (NonCombatStatType value : NonCombatStatType.values()) {
                System.out.println(value.name()+": Lvl. "+ PlayerDataHandler.getLevel(target,value)+" ("+
                        PlayerDataHandler.getExp(target,value, PlayerDataHandler.ExpType.CURRENT)+"/"+
                        PlayerDataHandler.getExp(target,value, PlayerDataHandler.ExpType.GOAL)+")");
            }
            for (CombatStatType value : CombatStatType.values()) {
                System.out.println(value.name()+": Lvl. "+ PlayerDataHandler.getLevel(target,value)+" ("+
                        PlayerDataHandler.getExp(target,value, PlayerDataHandler.ExpType.CURRENT)+"/"+
                        PlayerDataHandler.getExp(target,value, PlayerDataHandler.ExpType.GOAL)+")");
            }
            return true;
        }

        new SkillGUI(player,target,null).open(player);

        return true;
    }


}
