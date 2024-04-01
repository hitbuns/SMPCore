package com.SMPCore.Utilities;

import org.bukkit.block.Block;

public class SoundPlayerUtils {

    public static void playBlockSound(Block block) {
        try {
            block.getWorld().playSound(block.getLocation(), block.getBlockData().getSoundGroup().getBreakSound(), 1, 1);
        } catch (Exception ignored) {
        }
    }
}