package com.SMPCore.commands.SubCommands;

import com.MenuAPI.Utils;
import com.SMPCore.LootTableSystem.LootTableCreator;
import com.SMPCore.LootTableSystem.RewardsAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SubCommandLootTableUpdater extends SubCommand{
    @Override
    public String getId() {
        return "update";
    }

    @Override
    public String getUsage() {
        return "update <id>";
    }

    @Override
    public void run(CommandSender commandSender, String cmd, String[] args) {

        Player player = (Player) commandSender;
        try {
            new LootTableCreator(player, args[0].replace(".","_"),null).open(player);
        } catch (Exception exception) {
            commandSender.sendMessage(Utils.color("&eUsage: /"+cmd+" update <id>"));
        }

    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String cmd, String[] args) {
        return args.length == 1 ? RewardsAPI.getInstance().getorAddConfigurationSection("rewards")
                .getKeys(false).stream().filter(s -> s.toUpperCase().startsWith(args[0].toUpperCase())).sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList()) : null;
    }

    @Override
    public Predicate<CommandSender> getPredicate() {
        return commandSender -> commandSender instanceof Player && commandSender.hasPermission("staff.admin");
    }
}
