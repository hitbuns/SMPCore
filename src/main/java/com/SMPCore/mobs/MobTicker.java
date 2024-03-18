package com.SMPCore.mobs;

import com.MenuAPI.BukkitEventCaller;
import com.SMPCore.Events.TickedSMPEvent;
import com.SMPCore.Main;
import com.SMPCore.Utilities.ParticleUtils;
import com.SoundAnimation.SoundAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class MobTicker implements Runnable{

    BukkitTask bukkitTask;
    final List<LivingEntity> regen = new ArrayList<>();
    TickedSMPEvent tickedSMPEvent = new TickedSMPEvent();
    public static MobTicker Instance;

    public MobTicker() {
        Instance = this;
        bukkitTask = Bukkit.getScheduler().runTaskTimerAsynchronously(Main.Instance,this,1L,1L);
    }

    int counter;

    @Override
    public void run() {
        if (++counter >= 15*20) {

            regen.removeIf(livingEntity -> {

                if (livingEntity == null || livingEntity.isDead() || livingEntity.getHealth() <= 0) return true;

                double max = livingEntity.getMaxHealth();

                livingEntity.setHealth(Math.min(max,livingEntity.getHealth()+
                        max*0.05));
                SoundAPI.playSound(livingEntity,"regen_heal");

                ParticleUtils.makeCircle(location -> Main.Instance.getParticleNativeAPI().LIST_1_13.HEART.packet(true,location,1),livingEntity.getLocation(),15,50,2.5,Bukkit.getOnlinePlayers()
                        .toArray(Player[]::new));

                return false;
            });
            counter = 0;
        }


        Bukkit.getScheduler().runTask(Main.Instance,() ->
                BukkitEventCaller.callEvent(tickedSMPEvent));

    }



}
