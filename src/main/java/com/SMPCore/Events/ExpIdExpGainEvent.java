package com.SMPCore.Events;


import com.SMPCore.skills.PlayerDataHandler;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;

public class ExpIdExpGainEvent extends ExpIdEvent implements Cancellable {

    private double expAmount;
    private boolean cancel = false;

    public ExpIdExpGainEvent(OfflinePlayer offlinePlayer, PlayerDataHandler.ExpId statType,
                             double expAmount) {
        super(offlinePlayer, statType);

        this.expAmount = expAmount;

    }

    public ExpIdExpGainEvent divide(double divider) {
        try {
            expAmount /= Math.max(0,divider);
        } catch (Exception ignored) {}
        return this;
    }

    public ExpIdExpGainEvent multiply(double multiplier) {
        expAmount *= Math.max(0,multiplier);
        return this;
    }

    public ExpIdExpGainEvent add(double value) {
        this.expAmount += Math.max(0,value);
        return this;
    }

    public ExpIdExpGainEvent subtract(double value) {
        this.expAmount = Math.max(0,this.expAmount-value);
        return this;
    }

    public double getExpAmount() {
        return expAmount;
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
