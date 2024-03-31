package com.SMPCore.Events;

import com.SMPCore.skills.PlayerDataHandler;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;

public class ExpIdLevelUpEvent extends ExpIdEvent implements Cancellable {

    private final int previousLevel,newLevel;
    private final double expRemaining;
    private boolean cancel = false;

    public ExpIdLevelUpEvent(OfflinePlayer offlinePlayer, PlayerDataHandler.ExpId statType,
                             int previousLevel, int newLevel, double expRemaining) {
        super(offlinePlayer,statType);
        this.previousLevel = previousLevel;
        this.newLevel = newLevel;
        this.expRemaining = expRemaining;
    }

    public int getLevelUpAmount() {
        return getNewLevel()-getPreviousLevel();
    }

    public int getPreviousLevel() {
        return previousLevel;
    }

    public int getNewLevel() {
        return newLevel;
    }

    public double getExpRemaining() {
        return expRemaining;
    }
    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean b) {
        cancel = b;
    }
}
