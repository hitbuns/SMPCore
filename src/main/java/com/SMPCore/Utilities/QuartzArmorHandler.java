package com.SMPCore.Utilities;

import com.MenuAPI.ArmorType;
import com.MenuAPI.Utilities.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class QuartzArmorHandler {

    public static QuartzArmorHandler Instance;
    private final Map<EquipmentSlot, ItemStack> itemStackMap = new HashMap<>();
    public final ItemStack sword,axe;

    public QuartzArmorHandler(JavaPlugin javaPlugin) {

        Instance = this;

        for (ArmorType value : ArmorType.values()) {

            EquipmentSlot equipmentSlot = value.equipmentSlot;

            ItemStack itemStack = new ItemBuilder(Material.valueOf("LEATHER_"+
                    value.name())).setDisplayName("&b&oQuartz "+value.name().charAt(0)+value.name().toLowerCase()
                    .substring(1)).addModifiers(ItemBuilder.AttributeBuilder.init()
                    .addModifier(Attribute.GENERIC_ARMOR, AttributeModifier.Operation.ADD_NUMBER,
                            switch (value) {
                                case HELMET,BOOTS -> 4;
                                case CHESTPLATE -> 8;
                                case LEGGINGS -> 6;
                            },equipmentSlot)
                            .addModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, AttributeModifier.Operation.ADD_NUMBER,
                                    switch (value) {
                                        case HELMET,BOOTS -> 1.5;
                                        case CHESTPLATE -> 4;
                                        case LEGGINGS -> 3;
                                    },equipmentSlot)
                            .addModifier(Attribute.GENERIC_ATTACK_DAMAGE, AttributeModifier.Operation.MULTIPLY_SCALAR_1,
                                    0.05,equipmentSlot)
                            .addModifier(Attribute.GENERIC_MAX_HEALTH, AttributeModifier.Operation.ADD_NUMBER,
                                    switch (value) {
                                        case HELMET,BOOTS -> 2;
                                        case CHESTPLATE -> 5;
                                        case LEGGINGS -> 4;
                                    },equipmentSlot)
                            .build())
                    .customModelData(12925)
                    .color(137,117,162)
                    .build(false);

            javaPlugin.getServer().addRecipe(new ShapedRecipe(NamespacedKey
                    .minecraft("quartz_"+value.name().toLowerCase()),itemStack)
                    .shape(value.recipeShape).setIngredient('x',Material.QUARTZ));

            itemStackMap.put(equipmentSlot,itemStack);
        }

        javaPlugin.getServer().addRecipe(new ShapedRecipe(NamespacedKey
                .minecraft("quartz_broadsword"),sword = new ItemBuilder(Material.IRON_SWORD).setDisplayName("&b&oQuartz BroadSword").addModifiers(ItemBuilder.AttributeBuilder.init()
                        .addModifier(Attribute.GENERIC_ATTACK_DAMAGE, AttributeModifier.Operation.ADD_NUMBER,
                                9,EquipmentSlot.HAND)
                        .addModifier(Attribute.GENERIC_ATTACK_SPEED, AttributeModifier.Operation.MULTIPLY_SCALAR_1,
                                -0.375,EquipmentSlot.HAND)
                        .addModifier(Attribute.GENERIC_MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_SCALAR_1,
                                0.04,EquipmentSlot.HAND)
                        .addModifier(Attribute.GENERIC_ATTACK_SPEED, AttributeModifier.Operation.MULTIPLY_SCALAR_1,
                                0.2,EquipmentSlot.OFF_HAND)
                        .build()).customModelData(12925)
                .build(false))
                .shape("oxo","oxo","o?o").setIngredient('x',Material.QUARTZ)
                .setIngredient('?',Material.BLAZE_ROD));

        javaPlugin.getServer().addRecipe(new ShapedRecipe(NamespacedKey
                .minecraft("quartz_axe"),axe = new ItemBuilder(Material.IRON_AXE).setDisplayName("&b&oQuartz Waraxe"
                        ).addModifiers(ItemBuilder.AttributeBuilder.init()
                        .addModifier(Attribute.GENERIC_ATTACK_DAMAGE, AttributeModifier.Operation.ADD_NUMBER,
                                9,EquipmentSlot.HAND)
                        .addModifier(Attribute.GENERIC_ATTACK_SPEED, AttributeModifier.Operation.MULTIPLY_SCALAR_1,
                                -0.375,EquipmentSlot.HAND)
                        .addModifier(Attribute.GENERIC_MOVEMENT_SPEED, AttributeModifier.Operation.MULTIPLY_SCALAR_1,
                                0.04,EquipmentSlot.HAND)
                        .addModifier(Attribute.GENERIC_ATTACK_DAMAGE, AttributeModifier.Operation.MULTIPLY_SCALAR_1,
                                0.05,EquipmentSlot.OFF_HAND)
                        .build())
                        .customModelData(12925)
                .build(false))
                .shape("oxx","o?x","o?o").setIngredient('x',Material.QUARTZ)
                .setIngredient('?',Material.BLAZE_ROD));

    }


}
