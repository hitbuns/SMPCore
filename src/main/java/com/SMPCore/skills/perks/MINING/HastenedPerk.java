package com.SMPCore.skills.perks.MINING;

import com.MenuAPI.Utils;
import com.SMPCore.Events.DropTriggerEvent;
import com.SMPCore.Utilities.TempEntityDataHandler;
import com.SMPCore.skills.AbilitySkillPerk;
import com.SMPCore.skills.PlayerDataHandler;
import com.SMPCore.skills.impl.NonCombatStatType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public class HastenedPerk extends AbilitySkillPerk {

    public HastenedPerk() {
        super(NonCombatStatType.MINING, offlinePlayer -> PlayerDataHandler.getLevel(offlinePlayer,
                NonCombatStatType.MINING) >= 10);
    }

    @Override
    public void onSpawn(DropTriggerEvent event, LivingEntity livingEntity, int grade) {

        ItemStack itemStack = event.getItemStack();
        if (Utils.isNullorAir(itemStack) || !itemStack.getType().name().contains("PICKAXE")) return;

        Player player = event.getPlayer();

        if (!playerPredicate.test(player)) {
            player.sendMessage(Utils.color("&4[!] &eYou must be at least &aLvl. 10 Mining&e before you can use this!"));
            return;
        }

        TempEntityDataHandler.EntityData entityData = TempEntityDataHandler.getorAdd(player);

        if (entityData.playerCooldownHandler.isOnCoolDown("hastened_perk", TimeUnit.SECONDS, 15)) {

            // IN PROGRESS

        }

    }

    @Override
    public void onEvent(Event event, LivingEntity livingEntity) {

    }


}
