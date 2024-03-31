package com.SMPCore.skills;

import com.SMPCore.Events.DropTriggerEvent;
import com.SMPCore.mobs.MobType;
import org.bukkit.OfflinePlayer;

import java.util.function.Predicate;

public abstract class AbilitySkillPerk extends SkillPerk implements MobType.EventListener<DropTriggerEvent> {

    public AbilitySkillPerk(PlayerDataHandler.ExpId expId, Predicate<OfflinePlayer> playerPredicate) {
        super(expId, playerPredicate);
    }



}
