package com.SMPCore.skills;

import com.SMPCore.mobs.MobType;
import org.bukkit.OfflinePlayer;

import java.util.function.Predicate;

public abstract class SkillPerk implements MobType.EventHook {

    public final PlayerDataHandler.ExpId expId;
    public final Predicate<OfflinePlayer> playerPredicate;

    public SkillPerk(PlayerDataHandler.ExpId expId, Predicate<OfflinePlayer> playerPredicate) {
        this.expId = expId;
        this.playerPredicate = playerPredicate != null ? playerPredicate : offlinePlayer -> true;
    }

}
