package com.SMPCore.LootTableSystem;

import com.MenuAPI.GUISystem.AbstractValueChangerMenu;
import com.MenuAPI.GUISystem.DoubleChangeRunnable;
import com.MenuAPI.GUISystem.ValueChangeRunnable;
import com.MenuAPI.GUISystem.ValueGrabRunnable;
import org.bukkit.entity.Player;

public class LootTableChanceModifier extends AbstractValueChangerMenu {

    public LootTableChanceModifier(Player player, LootTableEntryModifier lootTableEntryModifier) {
        super(player, "Loot Table Entry Modifier", lootTableEntryModifier,
                new ChangeModifierType<Double>() {

            final Double[] values = {-5D,-1D,-0.1D,0.1D,1D,5D};

                    @Override
                    public Double[] getTypeValues() {
                        return values;
                    }

                    @Override
                    public String getDisplayId() {
                        return "Loot Table Chance";
                    }

                    @Override
                    public String getUnits() {
                        return "%";
                    }

                    @Override
                    public int getTypeValue() {
                        return 0;
                    }

                    @Override
                    public Double getMin() {
                        return 0D;
                    }

                    @Override
                    public Double getMax() {
                        return 100D;
                    }

                    @Override
                    public ValueChangeRunnable<Double> getChangeRunnable() {
                        return (DoubleChangeRunnable) (s, i, aDouble) ->  {
                            double b = aDouble*100;
                            b = Math.round(b)/100.0;
                            lootTableEntryModifier.descriptiveLootTableEntry.setChance(
                                    Math.max(0,Math.min(b,100))
                            );
                        };
                    }

                    @Override
                    public ValueGrabRunnable<Double> getGrabRunnable() {
                        return (s, i) -> lootTableEntryModifier.descriptiveLootTableEntry.getChance();
                    }
                });
    }

    @Override
    public int getGUIId() {
        return 0;
    }
}
