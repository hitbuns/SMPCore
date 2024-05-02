package com.SMPCore.commands.SubCommands;

import com.MenuAPI.Utils;
import com.SMPCore.LootTableSystem.RewardsAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SubCommandLootTableExecute extends SubCommand{
    @Override
    public String getId() {
        return "execute";
    }

    @Override
    public String getUsage() {
        return "execute <loot_table_id> [player] [amount]";
    }

    @Override
    public void run(CommandSender commandSender, String cmd, String[] args) {

        if (args.length < 1) {
            commandSender.sendMessage(Utils.color("&cUsage: /"+cmd+" execute <loot_table_id> [player] [amount]"));
            return;
        }

        String lootTableId = args[0];

        try {

            Player player = args.length > 1 ? Bukkit.getPlayer(args[1]) : commandSender instanceof
                    Player player1 ? player1 : null;
            int amount = args.length > 2 ? Integer.parseInt(args[2]) : 1;

            if (amount <= 1) {
                RewardsAPI.rewardPlayer(player, RewardsAPI.RewardReason.ADMIN,lootTableId,0);
                commandSender.sendMessage(Utils.color("&eRewarded "+player.getName()+" Loot Table Id = {"+lootTableId+"}"));
                return;
            }

            for (int i = 0; i < amount; i++) {
                RewardsAPI.rewardPlayer(player, RewardsAPI.RewardReason.ADMIN,lootTableId,0);
            }
            commandSender.sendMessage(Utils.color("&eRewarded "+player.getName()+" Loot Table Id = {"+lootTableId+"}&cx"+amount));

        } catch (Exception exception) {
            commandSender.sendMessage(Utils.color("&cUsage: /"+cmd+" execute <loot_table_id> [player] [amount]"));
        }

    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, String cmd, String[] args) {
        return args.length == 1 ? RewardsAPI.getInstance().getorAddConfigurationSection("rewards").getKeys(false)
                .stream().filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList()) :
                null;
    }

    @Override
    public Predicate<CommandSender> getPredicate() {
        return commandSender -> commandSender.hasPermission("staff.admin");
    }
}
