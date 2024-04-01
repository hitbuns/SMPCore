package com.SMPCore.mining;

import com.MenuAPI.Config;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;

public class BlockConfig extends Config {

    public static BlockConfig Instance;

    public BlockConfig(JavaPlugin javaPlugin) {
        super(javaPlugin, javaPlugin.getDataFolder(), "block-dura.yml");

        ConfigurationSection configurationSection = getorAddConfigurationSection("block-dura");

        Arrays.stream(Material.values()).filter(Material::isBlock)
                .forEach(material -> {

                    if (!configurationSection.contains(material.name())) {
                        create(material,false,false);
                    }

                });

        save();

    }

    void create(Material material,int timeToBreak,boolean axe,boolean pickaxe,boolean shovel,boolean save,boolean enabled) {
        ConfigurationSection configurationSection = getorAddConfigurationSection("block-dura"),
                configurationSection1 = getorAddConfigurationSection(configurationSection,material
                .name());
        configurationSection1.set("enabled",enabled);
        configurationSection1.set("timeToBreak",Math.max(0,timeToBreak));
        configurationSection1.set("shovel",shovel);
        configurationSection1.set("axe",axe);
        configurationSection1.set("pickaxe",pickaxe);
        if (save) save();
    }

    void create(Material material,boolean save,boolean enabled) {
        create(material,100,false,false,false,save,enabled);
    }

    public boolean shovel(Material material) {
        try {
            return material != null && getorAddConfigurationSection("block-dura").getConfigurationSection(material.name())
                    .getBoolean("shovel");
        } catch (Exception exception) {
            create(material,true,false);
            return false;
        }
    }

    public boolean axe(Material material) {
        try {
            return material != null && getorAddConfigurationSection("block-dura").getConfigurationSection(material.name())
                    .getBoolean("shovel");
        } catch (Exception exception) {
            create(material,true,false);
            return false;
        }
    }

    public boolean pickaxe(Material material) {
        try {
            return material != null && getorAddConfigurationSection("block-dura").getConfigurationSection(material.name())
                    .getBoolean("shovel");
        } catch (Exception exception) {
            create(material,true,false);
            return false;
        }
    }

    public boolean enabled(Material material) {
        try {
            return material != null && getorAddConfigurationSection("block-dura").getConfigurationSection(material.name())
                    .getBoolean("enabled");
        } catch (Exception exception) {
            create(material,true,false);
            return false;
        }
    }

    public int timeToBreak(Material material) {
        try {
            return material != null ? getorAddConfigurationSection("block-dura").getConfigurationSection(material.name())
                    .getInt("timeToBreak") : 100;
        } catch (Exception exception) {
            create(material,true,false);
            return 100;
        }
    }

    public boolean shovel(Block material) {
        return shovel(material != null ? material.getType() : null);
    }


    public boolean axe(Block material) {
        return axe(material != null ? material.getType() : null);
    }

    public boolean pickaxe(Block material) {
        return pickaxe(material != null ? material.getType() : null);
    }

    public int timeToBreak(Block material) {
        return timeToBreak(material != null ? material.getType() : null);
    }

    public boolean enabled(Block material) {
        return enabled(material != null ? material.getType() : null);
    }


    public static BlockConfig init(JavaPlugin javaPlugin) {
        return Instance = new BlockConfig(javaPlugin);
    }

}
