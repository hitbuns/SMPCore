package com.SMPCore.skills.perks.DEFENSE;

import com.SMPCore.skills.PlayerDataHandler;
import com.SMPCore.skills.SkillPerk;
import com.SMPCore.skills.impl.CombatStatType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;

public class BonePlatingPerk extends SkillPerk {

    public BonePlatingPerk() {
        super(CombatStatType.DEFENSE, offlinePlayer -> PlayerDataHandler.getLevel(offlinePlayer,
                CombatStatType.DEFENSE) >= 10 ? null : "&4[SkillsAPI] &eYou are required to be at least Lvl. &a10 Defense to use this perk!");
    }

    @Override
    public void onEvent(Event event, LivingEntity livingEntity) {


    }

    @Override
    public String getDisplayName() {
        return "&2Bone &aPlating";
    }
}
