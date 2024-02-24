package com.SMPCore.Waypoints;

import com.MenuAPI.Config;
import com.MenuAPI.Utilities.DescriptionBuilder;
import com.MenuAPI.Utilities.ItemBuilder;
import com.MenuAPI.Utils;
import com.mongodb.lang.NonNull;
import joptsimple.internal.Strings;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class WaypointConfig extends Config {


    /*

    waypoints:
        id:
            itemStack: %itemStack%
            material: %material%
            displayName: "&9Showers"
            location: %location%
     */



    private static WaypointConfig Instance;

    public WaypointConfig(JavaPlugin javaPlugin) {
        super(javaPlugin, javaPlugin.getDataFolder(),"waypoints.yml");

        Instance = this;

        ConfigurationSection configurationSection = getorAddConfigurationSection("waypoints");
        wayPointRegistry = configurationSection.getKeys(false).stream()
                .map(s1 -> {
                    try {
                        ConfigurationSection abc = getorAddConfigurationSection(configurationSection,
                                s1);
                        if (abc.contains("itemStack"))
                            return new WayPointInfo(s1,abc.getString("displayName"),
                                abc.getLocation("location"),abc.getItemStack("itemStack"));
                        else return new WayPointInfo(s1,abc.getString("displayName"),
                                abc.getLocation("location"),abc.contains("material") ?
                                Material.valueOf(abc.getString("material").toUpperCase()) : null);
                    } catch (Exception exception) {
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toMap(WayPointInfo::getId,
                        wayPointInfo -> wayPointInfo));

    }


    public static WaypointConfig getInstance() {
        return Instance;
    }

    private final Map<String,WayPointInfo> wayPointRegistry;

    public Map<String, WayPointInfo> getWayPointRegistry() {
        return wayPointRegistry;
    }

    public void register(WayPointInfo wayPointInfo,boolean override) {
        if (wayPointInfo != null && (override || !wayPointRegistry
                .containsKey(wayPointInfo.getId()))) {
            ConfigurationSection configurationSection = getorAddConfigurationSection("waypoints"),
                    configurationSection1 = getorAddConfigurationSection(configurationSection,
                            wayPointInfo.getId());

            configurationSection1.set("location",wayPointInfo.getLocation());
            configurationSection1.set("displayName",wayPointInfo.getDisplayName());
            configurationSection1.set("itemStack",wayPointInfo.getDisplayIcon());
            if (!Utils.isNullorAir(wayPointInfo.getDisplayIcon()))
                configurationSection.set("material",wayPointInfo.getDisplayIcon().getType()
                        .name());

            save();

            wayPointRegistry.put(wayPointInfo.id,wayPointInfo);

        }
    }

    public void deregister(String id) {
        if (id != null) {
            getorAddConfigurationSection("waypoints").set(id, null);
            save();
            wayPointRegistry.remove(id);
        }
    }

    public static class WayPointInfo {

        private Location location;
        @NonNull
        private final String id;
        private String displayName;
        private ItemStack displayIcon;

        public WayPointInfo(@NonNull String id,String displayName,Location
                            location,ItemStack itemStack) {
            this.location = location;
            this.displayName = Utils.color(displayName);
            this.id = id;
            this.displayIcon = Utils.isNullorAir(itemStack) ? new ItemBuilder(Material.ENDER_EYE)
                    .setDisplayName(displayName != null ? displayName :
                            ("&e"+Strings.join(Arrays.stream(id.split("_"))
                                    .map(s -> s.toUpperCase().charAt(0)+
                                            s.substring(1).toLowerCase()).toArray(String[]::new),
                                    " ")))
                    .setLore(DescriptionBuilder.init()
                            .addLore("&7&oClick to set waypoint to target").build())
                    .build(false) :
            itemStack.clone();
        }

        public WayPointInfo(@NonNull String id, String displayName, Location location,
                            Material material) {
            this(id,displayName,location,material != null ? new ItemBuilder(material)
                    .setDisplayName(displayName != null ? displayName :
                            ("&e"+Strings.join(Arrays.stream(id.split("_"))
                                            .map(s -> s.toUpperCase().charAt(0)+
                                                    s.substring(1).toLowerCase()).toArray(String[]::new),
                                    " ")))
                    .setLore(DescriptionBuilder.init()
                            .addLore("&7&oClick to set waypoint to target").build())
                    .build(false) : null);
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
            updateChangeToConfig();
        }

        public ItemStack getDisplayIcon() {
            return Utils.isNullorAir(displayIcon) ? null : displayIcon.clone();
        }

        public void setDisplayIcon(ItemStack displayIcon) {
            this.displayIcon = displayIcon;
            updateChangeToConfig();
        }

        public void setDisplayIcon(Material material) {
            setDisplayIcon(material != null ? new ItemBuilder(material)
                    .setDisplayName(displayName)
                    .setLore(DescriptionBuilder.init()
                            .addLore("&7&oClick to set waypoint to target").build())
                    .build(false) : null);
            updateChangeToConfig();
        }

        @NonNull
        public String getId() {
            return id;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            if (!Utils.isNullorAir(this.displayIcon)) {
                this.displayName = Utils.color(displayName);
                setDisplayIcon(this.displayIcon.getType());
            }
        }

        void updateChangeToConfig() {
            WaypointConfig.getInstance().register(this,true);
        }

    }


}
