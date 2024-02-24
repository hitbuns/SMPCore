package com.SMPCore.commands;

import com.MenuAPI.ItemAdder;
import com.MenuAPI.Utilities.DescriptionBuilder;
import com.MenuAPI.Utilities.ItemBuilder;
import com.MenuAPI.Utils;
import com.SMPCore.Utilities.WorldGuardAPI;
import com.SMPCore.gui.RegionFlagEditor;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.session.BukkitSessionManager;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.domains.PlayerDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import de.tr7zw.nbtapi.NBTItem;
import joptsimple.internal.Strings;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CmdClaim implements CommandExecutor, TabCompleter {

    ItemStack itemStack;

    public CmdClaim() {
        NBTItem nbtItem = new NBTItem(new ItemBuilder(Material.STICK)
                .setDisplayName("&7>| &aClaim Tool &7|<")
                .setLore(DescriptionBuilder.init()
                        .addLore("&eLeft-click &7to select position &a1&7 of claim area",
                                "&eRight-click &7to select position &a2&7 of claim area",
                                "&7Run the command &a'/protectarea claim' &7when you are ready",
                                "&7to claim the area!").build())
                .build(false));
        nbtItem.setBoolean("claimTool",true);
        itemStack = nbtItem.getItem();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player player)) return true;

        if (strings.length < 1) {
            commandSender.sendMessage(Utils.color("&cUsage: /"+command.getName()+" <"+ Strings.join(args1,",")+">"));
            return true;
        }

        switch (strings[0].toLowerCase()) {
            case "wand" -> {
                if (player.getInventory().contains(itemStack)) {
                    player.sendMessage(Utils.color("&4[!] &eYou already have a claim tool in your inventory!"));
                    return true;
                }
                ItemAdder.addItem(player,itemStack.clone());
            }
            case "claim" -> {

                LocalSession localSession = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(player));
                World world = BukkitAdapter.adapt(player.getWorld());


                if (!(localSession.getRegionSelector(world
                ) instanceof CuboidRegionSelector cuboidRegionSelector && cuboidRegionSelector.isDefined())) {
                    player.sendMessage(Utils.color("&4[!] &eYou must select a region using the claim tool before using this command!"));
                    return true;
                }

                RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
                RegionManager regionManager = regionContainer.get(world);

                if (regionManager == null) {
                    try {
                        throw new Exception("Region Manager for world = '"+player.getWorld().getName()+"' is null");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                CuboidRegion cuboidRegion = cuboidRegionSelector.getIncompleteRegion();

                long volume = cuboidRegion.getVolume(),total = regionManager.getRegions().values().stream().filter(protectedRegion -> protectedRegion.getOwners()
                        .contains(player.getUniqueId())).mapToLong(ProtectedRegion::volume).sum() + volume;

                System.out.println("VOLUME>> "+volume+":: TOTAL>> "+total);

                if (total >= 500000) {
                    player.sendMessage(Utils.color("&4[!] &eYou cannot claim this region as it would exceed 500000 block volume"));
                    return true;
                }

                ProtectedCuboidRegion protectedCuboidRegion =  new ProtectedCuboidRegion("protectarea_"+player
                        .getUniqueId()+"_"+ UUID.randomUUID()+"_"+Utils.RNG_INT(0,1000),cuboidRegion.getPos1(),cuboidRegion.getPos2());
                List<ProtectedRegion> list = List.of(protectedCuboidRegion);


                if (regionManager.getRegions().entrySet().stream()
                        .anyMatch(stringProtectedRegionEntry -> !(stringProtectedRegionEntry.getKey().equalsIgnoreCase("__global__"))
                        && !stringProtectedRegionEntry.getValue().getIntersectingRegions(list).isEmpty())) {
                    player.sendMessage(Utils.color("&4[!] &eYou cannot claim this area as there is already an existing admin region or protect area in the selected region"));
                    return true;
                }

                DefaultDomain defaultDomain = new DefaultDomain();
                PlayerDomain playerDomain = new PlayerDomain();
                playerDomain.addPlayer(player.getUniqueId());
                defaultDomain.setPlayerDomain(playerDomain);
                protectedCuboidRegion.setOwners(defaultDomain);
                regionManager.addRegion(protectedCuboidRegion);

                
                try {
                    regionManager.saveChanges();
                } catch (StorageException e) {
                    throw new RuntimeException(e);
                }

                player.sendMessage(Utils.color("&a&lSUCCESS &7You have successfully claimed the selected region as your area. Use &a'/protectarea <menu/info>'&7 to edit flags and access to user management."));


            }
            case "info" -> WorldGuardAPI.getRegionsInLocation(player.getLocation(), protectedRegion -> protectedRegion.getId()
                    .startsWith("protectarea_")).stream().findFirst().ifPresentOrElse(protectedRegion -> new RegionFlagEditor(player,!protectedRegion.getOwners()
                            .contains(player.getUniqueId()),protectedRegion,null).open(player),() -> player.sendMessage(Utils.color("&4[!] &cThere are no protectarea claim in this area!")));
            case "menu" -> {



            }
            case "help" -> {

            }
            default -> commandSender.sendMessage(Utils.color("&cUsage: /"+command.getName()+" <"+ Strings.join(args1,",")+">"));
        }

        return true;
    }


    List<String> args1 = Arrays.asList("wand","claim","info","menu","help");


    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return strings.length == 1 ? args1.stream().filter(s1 -> s1.startsWith(strings[strings.length-1].toLowerCase()))
                .collect(Collectors.toList()) : null;
    }
}
