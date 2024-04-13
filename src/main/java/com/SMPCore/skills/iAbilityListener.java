package com.SMPCore.skills;

import com.SMPCore.Events.DropTriggerEvent;
import org.bukkit.entity.Player;

public interface iAbilityListener {
        
        void onAbilityActivate(DropTriggerEvent dropTriggerEvent, Player player, boolean primary);
        
    }