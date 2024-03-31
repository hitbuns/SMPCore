package com.SMPCore.Events;

import com.SMPCore.skills.PlayerDataHandler;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;

public class ExpIdExpRemoveEvent extends ExpIdEvent implements Cancellable {

    private final double subtractedAmount;
    private boolean cancel = false;

    public ExpIdExpRemoveEvent(OfflinePlayer offlinePlayer, PlayerDataHandler.ExpId statType,
                               double subtractedAmount) {
        super(offlinePlayer, statType);

        this.subtractedAmount = Math.max(0,subtractedAmount);
    }

    public double getSubtractedAmount() {
        return subtractedAmount;
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
