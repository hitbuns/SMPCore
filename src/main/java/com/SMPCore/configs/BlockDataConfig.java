package com.SMPCore.configs;

import com.MenuAPI.Config;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class BlockDataConfig extends Config {

    public static BlockDataConfig Instance;


    public static void init(JavaPlugin javaPlugin) {
        Instance = new BlockDataConfig(javaPlugin);
    }

    BlockDataConfig(JavaPlugin javaPlugin) {
        super(javaPlugin, javaPlugin.getDataFolder(), "block-data(DO-NOT-EDIT).yml");
    }


    public void saveBlock(Block block) {
        if (block != null) {
            update(encoding(block),true);
        }
    }

    public void removeBlock(Block block) {
        if (block != null) {
            update(encoding(block),null);
        }
    }

    public boolean isBlockEncoded(Block block) {
        return block != null && getBoolean(encoding(block),false);
    }

    static String encoding(Block block) {
        return encoding(block != null ? block.getLocation() : null);
    }

    static String encoding(Location location) {
        return location != null ?  Base64Coder.encodeString(location.getWorld().getName()+"_"+location.getBlockX()+"_"+
                location.getBlockY()+"_"+location.getBlockZ()) : null;
    }

}
