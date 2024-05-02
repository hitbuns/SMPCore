package com.SMPCore.LootTableSystem;

import com.MenuAPI.BukkitEventCaller;
import com.MenuAPI.Config;
import com.MenuAPI.Utilities.DescriptionBuilder;
import com.MenuAPI.Utilities.ItemBuilder;
import com.MenuAPI.Utils;
import com.SMPCore.LootTableEvents.LootTablePreProcRewardEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RewardsAPI extends Config {

    private static RewardsAPI Instance;

    public RewardsAPI(JavaPlugin javaPlugin) {
        super(javaPlugin, javaPlugin.getDataFolder(),"rewards.yml","default-rewards.yml");
        Instance = this;


    }

    public static RewardsAPI getInstance() {
        return Instance;
    }

    /*
    rewards:
        sheriff:
            - 30::broadcast %player% is ghey::&aThis is an example description.::DIAMOND
     */

    public List<String> getRewardInfo(String path) {
        ConfigurationSection configurationSection = getorAddConfigurationSection("rewards");
        String s = path.replace(".","_");
        return configurationSection.contains(s) && configurationSection.isList(s) ? configurationSection.getStringList(s) : new ArrayList<>();
    }

    public List<String> getRewardInfo(iRewardPath rewardPath) {
        return rewardPath != null ? getRewardInfo(rewardPath.getPath()) : new ArrayList<>();
    }

    public List<LootTableEntry.DescriptiveLootTableEntry> getRewardsPackage(String path) {
        return getRewardInfo(path).stream().map(s -> {
            try {
                String[] split = s.split("::");
                return split.length == 4 ? new LootTableEntry.DescriptiveLootTableEntry(
                        split[1],Double.parseDouble(split[0]),Material.valueOf(split[3].toUpperCase()),
                        split[2]
                ) : null;
            } catch (Exception exception) {
                return null;
            }
        }).collect(Collectors.toList());
    }

    public List<ItemStack> getDescriptiveRewardItems(String path) {
        return getRewardInfo(path).stream().map(s -> {
            try {
                String[] split = s.split("::");
                return split.length == 4 ? new ItemBuilder(Material.valueOf(split[3].toUpperCase()))
                        .setLore(DescriptionBuilder.init()
                                .addLore(split[2],true,30).build()).build(false) : null;
            } catch (Exception exception) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<String> getDescriptionRewards(String path) {
        return getRewardInfo(path).stream().map(s -> {
            String[] split = s.split("::");
            return split.length == 4 ? Utils.color(split[2]) : null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<ItemStack> getDescriptiveRewardItems(iRewardPath rewardPath) {
        return rewardPath != null ? getDescriptiveRewardItems(rewardPath.getPath()) : new ArrayList<>();
    }

    public List<String> getDescriptionRewards(iRewardPath rewardPath) {
        return rewardPath != null ? getDescriptionRewards(rewardPath.getPath()) : new ArrayList<>();
    }


//    public Map<Double,String> getRewardCommands(String path) {
//        return getRewardInfo(path).stream().map(s -> {
//            try {
//                String[] split = s.split("::");
//                return split.length == 4 ? new LootTableEntry(split[1],Double.parseDouble(split[0])) : null;
//            } catch (Exception exception) {
//                exception.printStackTrace();
//                return null;
//            }
//        }).filter(Objects::nonNull).collect(Collectors.toMap(LootTableEntry::getChance,
//                LootTableEntry::getCommand));
//    }
//
//    public Map<Double,String> getRewardCommands(iRewardPath rewardPath) {
//        return rewardPath != null ? getRewardCommands(rewardPath) : new HashMap<>();
//    }
//
//    public Map<Double,String> getRewardCommands(Player player,String path) {
//        return player != null ? getRewardCommands(path).entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
//                doubleStringEntry -> doubleStringEntry.getValue().replace("%player%",
//                        player.getName()))) :  new HashMap<>();
//    }
//
//    public Map<Double,String> getRewardCommands(Player player, iRewardPath rewardPath) {
//        return rewardPath != null ? getRewardCommands(player,rewardPath.getPath())
//                : new HashMap<>();
//    }

    public void execute(Player player,RewardReason rewardReason,String path,double lootFactor) {
        if (player== null) return;
        ConsoleCommandSender consoleCommandSender = Bukkit.getConsoleSender();

        LootTablePreProcRewardEvent lootTablePreProcRewardEvent = new LootTablePreProcRewardEvent(player,
                path,rewardReason,1);
        if (BukkitEventCaller.callEvent(lootTablePreProcRewardEvent)) return;


        getRewardsPackage(path).forEach(descriptiveLootTableEntry -> {
            if (Utils.RNG(0,100) >= 100-
                    (1+lootFactor/(30+lootFactor/2))*lootTablePreProcRewardEvent.getProcMultiplier().process(descriptiveLootTableEntry.getChance()))
                Bukkit.dispatchCommand(consoleCommandSender,descriptiveLootTableEntry.getCommand().replace("%player%",player
                        .getName()));
        });
//
//        getRewardCommands(player, path).forEach((o2,o1) -> {
//            if (Utils.RNG(0,100) >= 100-
//                    (1+lootFactor/(30+lootFactor/2))*lootTablePreProcRewardEvent.getProcMultiplier().process(o2)) Bukkit.dispatchCommand(consoleCommandSender,o1);
//        });
    }

    public void execute(Player player,RewardReason rewardReason,iRewardPath rewardPath,double lootFactor) {
        if (rewardPath != null)
            execute(player,rewardReason,rewardPath.getPath(),lootFactor);

    }

    public void setRewardPath(String path, LootTableEntry.DescriptiveLootTableEntry... lootTableEntries) {
        ConfigurationSection configurationSection = getorAddConfigurationSection("rewards");
        if (path != null) {
            configurationSection.set(path, lootTableEntries == null || lootTableEntries.length == 0 ? null : Arrays.stream(lootTableEntries).map(lootTableEntry ->
                    lootTableEntry.getChance() + "::" + lootTableEntry.getCommand() + "::"
                            + lootTableEntry.getDescriptor() + "::" + lootTableEntry.getMaterial().name()).collect(Collectors.toList()));
            save();
        }
    }

    public void setRewardPath(iRewardPath rewardPath, LootTableEntry.DescriptiveLootTableEntry...
                              lootTableEntries) {
        if (rewardPath != null)
            setRewardPath(rewardPath.getPath(),lootTableEntries);
    }

    public static void rewardPlayer(Player player,String path,double lootFactor) {
        rewardPlayer(player,RewardReason.DEFAULT,path,lootFactor);
    }

    public static void rewardPlayer(Player player,RewardReason rewardReason,String path,
                                    double lootFactor) {
        Instance.execute(player,rewardReason,path,lootFactor);
    }


    public interface iRewardPath {

        String getPath();

    }
    
    public enum RewardReason {

        ADMIN,
        DEFAULT
        ;

    }

    public static class LootTableEntry {

        private String command;
        private double chance;

        public LootTableEntry(String command, double chance) {
            this.command = command;
            this.chance = chance;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        public void setChance(double chance) {
            this.chance = chance;
        }

        public String getCommand() {
            return command;
        }

        public double getChance() {
            return chance;
        }

        public static class DescriptiveLootTableEntry extends LootTableEntry {

            private String descriptor;
            private Material material;

            public DescriptiveLootTableEntry(String command, double chance,Material material,String descriptor) {
                super(command, chance);
                setDescriptor(descriptor);
                setMaterial(material);
            }

            public String getDescriptor() {
                return descriptor;
            }

            public void setDescriptor(String descriptor) {
                this.descriptor = Utils.color(descriptor != null ? descriptor : "&eNo Review Display");
            }

            public Material getMaterial() {
                return material;
            }

            public void setMaterial(Material material) {
                this.material = material != null ? material : Material.BARRIER;
            }
        }

    }
}
