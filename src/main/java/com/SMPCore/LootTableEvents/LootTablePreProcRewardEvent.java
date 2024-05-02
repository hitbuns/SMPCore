package com.SMPCore.LootTableEvents;

import com.SMPCore.LootTableSystem.RewardsAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class LootTablePreProcRewardEvent extends LootTableEvent implements Cancellable {

    private boolean cancel = false;
    ProcMultiplier procMultiplier;
    final Player player;
    final RewardsAPI.RewardReason rewardReason;

    public LootTablePreProcRewardEvent(Player player, String rewardPath, RewardsAPI.RewardReason rewardReason, ProcMultiplier procMultiplier) {
        super(rewardPath);
        setProcMultiplier(procMultiplier);
        this.player = player;
        this.rewardReason = rewardReason;
    }

    public LootTablePreProcRewardEvent(Player player, String rewardPath, RewardsAPI.RewardReason rewardReason, double multiplier) {
        this(player,rewardPath,rewardReason,a -> a*multiplier);
    }

    public Player getPlayer() {
        return player;
    }

    public RewardsAPI.RewardReason getRewardReason() {
        return rewardReason;
    }

    public ProcMultiplier getProcMultiplier() {
        return procMultiplier;
    }

    public void setProcMultiplier(ProcMultiplier procMultiplier) {
        this.procMultiplier = procMultiplier != null ?  procMultiplier : a -> a;
    }

    public void setProcMultiplier(double multiplier) {
        setProcMultiplier(a -> a*multiplier);
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
