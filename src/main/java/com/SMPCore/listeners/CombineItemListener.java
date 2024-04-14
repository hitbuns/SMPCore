package com.SMPCore.listeners;

import com.MenuAPI.Utils;
import com.SMPCore.skills.impl.AbilityIntentionType;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class CombineItemListener implements Listener {

    @EventHandler (ignoreCancelled = true,priority = EventPriority.MONITOR)
    public void onCraft(CraftItemEvent craftItemEvent) {

        if (craftItemEvent.getWhoClicked() instanceof Player player) {

            ItemStack itemStack = craftItemEvent.getRecipe().getResult();
            NBTItem nbtItem = new NBTItem(itemStack);
            nbtItem.setInteger("combinePower",1);


        }

    }

    public static ItemStack combineResult(ItemStack itemStack1,ItemStack itemStack2) {
        Map<Enchantment,Integer> enchants1 = itemStack1.getEnchantments(),enchants2 = itemStack2.getEnchantments();
        enchants2.forEach((enchantment, integer) -> enchants1.put(enchantment,Math.max(enchants1.getOrDefault(enchantment,0),integer)));

        NBTItem nbtItem1 = new NBTItem(itemStack1),nbtItem2 = new NBTItem(itemStack2);
        int v = (nbtItem1.hasKey("combinePower") ? nbtItem1
                .getInteger("combinePower") : 1)+(nbtItem2.hasKey("combinePower") ? nbtItem2
                .getInteger("combinePower") : 1);
        nbtItem1.setInteger("combinePower",v);

        ItemStack itemStack = nbtItem1.getItem();

        ItemMeta meta = itemStack.getItemMeta();
        Rarity rarity = Rarity.getRarity(v);

        AbilityIntentionType abilityIntentionType = EventListener.getAbilityIntentionType(itemStack);

        meta.setLore(Utils.color(Arrays.asList("&8&m============================",
                "&7  "+switch (abilityIntentionType) {
                    case MINING -> "Mining Power %: &a";
                    default -> "";
                },
                "",
                "  &7Rarity: "+switch (rarity) {
                    case COMMON -> "&7Common";
                    case UNCOMMON -> "&eUncommon";
                    case RARE -> "&aRare";
                    case EPIC -> "&5Epic";
                    case LEGENDARY -> "&cLegendary";
                    case MYTHIC -> "&dMythic";
                    case GODLY -> "&bGodly";
                    case UNHOLY -> "&4Unholy";
                },
                "  &7Progress: &8["+Utils.bar(),
                "&8&m                            "
                )));

        itemStack.addUnsafeEnchantments(enchants1);

        return itemStack;
    }

//    public static ItemStack update(ItemStack itemStack) {
//        Map<Enchantment,Integer> map = itemStack.getEnchantments();
//    }


    public enum Rarity {

        COMMON(2,2),
        UNCOMMON(4,6),
        RARE(7,13),
        EPIC(18,31),
        LEGENDARY(50,81),
        MYTHIC(125,206),
        GODLY(250,456),
        UNHOLY(-1,456)
        ;

        Rarity(int combinePowerRequired,int cumilitive) {
            this.combinePowerRequired = combinePowerRequired;
            this.cumilitive = cumilitive;
        }

        public final int combinePowerRequired,cumilitive;

        private static final TreeMap<Integer,Rarity> rarity = new TreeMap<>();

        public static Rarity getRarity(int combinePowerRequired) {
            return rarity.get(rarity.floorKey(combinePowerRequired));
        }

        static {
            for (Rarity value : Rarity.values()) {
                rarity.put(value.cumilitive,value);
            }
        }


    }


}
