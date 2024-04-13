package com.SMPCore.Utilities;

import com.SMPCore.Main;
import com.github.fierioziy.particlenativeapi.api.packet.ParticlePacket;
import com.github.fierioziy.particlenativeapi.api.particle.type.ParticleType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ParticleUtils {

    public static void makeCylinder(Location location, ParticleType particleType
            , int particleAmount, int stack, double radius, double ySeperation, double angleBetweenPoints, boolean elevate, double seeRadius, Player... players) {

        Location a = location.clone();

        for (int i = 0; i < stack; i++) {

            try {

                a.add(0,elevate ? ySeperation : -ySeperation,0);
                makeCircle(particleType,a,particleAmount,angleBetweenPoints,seeRadius,radius,players);

            } catch (Exception ignored) {}

        }
    }

    public static void makeCylinder(Location location,ParticleTypeModifier particleType
            ,int stack,double radius,double ySeperation,double angleBetweenPoints,boolean elevate,double seeRadius,Player... players) {

        Location a = location.clone();

        for (int i = 0; i < stack; i++) {

            try {

                a.add(0,elevate ? ySeperation : -ySeperation,0);
                makeCircle(particleType,a,angleBetweenPoints,seeRadius,radius,players);

            } catch (Exception ignored) {}

        }
    }

    public static Block[] blocksInCircleRadius2D(Location player, double radius) {

        Location center = player.clone();

        List<Block> blocks = new ArrayList<>();
        for (double x = -radius; x < radius; x++) {

            for (double z = -radius; z < radius; z++) {

                Location location = center.clone().add(x,0,z);

                double a = Math.abs(location.getX()),b = Math.abs(location.getZ());
                double centerX = Math.abs(center.getX()), centerZ = Math.abs(center.getZ());
                double adjustedX = Math.abs(a-centerX), adjustedZ = Math.abs(b-centerZ);
                double angle = 1/Math.sin(adjustedX/adjustedZ);
                double peakX = Math.abs(Math.sin(angle)*radius), peakZ = Math.abs(Math.cos(angle)*radius);

                if (Math.sqrt(Math.pow(peakX,2)+Math.pow(peakZ,2)) >= Math.sqrt(Math.pow(adjustedX,2) + Math.pow(adjustedZ,2)) ||
                        (location.getBlockX() == player.getBlockX() &&
                                (location.getBlockZ() <= centerZ + radius &&
                                        location.getBlockZ() >= centerZ - radius))
                        || (location.getBlockZ() == player.getBlockZ() &&
                        (location.getBlockX() <= player.getBlockX() + radius &&
                                location.getBlockX() >= player.getBlockX() - radius))) {
                    blocks.add(location.getBlock());
                }

            }


        }

        return blocks.stream().filter(Objects::nonNull).toArray(Block[]::new);
    }

    public static Player[] playersInCircleRadius(Location player,double radius) {

        Location center = player.clone();

        return center.getWorld().getNearbyEntities(center,radius,radius,radius).stream().map(entity -> {
            try {
                return (Player) entity;
            } catch (Exception exception) {
                return null;
            }
        }).filter(Objects::nonNull).filter(player1 -> {

            Location location = player1.getLocation().clone();

            double x = Math.abs(location.getX()),z = Math.abs(location.getZ());
            double centerX = Math.abs(center.getX()), centerZ = Math.abs(center.getZ());
            double adjustedX = Math.abs(x-centerX), adjustedZ = Math.abs(z-centerZ);
            double angle = 1/Math.sin(adjustedX/adjustedZ);
            double peakX = Math.abs(Math.sin(angle)*radius), peakZ = Math.abs(Math.cos(angle)*radius);

            return Math.sqrt(Math.pow(peakX,2)+Math.pow(peakZ,2)) >= Math.sqrt(Math.pow(adjustedX,2) + Math.pow(adjustedZ,2));
        }).toArray(Player[]::new);
    }

    public static void spiralParticle(ParticleType particleType, Entity entity, boolean reverse, Predicate<Player> playerPredicate,
                                      Player... players) {
        spiralParticle(particleType,entity,reverse,-1,playerPredicate,players);
    }

    public static void spiralParticle(ParticleType particleType, Entity entity,boolean reverse,int range,
                                      Player... players) {
        spiralParticle(particleType, entity,reverse, range,null, players);
    }

    public static void spiralParticle(ParticleType particleType, Entity entity,boolean reverse, Player... players) {
        spiralParticle(particleType,entity,reverse,-1,null,players);
    }

    public static void spiralParticle(ParticleType particleType, Entity entity,boolean reverse,int range,Predicate<Player> playerPredicate,
                                      Player... players) {
        if (entity != null)
            spiralParticle(particleType,entity instanceof LivingEntity livingEntity ? livingEntity.getEyeLocation() :
                    entity.getLocation(),reverse,range,playerPredicate,players);
    }

    public static void spiralParticle(ParticleType particleType,Location location,boolean reverse,int range,Player... players) {
        spiralParticle(particleType,location,reverse,range,null,players);
    }

    public static void spiralParticle(ParticleType particleType,Location location,boolean reverse,Predicate<Player> playerPredicate,Player... players) {
        spiralParticle(particleType,location,reverse,-1,playerPredicate,players);
    }

    public static void spiralParticle(ParticleType particleType,Location location,boolean reverse,Player... players) {
        spiralParticle(particleType,location,reverse,-1,null,players);
    }

    public static void arc(ParticleTypeModifier particleType, Location location, Vector vector, double angleArc, double angleBetweenPoints, double seeRadius, double radius, Player... players) {
        Location original = location.clone();

        List<Player> players1 = Arrays.stream(players).filter(Objects::nonNull).collect(Collectors.toList());

        double loopFactor = 360/angleBetweenPoints;
        Bukkit.getScheduler().runTaskAsynchronously(Main.Instance,
                () -> {
                    //(radius/(radius+15))
                    //,angleFactor = 360/loopFactor;

                    double angle = 0;

                    for (int i = 0; i < loopFactor; i++) {
                        if (angle >= 360) angle = 0;

                        double b = Math.toRadians(angle);

                        Location location1 = original.clone();
                        location1.add(Math.sin(b)*radius,0,Math.cos(b)*radius);

                        angle += angleBetweenPoints;

                        ////System.out.println("ANGLE>> "+Math.toDegrees(vector.clone().setY(0).angle(original.toVector().subtract(location1.toVector()).setY(0))));
                        if (Math.toDegrees(vector.clone().multiply(-1).setY(0).angle(original.toVector().subtract(location1.toVector()).setY(0))) > angleArc/2) continue;

                        if (seeRadius > 0)
                            particleType.a(location1).sendInRadiusTo(players1,seeRadius);
                        else particleType.a(location1).sendTo(players1);
                    }
                });

    }

    public static void arc(ParticleType particleType,Location location,Vector vector,double angleArc,int particleAmount,double angleBetweenPoints,double seeRadius,double radius,Player... players) {
        Location original = location.clone();

        List<Player> players1 = Arrays.stream(players).filter(Objects::nonNull).collect(Collectors.toList());

        double loopFactor = 360/angleBetweenPoints;
        Bukkit.getScheduler().runTaskAsynchronously(Main.Instance,
                () -> {
                    //(radius/(radius+15))
                    //,angleFactor = 360/loopFactor;

                    double angle = 0;

                    for (int i = 0; i < loopFactor; i++) {
                        if (angle >= 360) angle = 0;

                        double b = Math.toRadians(angle);

                        Location location1 = original.clone();
                        location1.add(Math.sin(b)*radius,0,Math.cos(b)*radius);
                        angle += angleBetweenPoints;

                        ////System.out.println("ANGLE>> "+Math.toDegrees(vector.clone().setY(0).angle(original.toVector().subtract(location1.toVector()).setY(0))));
                        if (Math.toDegrees(vector.clone().setY(0).multiply(-1).angle(original.toVector().subtract(location1.toVector()).setY(0))) > angleArc/2) continue;

                        if (seeRadius > 0)
                            particleType.packet(true,location1,particleAmount).sendInRadiusTo(players1,seeRadius);
                        else particleType.packet(true,location1,particleAmount).sendTo(players1);
                    }
                });
    }

    public static void makeCircle(ParticleType particleType,Location location,int particleAmount,double angleBetweenPoints,double seeRadius,double radius,Player... players) {
        Location original = location.clone();

        List<Player> players1 = Arrays.stream(players).filter(Objects::nonNull).collect(Collectors.toList());

        double loopFactor = 360/angleBetweenPoints;
        Bukkit.getScheduler().runTaskAsynchronously(Main.Instance,
                () -> {
                    //(radius/(radius+15))
                    //,angleFactor = 360/loopFactor;

                    double angle = 0;

                    for (int i = 0; i < loopFactor; i++) {
                        if (angle >= 360) angle = 0;

                        double b = Math.toRadians(angle);

                        Location location1 = original.clone();
                        location1.add(Math.sin(b)*radius,0,Math.cos(b)*radius);
                        if (seeRadius > 0)
                            particleType.packet(true,location1,particleAmount).sendInRadiusTo(players1,seeRadius);
                        else particleType.packet(true,location1,particleAmount).sendTo(players1);

                        angle += angleBetweenPoints;
                    }
                });
    }

    public static void spiralParticle(ParticleType particleType,Location location,boolean reverse,int range,Predicate<Player> playerPredicate,Player... players) {
        Location original = location.clone();
        double radius = 0,angle = 0;

        for (int i = 0; i < 361; i++) {

            double b = Math.toRadians(angle),as = reverse ? -1 : 1;
            Location location1 = original.clone().add(as*Math.sin(b)*radius,-0.01*i,as*Math.cos(b)*radius);

            radius += 0.01;
            if (angle >= 360) angle = 0;
            angle += 1;

            if (range > 0) particleType.packet(true,location1).sendInRadiusTo(playerPredicate != null ?
                    Arrays.stream(players).filter(playerPredicate)
                            .collect(Collectors.toList()) : Arrays.stream(players).collect(Collectors.toList()) ,range);
            else particleType.packet(true,location1).sendTo(playerPredicate != null ?
                    Arrays.stream(players).filter(playerPredicate)
                            .collect(Collectors.toList()) : Arrays.stream(players).collect(Collectors.toList()));
        }
    }

    public static List<Player> getNearbyPlayers(Location location,double radius, Predicate<Player> predicate) {
        double t = Math.max(0.01,radius);
        return predicate != null ? location.getWorld().getNearbyEntities(location,t,t,t,entity -> entity instanceof Player)
                .stream().map(entity -> entity instanceof Player player ? player : null).filter(Objects::nonNull)
                .filter(predicate).collect(Collectors.toList()) : location.getWorld().getNearbyEntities(location,t,t,t,entity -> entity instanceof Player)
                .stream().map(entity -> entity instanceof Player player ? player : null).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static void spiralParticle(ParticleTypeModifier particleType, Entity entity,boolean reverse,Predicate<Player> playerPredicate,
                                      Player... players) {
        spiralParticle(particleType,entity,reverse,-1,playerPredicate,players);
    }

    public static void spiralParticle(ParticleTypeModifier particleType, Entity entity,boolean reverse,int range,
                                      Player... players) {
        spiralParticle(particleType, entity,reverse, range,null, players);
    }

    public static void spiralParticle(ParticleTypeModifier particleType, Entity entity,boolean reverse, Player... players) {
        spiralParticle(particleType,entity,reverse,-1,null,players);
    }

    public static void spiralParticle(ParticleTypeModifier particleType, Entity entity,boolean reverse,int range,Predicate<Player> playerPredicate,
                                      Player... players) {
        if (entity != null)
            spiralParticle(particleType,entity instanceof LivingEntity livingEntity ? livingEntity.getEyeLocation() :
                    entity.getLocation(),reverse,range,playerPredicate,players);
    }

    public static void spiralParticle(ParticleTypeModifier particleType,Location location,boolean reverse,int range,Player... players) {
        spiralParticle(particleType,location,reverse,range,null,players);
    }

    public static void spiralParticle(ParticleTypeModifier particleType,Location location,boolean reverse,Predicate<Player> playerPredicate,Player... players) {
        spiralParticle(particleType,location,reverse,-1,playerPredicate,players);
    }

    public static void spiralParticle(ParticleTypeModifier particleType,Location location,boolean reverse,Player... players) {
        spiralParticle(particleType,location,reverse,-1,null,players);
    }

    public enum Direction {

        FRONT("↑"),
        FRONT_RIGHT("↗"),
        RIGHT("→"),
        FRONT_LEFT("↖"),
        LEFT("←"),
        BACK_LEFT("↙"),
        BACK_RIGHT("↘"),
        BACK("↓")
        ;

        Direction(String value) {
            this.value = value;
        }

        final String value;

        public String getValue() {
            return value;
        }

        public static Direction getDirection(double angle) {
            return switch ((int) Math.round(angle / 45)) {
                case -5,3 -> FRONT_LEFT;
                case -6,2 -> LEFT;
                case -7,1 -> BACK_LEFT;
                case 0,8 -> BACK;
                case -1,7 -> BACK_RIGHT;
                case -2,6 -> RIGHT;
                case -3,5 -> FRONT_RIGHT;
                default -> FRONT;
            };
        }

        public static Direction getDirection(LivingEntity location,Location location1) {
            Vector directionToTarget = location.getLocation().toVector().subtract(location1.toVector())
                    .normalize().clone().setY(0),directionFacing = location.getEyeLocation().getDirection().clone()
                    .normalize()
                    .setY(0);

            double tanToTarget = Math.toDegrees(Math.atan2(directionToTarget.getX(),directionToTarget.getZ())),
                    tanFacing = Math.toDegrees(Math.atan2(directionFacing.getX(),directionFacing.getZ())),
                    tanRelativeToFacing = tanFacing-tanToTarget;

            return getDirection(tanRelativeToFacing);
        }

    }

    public static void makeCircle(ParticleTypeModifier particleType,Location location,double angleBetweenPoints,double seeRadius,double radius,Player... players) {
        Location original = location.clone();

        List<Player> players1 = Arrays.stream(players).filter(Objects::nonNull).collect(Collectors.toList());

        double loopFactor = 360/angleBetweenPoints;
        Bukkit.getScheduler().runTaskAsynchronously(Main.Instance,
                () -> {
                    //(radius/(radius+15))
                    //,angleFactor = 360/loopFactor;

                    double angle = 0;

                    for (int i = 0; i < loopFactor; i++) {
                        if (angle >= 360) angle = 0;

                        double b = Math.toRadians(angle);

                        Location location1 = original.clone();
                        location1.add(Math.sin(b)*radius,0,Math.cos(b)*radius);
                        if (seeRadius > 0)
                            particleType.a(location1).sendInRadiusTo(players1,seeRadius);
                        else particleType.a(location1).sendTo(players1);

                        angle += angleBetweenPoints;
                    }
                });

    }

    public static LivingEntity[] getEntitiesInAngle(LivingEntity livingEntity,double angle,double radius) {
        return getEntitiesInAngle(livingEntity.getLocation(),livingEntity.getEyeLocation()
                .getDirection(),angle,radius);
    }

    public static LivingEntity[] getEntitiesInAngle(Location location,Vector facingDirection,double angle,double radius) {
        Vector face = facingDirection.clone().setY(0).normalize();
        double ca = Math.max(0,angle);
        return location.getWorld().getNearbyEntities(location,radius,radius,radius)
                .stream().map(entity -> {
                    try {
                        return  (LivingEntity) entity;
                    } catch (Exception exception) {
                        return null;
                    }

                }).filter(player -> {

                    if (player == null || player.getLocation().distance(location) > radius) return false;

                    Vector v = location.toVector().subtract(player.getLocation().toVector()).setY(0).normalize();
                    return Math.toDegrees(face.multiply(-1).angle(v)) <= ca/2;
                }).toArray(LivingEntity[]::new);
    }

    public static void spiralParticle(ParticleTypeModifier particleType,Location location,boolean reverse,int range,Predicate<Player> playerPredicate,Player... players) {
        Location original = location.clone();
        double radius = 0,angle = 0;

        for (int i = 0; i < 361; i++) {

            double b = Math.toRadians(angle),as = reverse ? -1 : 1;
            Location location1 = original.clone().add(as*Math.sin(b)*radius,-0.01*i,as*Math.cos(b)*radius);

            radius += 0.01;
            if (angle >= 360) angle = 0;
            angle += 1;

            if (range > 0) particleType.a(location1).sendInRadiusTo(playerPredicate != null ?
                    Arrays.stream(players).filter(playerPredicate)
                            .collect(Collectors.toList()) : Arrays.stream(players).collect(Collectors.toList()) ,range);
            else particleType.a(location1).sendTo(playerPredicate != null ?
                    Arrays.stream(players).filter(playerPredicate)
                            .collect(Collectors.toList()) : Arrays.stream(players).collect(Collectors.toList()));
        }
    }

    public static void drawLine(Location pos1, Location pos2, ParticleTypeModifier particle,int particleAmount,Player... players) {
        if (pos1.getWorld() != pos2.getWorld()) {
            return;
        }
        Vector endLoc = pos2.toVector();
        pos1.setDirection(endLoc.subtract(pos1.toVector()));
        Vector increase = pos1.getDirection().multiply(0.25);
        int distance = (int) pos1.distance(pos2);
        double i;
        List<Player> players1 = Arrays.stream(players).filter(Objects::nonNull).collect(Collectors.toList());
        for (i = 0.0D; i < distance; i += 0.25) {
            Location loc = pos1.add(increase);
            particle.a(loc).sendTo(players1);
        }
    }

    public static void drawLine(Location pos1, Location pos2, ParticleType particle,int particleAmount,Player... players) {
        if (pos1.getWorld() != pos2.getWorld()) {
            return;
        }
        Vector endLoc = pos2.toVector();
        pos1.setDirection(endLoc.subtract(pos1.toVector()));
        Vector increase = pos1.getDirection().multiply(0.25);
        int distance = (int) pos1.distance(pos2);
        double i;
        List<Player> players1 = Arrays.stream(players).filter(Objects::nonNull).collect(Collectors.toList());
        for (i = 0.0D; i < distance; i += 0.25) {
            Location loc = pos1.add(increase);
            particle.packet(true,loc,Math.max(1,particleAmount)).sendTo(players1);
        }
    }

//    public static void drawLine(Location pos1, Location pos2, int particleAmount, Player... players) {
//        if (pos1.getWorld() != pos2.getWorld()) {
//            return;
//        }
//        Vector endLoc = pos2.toVector();
//        pos1.setDirection(endLoc.subtract(pos1.toVector()));
//        Vector increase = pos1.getDirection().multiply(0.25);
//        int distance = (int) pos1.distance(pos2);
//        double i;
//        List<Player> players1 = Arrays.stream(players).collect(Collectors.toList());
//        ParticleTypeDustColorTransition particleTypeDustColorTransition = StaminaListener.Instance.getParticleNativeAPI().LIST_1_13.DUST_COLOR_TRANSITION;
//        for (i = 0.0D; i < distance; i += 0.25) {
//            Location loc = pos1.add(increase);
//            particleTypeDustColorTransition
//                    .color(Color.fromRGB(83,145 ,176 ),Color.fromRGB(
//                            50,78,92),10 ).packet(true,loc).sendTo(players1);
//        }
//    }


    public static void drawSphere(Location center, ParticleType particle, double radiusInt,double degrees,int circleStack,Player... players) {
//        List<Player> players1 = Arrays.stream(players).collect(Collectors.toList());
//        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(),() -> {
        for (double i = 0; i <= Math.PI; i += Math.PI / (radiusInt * circleStack)) {
            double radius = Math.sin(i) * radiusInt;
            double y = Math.cos(i) * radiusInt;

            makeCircle(particle,center.clone().add(0,y,0),1,degrees,40,radius,players);

//                for (double b = 0; b < 360; b += degrees) {
//                    double a = Math.toRadians(b);
//                    double x = Math.cos(a) * radius;
//                    double z = Math.sin(a) * radius;
//                    center.add(x, y, z);
//                    particle.packet(true,center).sendTo(players1);
//                    center.subtract(x, y, z);
//                }

//            for (double a = 0; a < 6.283185307179586D; a += Math.PI / (radiusInt * circleStack)) {
//                double x = Math.cos(a) * radius;
//                double z = Math.sin(a) * radius;
//                center.add(x, y, z);
//                particle.packet(true,center).sendTo(players1);
//                center.subtract(x, y, z);
//            }
        }
//        });
    }



    public static void drawSphere(Location center, ParticleTypeModifier particle, double radiusInt,double degrees,int circleStack,Player... players) {
//        List<Player> players1 = Arrays.stream(players).collect(Collectors.toList());
//        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(),()-> {
        for (double i = 0; i <= Math.PI; i += Math.PI / (radiusInt * circleStack)) {
            double radius = Math.sin(i) * radiusInt;
            double y = Math.cos(i) * radiusInt;

            makeCircle(particle,center.clone().add(0,y,0),degrees,40,radius,players);

//                for (double b = 0; b < 360; b += degrees) {
//                    double a = Math.toRadians(b);
//                    double x = Math.cos(a) * radius;
//                    double z = Math.sin(a) * radius;
//                    center.add(x, y, z);
//                    particle.a(center).sendTo(players1);
//                    center.subtract(x, y, z);
//                }
//                for (double a = 0; a < 6.283185307179586D; a += Math.PI / (radiusInt * circleStack)) {
//                    double x = Math.cos(a) * radius;
//                    double z = Math.sin(a) * radius;
//                    center.add(x, y, z);
//                    particle.a(center).sendTo(players1);
//                    center.subtract(x, y, z);
//                }
        }
//        });
    }

    public interface ParticleTypeModifier {

        ParticlePacket a(Location location);

    }


}
