package com.SMPCore.LootTableSystem;

import com.MenuAPI.ChatMessageResponseSystem.ChatMessageResponderHandlerList;
import com.MenuAPI.GUISystem.*;
import com.MenuAPI.Utilities.ItemBuilder;
import com.MenuAPI.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MoneyEntryModifier extends AbstractValueChangerMenu implements ChatMessageResponderHandlerList.ChatMessageResponder {

    final LootTableEntryModifier lootTableEntryModifier;

    public MoneyEntryModifier(Player player, LootTableEntryModifier lootTableEntryModifier) {
        super(player, "Select a money reward", lootTableEntryModifier, new ChangeModifierType<Double>() {

            final Double[] values = {-10000D,-100D,-1D,1D,100D,10000D};
            double aDouble;

            @Override
            public Double[] getTypeValues() {
                return values;
            }

            @Override
            public String getDisplayId() {
                return "Money Reward";
            }

            @Override
            public String getUnits() {
                return "&6$";
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
                return 9999999999D;
            }

            @Override
            public ValueChangeRunnable<Double> getChangeRunnable() {
                return (DoubleChangeRunnable) (s, i, aDouble) -> {
                    this.aDouble = Math.max(getMin(),Math.min(aDouble,getMax()));
                    lootTableEntryModifier.descriptiveLootTableEntry.setCommand("eco give %player% "+this.aDouble);
                };
            }

            @Override
            public ValueGrabRunnable<Double> getGrabRunnable() {
                return (s, i) -> aDouble;
            }
        });

        this.lootTableEntryModifier = lootTableEntryModifier;

        registerPriority(new Button(4,guiClickEvent -> {
            close(getPlayer(),true);
            ChatMessageResponderHandlerList.getInstance().addMessageResponders(getPlayer(),this);
            getPlayer().sendMessage(Utils.color("&eType in an amount to reward the player! Type 'cancel' to cancel this process"));
        },false));

        getInventory().setItem(4,new ItemBuilder(Material.KNOWLEDGE_BOOK).setDisplayName("&eClick to set a custom value").build(false));
    }

    @Override
    public int getGUIId() {
        return 38;
    }

    @Override
    public boolean run(Player player, String s) {

        if (s.equalsIgnoreCase("cancel")) {
            ChatMessageResponderHandlerList.getInstance()
                    .setGUIOpenable(getPlayer(),true);
            open(getPlayer());
            return true;
        }

        try {

            lootTableEntryModifier.descriptiveLootTableEntry.setCommand("eco give %player% "+Math.max(Math.min(Double.parseDouble(s),999999999),0));
            ChatMessageResponderHandlerList.getInstance()
                    .setGUIOpenable(getPlayer(),true);
            openPage(lootTableEntryModifier);

        } catch (Exception exception) {
            getPlayer().sendMessage(Utils.color("&eType in an amount to reward the player! Type 'cancel' to cancel this process"));
        }


        return true;
    }

}
