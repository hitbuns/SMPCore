package com.SMPCore.listeners;

import com.MenuAPI.Utils;
import com.SMPCore.skills.impl.AbilityIntentionType;
import de.tr7zw.nbtapi.NBTItem;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class CombineItemListener implements Listener {

    @EventHandler (ignoreCancelled = true,priority = EventPriority.MONITOR)
    public void onCraft(CraftItemEvent craftItemEvent) {

        ItemStack itemStack = craftItemEvent.getRecipe().getResult();
        AbilityIntentionType abilityIntentionType = EventListener.getAbilityIntentionType(itemStack);
        System.out.println("CRAFT>> 1_"+(abilityIntentionType != null ? abilityIntentionType.name() : "NO RESULT"));

        if (abilityIntentionType != null) {
            System.out.println("CRAFT>> 2");
            craftItemEvent.setCurrentItem(updateItem(itemStack,craftItemEvent.getWhoClicked() instanceof  Player player ?
                    player : null));
        }

    }


    @EventHandler (ignoreCancelled = true,priority = EventPriority.MONITOR)
    public void onCombine(InventoryClickEvent inventoryClickEvent) {

        System.out.println("COMBINE>> 1");

        ItemStack current = inventoryClickEvent.getCurrentItem(),cursor = inventoryClickEvent.getCursor();

        if (Utils.isNullorAir(current) || Utils.isNullorAir(cursor)) return;

        System.out.println("COMBINE>> 2");

        AbilityIntentionType abilityIntentionType = EventListener.getAbilityIntentionType(current);

        System.out.println("COMBINE>> 2A "+(abilityIntentionType != null ? abilityIntentionType.name() : "NO RESULT"));
        if (abilityIntentionType != null && current.getType() == cursor.getType() && abilityIntentionType ==
        EventListener.getAbilityIntentionType(cursor)) {

            System.out.println("COMBINE>> 3");
            inventoryClickEvent.setCurrentItem(combineResult(current,cursor,inventoryClickEvent
                    .getWhoClicked() instanceof Player player ? player : null));
            inventoryClickEvent.setCursor(null);

        }

    }

    public static ItemStack updateItem(ItemStack itemStack,Player crafter) {

        if (Utils.isNullorAir(itemStack)) return null;

        NBTItem nbtItem = new NBTItem( itemStack);
        int v = nbtItem.hasKey("combinePower") ? nbtItem.getInteger("combinePower") : 1;
        Rarity rarity = Rarity.getRarity(v);
        double a = 100+rarity.priority*10D;

        ItemMeta meta = itemStack.getItemMeta();

        AbilityIntentionType abilityIntentionType = EventListener.getAbilityIntentionType(itemStack);

        meta.setLore(Utils.color(Arrays.asList("&8&m============================",
                "&7  "+switch (abilityIntentionType) {
                    case MINING -> "Mining Speed: "+a+"%";
                    case AXE -> "Axe Power: &a"+a+"%";
                    case SWORD -> "Sweep Radius: &a"+a+"%";
                    case FARMING -> "Double Crop Chance: &a"+(a-100)+"%";
                    case FISHING -> "Loot Bonus: &a"+a+"%";
                    case RANGED_COMBAT -> "Pierce Chance: &a"+((a-100)*0.5)+"%";
                    default -> "";
                },
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
                "  &7Progress: &8["+Utils.bar((float) Rarity.progress(v))+"&8] "+(rarity == Rarity.UNHOLY ? "&a&lMAX" : "&e"+rarity.getCombinePowerInRarity(v)+"/"+
                        rarity.combinePowerRequired),
                PlaceholderAPI.setPlaceholders(crafter,"  &7&lCrafted by: "+(crafter != null ? "%katsu_player_"+crafter.getName()+"% "+crafter.getName() : "Server")),
                "&8&m                            "
        )));

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack combineResult(ItemStack itemStack1,ItemStack itemStack2,Player crafter) {
        Map<Enchantment,Integer> enchants1 = itemStack1.getEnchantments(),enchants2 = itemStack2.getEnchantments();
        enchants2.forEach((enchantment, integer) -> enchants1.put(enchantment,Math.max(enchants1.getOrDefault(enchantment,0),integer)));

        NBTItem nbtItem1 = new NBTItem(itemStack1),nbtItem2 = new NBTItem(itemStack2);
        int v = (nbtItem1.hasKey("combinePower") ? nbtItem1
                .getInteger("combinePower") : 1)+(nbtItem2.hasKey("combinePower") ? nbtItem2
                .getInteger("combinePower") : 1);
        Rarity rarity = Rarity.getRarity(v);
        double a = 100+rarity.priority*10D;
        nbtItem1.setInteger("combinePower",v);
        nbtItem1.setDouble("power",a);


        ItemStack itemStack = updateItem(nbtItem1.getItem(),crafter);

        itemStack.addUnsafeEnchantments(enchants1);

        return itemStack;
    }

//    public static ItemStack update(ItemStack itemStack) {
//        Map<Enchantment,Integer> map = itemStack.getEnchantments();
//    }


    public enum Rarity {

        COMMON(1,2,3),
        UNCOMMON(2,4,7),
        RARE(3,7,14),
        EPIC(4,18,32),
        LEGENDARY(5,50,82),
        MYTHIC(6,125,207),
        GODLY(7,250,457),
        UNHOLY(8,-1,457)
        ;

        Rarity(int priority,int combinePowerRequired,int cumilitive) {
            this.combinePowerRequired = combinePowerRequired;
            this.cumilitive = cumilitive;
            this.priority = priority;
        }

        public static final Rarity[] ordered = Arrays.stream(Rarity.values()).sorted(Comparator.comparingInt(x -> x.priority)).toArray(Rarity[]::new);

        public final int combinePowerRequired,cumilitive,priority;

        private static final TreeMap<Integer,Rarity> rarity = new TreeMap<>();

        public static Rarity getRarity(int combinePowerRequired) {

            if (combinePowerRequired == 457) return  Rarity.UNHOLY;

            int v = rarity.floorKey(combinePowerRequired);
            return v == combinePowerRequired ? next(rarity.get(v)) : rarity.get(v);
        }

        static {
            for (Rarity value : Rarity.values()) {
                rarity.put(value.cumilitive,value);
            }
        }

        public static Rarity next(Rarity rarity) {
            return switch (rarity) {
                case COMMON -> UNCOMMON;
                case UNCOMMON -> RARE;
                case RARE -> EPIC;
                case EPIC -> LEGENDARY;
                case LEGENDARY -> MYTHIC;
                case MYTHIC -> GODLY;
                case GODLY, UNHOLY -> UNHOLY;
            };
        }

        public double getCombinePowerInRarity(int combinePower) {

            if (this == UNHOLY) return -1;

            return this.cumilitive-combinePower;

        }

        public static double progress(int combinePower) {
            Rarity rarity1 = getRarity(combinePower);

            if (rarity1 == Rarity.UNHOLY) return 1;

            double a = rarity1.getCombinePowerInRarity(combinePower);
            return a/ (double) rarity1.combinePowerRequired;
        }


    }


}
