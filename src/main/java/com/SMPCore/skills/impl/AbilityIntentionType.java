package com.SMPCore.skills.impl;

import com.SMPCore.skills.PlayerDataHandler;

public enum AbilityIntentionType {

    MINING(NonCombatStatType.MINING),
    FARMING(NonCombatStatType.FARMING),
    SWORD(CombatStatType.STRENGTH),
    RANGED_COMBAT(CombatStatType.DEXTERITY),
    DEFENSE_PASSIVE(CombatStatType.DEFENSE),
    ENCHANTING(NonCombatStatType.ENCHANTING),
    AXE(CombatStatType.STRENGTH,NonCombatStatType.WOODCUTTING)


    ;

    AbilityIntentionType(PlayerDataHandler.ExpId... expIds) {
        this.expIds =expIds;
    }

    public final PlayerDataHandler.ExpId[] expIds;

}
