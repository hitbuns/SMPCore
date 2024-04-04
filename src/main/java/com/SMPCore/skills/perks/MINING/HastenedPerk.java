package com.SMPCore.skills.perks.MINING;

import com.MenuAPI.Utils;
import com.SMPCore.Events.DropTriggerEvent;
import com.SMPCore.Utilities.TempEntityDataHandler;
import com.SMPCore.skills.AbilitySkillPerk;
import com.SMPCore.skills.PlayerDataHandler;
import com.SMPCore.skills.impl.NonCombatStatType;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.EntityEffect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

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

            player.addPotionEffect(PotionEffectType.FAST_DIGGING.createEffect(Math.floorDiv(PlayerDataHandler.getLevel(player,
                    NonCombatStatType.MINING),30),300));
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,TextComponent.fromLegacyText(Utils.color("&a** HASTENED **")));
            player.playEffect(EntityEffect.FIREWORK_EXPLODE);

        }

    }

    @Override
    public void onEvent(Event event, LivingEntity livingEntity) {

    }


}
