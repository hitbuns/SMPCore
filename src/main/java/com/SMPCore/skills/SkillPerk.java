package com.SMPCore.skills;

import com.SMPCore.Utilities.MessageResponsePredicate;
import com.SMPCore.mobs.MobType;
import org.bukkit.OfflinePlayer;

import java.util.function.Predicate;

public abstract class SkillPerk implements MobType.EventHook {

    public final PlayerDataHandler.ExpId expId;
    public final MessageResponsePredicate<OfflinePlayer> playerPredicate;

    public SkillPerk(PlayerDataHandler.ExpId expId, MessageResponsePredicate<OfflinePlayer> playerPredicate) {
        this.expId = expId;
        this.playerPredicate = playerPredicate != null ? playerPredicate : offlinePlayer -> null;
    }

    public abstract String getDisplayName();

}
