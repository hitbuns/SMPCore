package com.SMPCore.mobs;

import com.MenuAPI.Utilities.ItemBuilder;
import com.MenuAPI.Utils;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EquipmentHandler {

    public static Map<EquipmentSlot, ItemStack> randomEquipment(double yLevel,Map<EquipmentSlot,Double> equipmentOdds
            ,boolean isRanged) {
        Map<EquipmentSlot,ItemStack> equipment = new HashMap<>();
        EquipmentSlot[] equipmentSlots = EquipmentSlot.values();
        for (EquipmentSlot equipmentSlot : equipmentSlots) {
            double equipmentOdd = equipmentOdds.getOrDefault(equipmentSlot,-1D), rngFactor = equipmentOdd
            < 0 ? -1 : (yLevel >= 70 ? 10 : 10+(70-yLevel)*1.5)*(equipmentOdd/(equipmentOdd+25));
            int enchantLevel = Utils.RNG_INT(-1-5*(1-rngFactor/(rngFactor+20)),1+4*
                    rngFactor/(rngFactor+30));
            if (equipmentSlot != EquipmentSlot.HAND && equipmentSlot != EquipmentSlot.OFF_HAND) {

                if (Utils.RNG(0,100) >= 100-rngFactor) {

                    Material material = randomRecurse(Intention.ARMOR,null,rngFactor);

                    if (material != null) {

                        ItemBuilder itemBuilder = new ItemBuilder(org.bukkit.Material.valueOf(material
                                .name()+"_"+switch (equipmentSlot) {

                            case CHEST -> "CHESTPLATE";
                            case LEGS -> "LEGGINGS";
                            case FEET -> "BOOTS";
                            default -> "HELMET";

                        }));

                        if (enchantLevel >= 1) itemBuilder.enchant(Enchantment.PROTECTION_ENVIRONMENTAL,
                                enchantLevel);

                        equipment.put(equipmentSlot, itemBuilder.build(false));
                    }

                }

                continue;
            }

            if (Utils.RNG(0,100) >= 100-rngFactor) {

                if (equipmentSlot == EquipmentSlot.OFF_HAND) {
                    equipment.put(EquipmentSlot.OFF_HAND,
                            new ItemBuilder(org.bukkit.Material.SHIELD)
                                    .build(false));
                    continue;
                }


                if (isRanged) {

                    ItemBuilder itemBuilder = new ItemBuilder(Utils.RNG_INT(0,100) >= 50 ?
                            org.bukkit.Material.BOW : org.bukkit.Material.CROSSBOW);


                    if (enchantLevel >= 1) itemBuilder.enchant(itemBuilder.getItemStack()
                                    .getType() == org.bukkit.Material.BOW ? Enchantment.ARROW_DAMAGE :
                            Enchantment.PIERCING,
                            enchantLevel);

                    equipment.put(equipmentSlot,itemBuilder.build(false));

                    continue;

                }

                Material material = randomRecurse(Intention.TOOL,null,rngFactor);

                if (material != null) {

                    ItemBuilder itemBuilder = new ItemBuilder(org.bukkit.Material.valueOf(material
                            .name()+"_"+(Utils.RNG(0,100) >= 50 ? "SWORD" : "AXE")));

                    if (enchantLevel >= 1) itemBuilder.enchant(Enchantment.DAMAGE_ALL,
                            enchantLevel);

                    ItemStack itemStack = itemBuilder.build(false);

                    if (Utils.RNG(0,85) >= rngFactor) {
                        ItemMeta itemMeta = itemStack.getItemMeta();

                        itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED,
                                new AttributeModifier("generic_attack_speed_"+
                                        UUID.randomUUID()+"_"+Utils.RNG_INT(0,1000),rngFactor/100, AttributeModifier.Operation.MULTIPLY_SCALAR_1));

                        itemStack.setItemMeta(itemMeta);

                    }


                    equipment.put(equipmentSlot, itemStack);
                }

            }

        }
        return equipment;
    }

    public static Material randomRecurse(Intention intention,Material material,double rngFactor) {
         if (Utils.RNG_INT(0,100-2*(material != null ? material.priority :0)) >= 90-rngFactor) {
             Material[] materials = intention == Intention.TOOL ? Material.allTools : Material.allArmor;
             if (material == null) return randomRecurse(intention,materials[0],rngFactor);
             for (Material material1 : materials) {
                 if (material1.priority > material.priority) return randomRecurse(intention,
                         material1, rngFactor);
             }
         }
         return material;
    }

    public enum Material {

        LEATHER(1,Intention.ARMOR),
        WOODEN(1,Intention.TOOL),
        STONE(2,Intention.TOOL),
        GOLDEN(3,Intention.TOOL,Intention.ARMOR),
        CHAINMAIL(4,Intention.ARMOR),
        IRON(5,Intention.ARMOR,Intention.TOOL),
        DIAMOND(6,Intention.ARMOR,Intention.TOOL),
        NETHERITE(7,Intention.ARMOR,Intention.TOOL)

        ;

        public static final Material[] allTools = Arrays.stream(Material.values())
                .filter(material -> Arrays.stream(material.intentions)
                        .anyMatch(intention -> intention == Intention.TOOL))
                .sorted((o1, o2) -> Integer.compare(o2.priority,o1.priority)).toArray(Material[]::new),allArmor
                = Arrays.stream(Material.values())
                .filter(material -> Arrays.stream(material.intentions)
                        .anyMatch(intention -> intention == Intention.ARMOR))
                .sorted((o1, o2) -> Integer.compare(o2.priority,o1.priority)).toArray(Material[]::new);
        final Intention[] intentions;
        final int priority;

        Material(int priority,Intention... intentions) {
            this.priority = priority;
            this.intentions = intentions != null ? intentions : new Intention[0];
        }

    }

    public enum Intention {

        ARMOR,
        TOOL
        ;

    }

}
