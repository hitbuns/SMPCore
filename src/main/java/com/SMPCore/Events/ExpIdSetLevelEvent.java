package com.SMPCore.Events;

import com.SMPCore.skills.PlayerDataHandler;
import org.bukkit.OfflinePlayer;

public class ExpIdSetLevelEvent extends ExpIdEvent {

    private final int level;

    public ExpIdSetLevelEvent(OfflinePlayer offlinePlayer, PlayerDataHandler.ExpId statType,
                              int level) {
        super(offlinePlayer, statType);

        this.level = level;

    }

    public int getLevel() {
        return level;
    }
}
