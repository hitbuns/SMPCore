package com.SMPCore.Utilities;

import com.github.fierioziy.particlenativeapi.api.particle.type.ParticleType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.concurrent.TimeUnit;

public class ProjectileTracker extends ExecutorLimitTask {

        private final Entity projectile;
        ParticleType particleType;
        ParticleUtils.ParticleTypeModifier particleTypeModifier;


        public ProjectileTracker(Entity
                                  projectile,ParticleType particleType,double bulletSpread) {
            super(TimeUnit.MICROSECONDS,0,Math.max(1,Math.round(
                    (1 -  bulletSpread / (bulletSpread + 12)) * 5000)),1200+
                    Math.round(bulletSpread/5)*500);

            this.projectile = projectile;
            this.particleType = particleType;

        }

        public ProjectileTracker(Entity projectile, ParticleUtils.ParticleTypeModifier particleTypeModifier,
                                 double bulletSpread) {
            super(TimeUnit.MICROSECONDS,0,Math.max(1,Math.round(
                    (1 -  bulletSpread / (bulletSpread + 12)) * 5000)),1200+
                    Math.round(bulletSpread/5)*500);
            this.projectile = projectile;
            this.particleTypeModifier = particleTypeModifier;
        }

        @Override
        public void run() {
            super.run();


            if (projectile == null || projectile.isDead() || projectile.isOnGround()) {
                setCurrentCount(
                        getCountMax()
                );
                return;
            }

            (particleType != null ? particleType.packet(true, projectile.getLocation(),
                    1) : particleTypeModifier
                    .a(projectile.getLocation())).sendInRadiusTo(Bukkit
                            .getOnlinePlayers(), 150);


        }
    }