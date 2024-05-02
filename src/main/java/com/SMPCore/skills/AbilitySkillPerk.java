package com.SMPCore.skills;

import com.SMPCore.Utilities.MessageResponsePredicate;
import com.SMPCore.skills.impl.AbilityIntentionType;
import org.bukkit.OfflinePlayer;

public abstract class AbilitySkillPerk extends SkillPerk implements iAbilityListener {

    public final AbilityIntentionType[] abilityIntentionTypes;

    public AbilitySkillPerk(PlayerDataHandler.ExpId expId, MessageResponsePredicate<OfflinePlayer> playerPredicate, AbilityIntentionType... abilityIntentionTypes) {
        super(expId,playerPredicate);
        this.abilityIntentionTypes = abilityIntentionTypes != null ? abilityIntentionTypes : new AbilityIntentionType[0];
    }




}
