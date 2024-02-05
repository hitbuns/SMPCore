package com.SMPCore.Utilities;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WorldGuardAPI {

    private static WorldGuardAPI Instance;
    RegionContainer regionContainer;

    public WorldGuardAPI() {
        Instance = this;

        regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
    }

    public Set<ProtectedRegion> getRegions(Location location,boolean blackList,CheckType checkType,String... ids) {
        return getRegions(location,blackList ?
                protectedRegion -> Arrays.stream(ids).noneMatch(s -> switch (checkType) {
                    case CONTAINS -> protectedRegion.getId().contains(s);
                    case IGNORE_CASE -> protectedRegion.getId().equalsIgnoreCase(s);
                    default -> protectedRegion.getId().equals(s);
                }) :
                protectedRegion -> Arrays.stream(ids).anyMatch(s -> switch (checkType) {
                    case CONTAINS -> protectedRegion.getId().contains(s);
                    case IGNORE_CASE -> protectedRegion.getId().equalsIgnoreCase(s);
                    default -> protectedRegion.getId().equals(s);
                }));
    }


    public Set<ProtectedRegion> getRegions(Location location, Predicate<ProtectedRegion> predicate) {
        return regionContainer == null || location == null ? new HashSet<>() : predicate != null ?
                regionContainer.createQuery().getApplicableRegions(BukkitAdapter
        .adapt(location)).getRegions().stream().filter(predicate).collect(Collectors.toSet()) :
                regionContainer.createQuery().getApplicableRegions(BukkitAdapter
                        .adapt(location)).getRegions();
    }

    public static WorldGuardAPI getInstance() {
        return Instance;
    }

    public static Set<ProtectedRegion> getRegionsInLocation(Location location,boolean blackList,CheckType checkType,String... ids) {
        return Instance.getRegions(location, blackList,checkType, ids);
    }

    public static Set<ProtectedRegion> getRegionsInLocation(Location location, Predicate<ProtectedRegion> predicate) {
        return Instance.getRegions(location, predicate);
    }

    public enum CheckType {

        EQUALS,IGNORE_CASE,CONTAINS
        ;

    }

}

