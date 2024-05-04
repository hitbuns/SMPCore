package com.SMPCore.listeners;

import com.MenuAPI.ItemAdder;
import com.MenuAPI.Utilities.FormattedNumber;
import com.MenuAPI.Utils;
import com.SMPCore.Events.FarmHarvestEvent;
import com.SMPCore.LootTableSystem.RewardsAPI;
import com.SMPCore.Main;
import com.SMPCore.Utilities.ParticleUtils;
import com.SMPCore.configs.CraftExpConfig;
import com.SMPCore.mobs.EquipmentHandler;
import com.SMPCore.skills.ExpReason;
import com.SMPCore.skills.PlayerDataHandler;
import com.SMPCore.skills.impl.AbilityIntentionType;
import com.SMPCore.skills.impl.NonCombatStatType;
import com.SoundAnimation.SoundAPI;
import de.tr7zw.nbtapi.NBTItem;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.LazyMetadataValue;

import java.util.*;

public class CombineItemListener implements Listener {

    @EventHandler (priority = EventPriority.MONITOR,ignoreCancelled = true)
    public void onSwordSweep(EntityDamageByEntityEvent entityDamageByEntityEvent) {

        if (entityDamageByEntityEvent.getDamager() instanceof Player player) {

            ItemStack itemStack = player.getInventory().getItemInMainHand();

            if (EventListener.getAbilityIntentionType(itemStack) != AbilityIntentionType.SWORD) return;

            NBTItem nbtItem = new NBTItem(itemStack);
            double v  = nbtItem.getDouble("power")/100;

            double radius = 3 * (v);

            Player[] players = Bukkit.getOnlinePlayers().toArray(Player[]::new);

            ParticleUtils.arc(location -> Main.Instance.getParticleNativeAPI().LIST_1_13.DUST.color(Color.fromRGB(217, 169, 26),1)
                    .packet(true,location),player.getLocation().clone().add(0,0.4,0),player.getEyeLocation().getDirection(),45,3,50,radius/3,
                    players);

            ParticleUtils.arc(location -> Main.Instance.getParticleNativeAPI().LIST_1_13.DUST.color(Color.fromRGB(217, 185, 26),1)
                            .packet(true,location),player.getLocation().clone().add(0,0.6,0),player.getEyeLocation().getDirection(),45,3,50,radius*2/3,
                    players);

            ParticleUtils.arc(location -> Main.Instance.getParticleNativeAPI().LIST_1_13.DUST.color(Color.fromRGB(192, 217, 26),1)
                            .packet(true,location),player.getLocation().clone().add(0,0.8,0),player.getEyeLocation().getDirection(),45,3,50,radius,
                    players);

            LivingEntity[ ] livingEntities = ParticleUtils.getEntitiesInAngle(player, 45, radius);
            for (LivingEntity livingEntity : livingEntities) {
                double dist = livingEntity.getLocation().distance(player.getLocation());
                livingEntity.damage(entityDamageByEntityEvent.getDamage()*(1-dist/(dist+radius)));
            }

        }

    }


    @EventHandler
    public void onBowShoot(ProjectileLaunchEvent projectileLaunchEvent) {

        if (projectileLaunchEvent.getEntity().getShooter() instanceof Player player) {

            ItemStack itemStack = player.getInventory().getItemInMainHand();

            if (Utils.isNullorAir(itemStack) || EventListener.getAbilityIntentionType(itemStack) != AbilityIntentionType.RANGED_COMBAT) return;

            NBTItem nbtItem = new NBTItem(itemStack);
            if (nbtItem.hasKey("power")) projectileLaunchEvent.getEntity().setMetadata("pierce",new LazyMetadataValue(Main.Instance, LazyMetadataValue.CacheStrategy.NEVER_CACHE,
                    ()-> (nbtItem.getDouble("power")-100)*0.5));

        }

    }

