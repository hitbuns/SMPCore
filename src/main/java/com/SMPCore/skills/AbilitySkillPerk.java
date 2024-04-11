package com.SMPCore.skills;

import com.SMPCore.Events.DropTriggerEvent;
import com.SMPCore.Utilities.MessageResponsePredicate;
import com.SMPCore.mobs.MobType;
import com.SMPCore.skills.impl.AbilityIntentionType;
import org.bukkit.OfflinePlayer;

import java.util.function.Predicate;

public abstract class AbilitySkillPerk extends SkillPerk implements MobType.EventListener<DropTriggerEvent> {

    public final AbilityIntentionType[] abilityIntentionTypes;

    public AbilitySkillPerk(PlayerDataHandler.ExpId expId, MessageResponsePredicate<OfflinePlayer> playerPredicate, AbilityIntentionType... abilityIntentionTypes) {
        super(expId,playerPredicate);
        this.abilityIntentionTypes = abilityIntentionTypes != null ? abilityIntentionTypes : new AbilityIntentionType[0];
    }



}
