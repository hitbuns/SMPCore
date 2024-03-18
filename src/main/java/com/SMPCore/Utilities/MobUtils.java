package com.SMPCore.Utilities;

import com.MenuAPI.Utilities.FormattedNumber;
import com.MenuAPI.Utils;
import com.SMPCore.Main;
import joptsimple.internal.Strings;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.LazyMetadataValue;

import java.util.Arrays;

public class MobUtils {

    public static void updateEntity(LivingEntity livingEntity) {
        updateEntity(livingEntity,livingEntity.getHealth());
    }

    public static void updateEntity(LivingEntity livingEntity,double healthValue) {

        if (livingEntity == null) return;

        String s1 = (livingEntity.hasMetadata("displayName") ? livingEntity.getMetadata("displayName").get(0).asString() :
                Utils.color("&e"+
                        Strings.join(Arrays.stream(livingEntity.getType().name().split("_"))
                                .map(s -> s.charAt(0)+s.toLowerCase().substring(1))
                                .toArray(String[]::new)," ")));

        if (!livingEntity.hasMetadata("displayName")) {
            livingEntity.setMetadata("displayName",new LazyMetadataValue(Main.Instance,
                    LazyMetadataValue.CacheStrategy.NEVER_CACHE,()-> s1));
        }

        double health = Math.max(0,healthValue);

        livingEntity.setCustomNameVisible(true);
        livingEntity.setCustomName(Utils.color(s1+" &f"+ FormattedNumber.getInstance().getCommaFormattedNumber(health,1)+" &c✙"));

    }


    static String[] colors =  new String[] {"&7","&b","&9","&c","&6","&4"};

    public static String getGrade(int grade) {
        return (grade < 1 || grade > 6) ? "&8[&fUnk.&8]" : "&8["+colors[grade-1]+"☆&8]";
    }

}