    @EventHandler (priority = EventPriority.MONITOR,ignoreCancelled = true)
    public void onBowPierce(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        if (entityDamageByEntityEvent.getDamager() instanceof Projectile projectile && projectile.hasMetadata("pierce")) {

            if (Utils.RNG(0,100) < 100-projectile.getMetadata("pierce").get(0).asDouble()) return;

            entityDamageByEntityEvent.setDamage(Math.max((entityDamageByEntityEvent.getDamage() * .7 / entityDamageByEntityEvent.getFinalDamage()) * entityDamageByEntityEvent.getDamage(),
                    entityDamageByEntityEvent.getDamage()));
            SoundAPI.playSound(entityDamageByEntityEvent.getEntity(), "pierce_armor");
            if (Utils.getAttacker(entityDamageByEntityEvent.getDamager()) instanceof Player player) player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    TextComponent.fromLegacyText(Utils.color("&c&m   &c ** PIERCE ** &c&m   ")));
        }
    }

    @EventHandler
    public void onFarm(FarmHarvestEvent farmHarvestEvent) {

        ItemStack itemStack = farmHarvestEvent.getPlayer().getInventory().getItemInMainHand();
        if (EventListener.getAbilityIntentionType(itemStack) != AbilityIntentionType.FARMING) return;

        NBTItem nbtItem = new NBTItem(itemStack);
        double a = nbtItem.hasKey("power") ? Math.max(nbtItem.getDouble("power")-100,0) : 0;

        if (Utils.RNG(0,100) >= 100-a) {

            double v;
            try {
                v = switch (itemStack.getType().name().split("_")[0]) {
                    case "STONE" -> 2;
                    case "GOLDEN" -> 3;
                    case "IRON" -> 4;
                    case "DIAMOND" -> 5;
                    case "NETHERITE" -> 6;
                    default -> 1;
                };
            } catch (Exception exception) {
                v = 1;
            }

            farmHarvestEvent.amountMultiplier *= v;
            farmHarvestEvent.exp *= v;
        }

    }

    @EventHandler (ignoreCancelled = true,priority = EventPriority.MONITOR)
    public void onFish(PlayerFishEvent playerFishEvent) {

        Player player = playerFishEvent.getPlayer();

        AbilityIntentionType.allPerks.forEach((s, skillPerk) -> skillPerk.onEvent(playerFishEvent, player));

        if (playerFishEvent.getState() == PlayerFishEvent.State.CAUGHT_FISH) {

            ItemStack itemStack = player.getInventory().getItemInMainHand();
            if (EventListener.getAbilityIntentionType(itemStack) != AbilityIntentionType.FISHING) return;

            playerFishEvent.setCancelled(true);

            NBTItem nbtItem = new NBTItem(itemStack);
            double value = nbtItem.hasKey("power") ? nbtItem.getDouble("power")/100 : 0;

            RewardsAPI.rewardPlayer(player,"fishing",(1+PlayerDataHandler.getLevel(player,NonCombatStatType.FISHING)*0.5)*value);

        }

    }

    @EventHandler (ignoreCancelled = true,priority = EventPriority.MONITOR)
    public void onCraft(CraftItemEvent craftItemEvent) {

        ItemStack itemStack = craftItemEvent.getRecipe().getResult();
        AbilityIntentionType abilityIntentionType = EventListener.getAbilityIntentionType(itemStack);
        Player player = craftItemEvent.getWhoClicked() instanceof  Player player1 ?
                player1 : null;
        int getMaxCraftAmount = craftItemEvent.getClick().name().contains("SHIFT") ? getMaxCraftAmount(craftItemEvent.getInventory()) : 1;
        if (player != null) PlayerDataHandler.addExp(player, NonCombatStatType.CRAFTING, ExpReason.GRIND, CraftExpConfig.Instance.expGain(craftItemEvent.getRecipe())*
                getMaxCraftAmount/itemStack.getAmount());

        if (abilityIntentionType != null) {

            if (getMaxCraftAmount > 1) {

                craftItemEvent.setCancelled(true);
                for (int i = 0; i < getMaxCraftAmount; i++) {
                    ItemStack itemStack1 = updateItem(itemStack.clone(),player);
                    ItemAdder.addItem(player,itemStack1);
                }

                craftItemEvent.getInventory().setMatrix(Arrays.stream(craftItemEvent.getInventory().getMatrix()).map(itemStack1 -> {

                    if (itemStack1 == null) return null;

                    int v = itemStack1.getAmount()-getMaxCraftAmount;

                    if (v > 0) {
                        itemStack1.setAmount(v);
                        return itemStack1;
                    }

                    return null;

                }).toArray(ItemStack[]::new));

                return;
            }

            craftItemEvent.setCurrentItem(updateItem(itemStack,player));
        }

    }


    public static int getMaxCraftAmount(CraftingInventory inv) {
        if (inv.getResult() == null)
            return 0;

        int resultCount = inv.getResult().getAmount();
        int materialCount = Integer.MAX_VALUE;

        for (ItemStack is : inv.getMatrix())
            if (is != null && is.getAmount() < materialCount)
                materialCount = is.getAmount();

        return resultCount * materialCount;
    }


    @EventHandler (ignoreCancelled = true,priority = EventPriority.MONITOR)
    public void onCombine(InventoryClickEvent inventoryClickEvent) {

        ItemStack current = inventoryClickEvent.getCurrentItem(),cursor = inventoryClickEvent.getCursor();

        if (Utils.isNullorAir(current) || Utils.isNullorAir(cursor)) return;

        AbilityIntentionType abilityIntentionType = EventListener.getAbilityIntentionType(current);
        if (abilityIntentionType != null && current.getType() == cursor.getType() && abilityIntentionType ==
        EventListener.getAbilityIntentionType(cursor)) {
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
        nbtItem.setDouble("power",a);

        itemStack = nbtItem.getItem();

        ItemMeta meta = itemStack.getItemMeta();

        AbilityIntentionType abilityIntentionType = EventListener.getAbilityIntentionType(itemStack);
        float f = (float) Rarity.progress(v);

        if (abilityIntentionType == AbilityIntentionType.DEFENSE_PASSIVE) {

            if (meta.hasAttributeModifiers()) {
                meta.getAttributeModifiers(Attribute.GENERIC_MAX_HEALTH).stream().filter(attributeModifier -> attributeModifier.getName().contains("health_combine"))
                        .findFirst().ifPresent(attributeModifier -> meta.removeAttributeModifier(Attribute.GENERIC_MAX_HEALTH, attributeModifier));
                meta.getAttributeModifiers(Attribute.GENERIC_MOVEMENT_SPEED).stream().filter(attributeModifier -> attributeModifier.getName().contains("speed_combine"))
                        .findFirst().ifPresent(attributeModifier -> meta.removeAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, attributeModifier));
            }
            String material = itemStack.getType().name();

            EquipmentSlot equipmentSlot = material.contains("_HELMET") ? EquipmentSlot.HEAD : material.contains("_CHESTPLATE") ?
                    EquipmentSlot.CHEST : material.contains("_LEGGINGS") ? EquipmentSlot.LEGS : material.contains("_BOOTS") ? EquipmentSlot.FEET : EquipmentSlot.OFF_HAND;

            meta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH,new AttributeModifier(UUID.randomUUID(),"health_combine_"+UUID.randomUUID()+"_"+Utils.RNG_INT(0,1000),
                    ((a-100)*0.05), AttributeModifier.Operation.ADD_NUMBER,equipmentSlot));
            meta.addAttributeModifier(Attribute.GENERIC_MOVEMENT_SPEED,new AttributeModifier(UUID.randomUUID(),"speed_combine_"+UUID.randomUUID()+"_"+Utils.RNG_INT(0,1000),
                    ((a-100)*0.1)/100, AttributeModifier.Operation.MULTIPLY_SCALAR_1,equipmentSlot));

            itemStack.getType().getDefaultAttributeModifiers(equipmentSlot).entries().forEach(attributeAttributeModifierEntry -> {
                try {
                    meta.addAttributeModifier(attributeAttributeModifierEntry.getKey(), attributeAttributeModifierEntry.getValue());
                } catch (Exception ignored) {

                }
                    });

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        }


        List<String> lore = new ArrayList<>(Arrays.asList("&8&m============================",
                switch (abilityIntentionType) {
                    case EXCAVATION -> "&7  Digging Speed: "+a+"%";
                    case MINING -> "&7  Mining Speed: "+a+"%"; //D
                    case AXE -> "&7  Axe Power: &a"+a+"%"; //D
                    case SWORD -> "&7  Sweep Radius: &a"+a+"%";
                    case FARMING -> "&7  Double Crop Chance: &a"+(a-100)+"%"; //D
                    case FISHING -> "&7  Loot Bonus: &a"+a+"%";
                    case RANGED_COMBAT -> "&7  Pierce Chance: &a"+((a-100)*0.5)+"%"; //D
                    case DEFENSE_PASSIVE -> "&7  &7&m   &7 Stats &7&m   "; //D
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
                "  &7Progress: &8["+Utils.bar(f)+"&8] "+(rarity == Rarity.UNHOLY ? "&a&lMAX" : "&e"+ FormattedNumber.getInstance().getCommaFormattedNumber(f*100,1)+"%"),
                "  &7&oCrafted by "+(crafter != null ? "%katsu_player_"+crafter.getName()+"_small% &7&o"+crafter.getName() : "Server"),
                "&8&m                                           "
        ));

        if (abilityIntentionType == AbilityIntentionType.DEFENSE_PASSIVE) {

            lore.addAll(2,Arrays.asList("&7   &a&m+&e "+((a-100)*0.05)+" &c\uD83D\uDDA4",
                    "&7   &a&m+&e "+((a-100)*0.1)+"% &b\uD83E\uDD7E","&7   &a&m+&e -"+FormattedNumber.getInstance().getCommaFormattedNumber(pveReduction(itemStack),1)
                            +"% &9PVE Dmg Reduction \uD83D\uDEE1"));
        }

        meta.setLore(Utils.color(crafter,lore));


        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static double pveReduction(ItemStack itemStack) {

        if (EventListener.getAbilityIntentionType(itemStack) != AbilityIntentionType.DEFENSE_PASSIVE) return 0;

        NBTItem nbtItem = new NBTItem(itemStack);
        double a = nbtItem.getDouble("power");

        EquipmentHandler.Material material;

        try {
            material = EquipmentHandler.Material.valueOf(itemStack.getType().name().split("_")[0]);
        } catch (Exception exception) {
            material = EquipmentHandler.Material.LEATHER;
        }

        return ((a-100)*0.02*material.priority);
    }

    @EventHandler (priority = EventPriority.MONITOR,ignoreCancelled = true)
    public void onPVEDamageReduction(EntityDamageByEntityEvent entityDamageByEntityEvent) {

        if (!(Utils.getAttacker(entityDamageByEntityEvent.getDamager()) instanceof Player) && entityDamageByEntityEvent.getEntity() instanceof
                LivingEntity livingEntity) {

            double v = pveReduction(livingEntity.getEquipment().getItemInOffHand())+Arrays.stream(livingEntity.getEquipment().getArmorContents()).filter(Objects::nonNull).mapToDouble(CombineItemListener::pveReduction).sum();

            entityDamageByEntityEvent.setDamage(Math.max(0,entityDamageByEntityEvent.getDamage()*(1-v/100)));

        }

    }

    public static ItemStack combineResult(ItemStack itemStack1,ItemStack itemStack2,Player crafter) {
        Map<Enchantment,Integer> enchants1 = new HashMap<>(itemStack1.getEnchantments()),enchants2 = new HashMap<>(itemStack2.getEnchantments());
        enchants2.forEach((enchantment, integer) -> enchants1.put(enchantment,Math.max(enchants1.getOrDefault(enchantment,0),integer)));

        NBTItem nbtItem1 = new NBTItem(itemStack1),nbtItem2 = new NBTItem(itemStack2);
        int v = (nbtItem1.hasKey("combinePower") ? nbtItem1
                .getInteger("combinePower") : 1)*itemStack1.getAmount()+(nbtItem2.hasKey("combinePower") ? nbtItem2
                .getInteger("combinePower") : 1)*itemStack2.getAmount();
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
            return
                    //v == combinePowerRequired ? next(rarity.get(v)) :
                            rarity.get(v);
        }

        static {
            for (Rarity value : Rarity.values()) {
                rarity.put(switch (value) {
                    case COMMON -> 0;
                    case UNCOMMON -> COMMON.cumilitive;
                    case RARE -> UNCOMMON.cumilitive;
                    case EPIC -> RARE.cumilitive;
                    case LEGENDARY -> EPIC.cumilitive;
                    case MYTHIC -> LEGENDARY.cumilitive;
                    case GODLY -> MYTHIC.cumilitive;
                    case UNHOLY -> GODLY.cumilitive;
                },value);
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

            return this.combinePowerRequired-(this.cumilitive-combinePower);

        }

        public static double progress(int combinePower) {
            Rarity rarity1 = getRarity(combinePower);

            if (rarity1 == Rarity.UNHOLY) return 1;

            double a = rarity1.getCombinePowerInRarity(combinePower);
            return a/ (double) rarity1.combinePowerRequired;
        }


    }


}
