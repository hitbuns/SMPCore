package com.SMPCore.skills;

import com.MenuAPI.GUISystem.AbstractClickableGUI;
import com.MenuAPI.GUISystem.Button;
import com.MenuAPI.GUISystem.iPage;
import com.MenuAPI.Utilities.DecorationUtils;
import com.MenuAPI.Utilities.DescriptionBuilder;
import com.MenuAPI.Utilities.FormattedNumber;
import com.MenuAPI.Utilities.ItemBuilder;
import com.SMPCore.skills.impl.NonCombatStatType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SkillGUI extends AbstractClickableGUI {

    final iPage backPage;
    final Player player;

    public SkillGUI(Player player,Player skillPlayer, iPage backPage) {
        super(player, "Skill Profile - "+player.getName(), 4, false);
        
        this.backPage = backPage;
        this.player = skillPlayer != null ? skillPlayer : player;

        init();

    }
    
    public SkillGUI(Player player,iPage backPage) {
        this(player,null,backPage);
    }

    void init() {
        registerDefaultButtons();

        setupInventory();
    }


    @Override
    public int getGUIId() {
        return 102;
    }

    @Override
    public void registerDefaultButtons() {
        getDefaultMap().put("default", new Button(-1,guiClickEvent -> {},false));
        setDefaultAction("default");
    }

    @Override
    public void setupInventory() {

        DecorationUtils.border(getInventory(), Material.BLACK_STAINED_GLASS_PANE);
        DecorationUtils.fillItem(getInventory(),Material.GRAY_STAINED_GLASS_PANE);

        getInventory().setItem(11,new ItemBuilder(Material.STONE_HOE)
                .setDisplayName("&aFarming Lvl. &b"+PlayerDataHandler.getLevel(player, NonCombatStatType.FARMING)).setGlowing(true).setLore(DescriptionBuilder.init()
                        .addLore("&7("+
                                FormattedNumber.getInstance().getCommaFormattedNumber(PlayerDataHandler
                                        .getExp(player,NonCombatStatType.FARMING, PlayerDataHandler.ExpType.CURRENT),1)+"/"+
                                FormattedNumber.getInstance().getCommaFormattedNumber(PlayerDataHandler
                                        .getExp(player,NonCombatStatType.FARMING, PlayerDataHandler.ExpType.GOAL),1)+")").build()).build(false));
        getInventory().setItem(12,new ItemBuilder(Material.SMOKER)
                .setDisplayName("&aCooking Lvl. &b"+PlayerDataHandler.getLevel(player, NonCombatStatType.COOKING)).setGlowing(true).setLore(DescriptionBuilder.init()
                        .addLore("&7("+
                                FormattedNumber.getInstance().getCommaFormattedNumber(PlayerDataHandler
                                        .getExp(player,NonCombatStatType.COOKING, PlayerDataHandler.ExpType.CURRENT),1)+"/"+
                                FormattedNumber.getInstance().getCommaFormattedNumber(PlayerDataHandler
                                        .getExp(player,NonCombatStatType.COOKING, PlayerDataHandler.ExpType.GOAL),1)+")").build()).build(false));
        getInventory().setItem(13,new ItemBuilder(Material.FISHING_ROD)
                .setDisplayName("&aFishing Lvl. &b"+PlayerDataHandler.getLevel(player, NonCombatStatType.FISHING)).setGlowing(true).setLore(DescriptionBuilder.init()
                        .addLore("&7("+
                                FormattedNumber.getInstance().getCommaFormattedNumber(PlayerDataHandler
                                        .getExp(player,NonCombatStatType.FISHING, PlayerDataHandler.ExpType.CURRENT),1)+"/"+
                                FormattedNumber.getInstance().getCommaFormattedNumber(PlayerDataHandler
                                        .getExp(player,NonCombatStatType.FISHING, PlayerDataHandler.ExpType.GOAL),1)+")").build()).build(false));
        getInventory().setItem(14,new ItemBuilder(Material.OAK_WOOD)
                .setDisplayName("&aWoodcutting Lvl. &b"+PlayerDataHandler.getLevel(player, NonCombatStatType.WOODCUTTING)).setGlowing(true).setLore(DescriptionBuilder.init()
                        .addLore("&7("+
                                FormattedNumber.getInstance().getCommaFormattedNumber(PlayerDataHandler
                                        .getExp(player,NonCombatStatType.WOODCUTTING, PlayerDataHandler.ExpType.CURRENT),1)+"/"+
                                FormattedNumber.getInstance().getCommaFormattedNumber(PlayerDataHandler
                                        .getExp(player,NonCombatStatType.WOODCUTTING, PlayerDataHandler.ExpType.GOAL),1)+")").build()).build(false));
        getInventory().setItem(15,new ItemBuilder(Material.STONE_PICKAXE)
                .setDisplayName("&aMining Lvl. &b"+PlayerDataHandler.getLevel(player, NonCombatStatType.MINING)).setGlowing(true).setLore(DescriptionBuilder.init()
                        .addLore("&7("+
                                FormattedNumber.getInstance().getCommaFormattedNumber(PlayerDataHandler
                                        .getExp(player,NonCombatStatType.MINING, PlayerDataHandler.ExpType.CURRENT),1)+"/"+
                                FormattedNumber.getInstance().getCommaFormattedNumber(PlayerDataHandler
                                        .getExp(player,NonCombatStatType.MINING, PlayerDataHandler.ExpType.GOAL),1)+")").build()).build(false));

        getInventory().setItem(20,new ItemBuilder(Material.CRAFTING_TABLE)
                .setDisplayName("&aCrafting Lvl. &b"+PlayerDataHandler.getLevel(player, NonCombatStatType.CRAFTING)).setGlowing(true).setLore(DescriptionBuilder.init()
                        .addLore("&7("+
                                FormattedNumber.getInstance().getCommaFormattedNumber(PlayerDataHandler
                                        .getExp(player,NonCombatStatType.CRAFTING, PlayerDataHandler.ExpType.CURRENT),1)+"/"+
                                FormattedNumber.getInstance().getCommaFormattedNumber(PlayerDataHandler
                                        .getExp(player,NonCombatStatType.CRAFTING, PlayerDataHandler.ExpType.GOAL),1)+")").build()).build(false));
        getInventory().setItem(21,new ItemBuilder(Material.ENCHANTING_TABLE)
                .setDisplayName("&aEnchanting Lvl. &b"+PlayerDataHandler.getLevel(player, NonCombatStatType.ENCHANTING)).setGlowing(true).setLore(DescriptionBuilder.init()
                        .addLore("&7("+
                                FormattedNumber.getInstance().getCommaFormattedNumber(PlayerDataHandler
                                        .getExp(player,NonCombatStatType.ENCHANTING, PlayerDataHandler.ExpType.CURRENT),1)+"/"+
                                FormattedNumber.getInstance().getCommaFormattedNumber(PlayerDataHandler
                                        .getExp(player,NonCombatStatType.ENCHANTING, PlayerDataHandler.ExpType.GOAL),1)+")").build()).build(false));
        getInventory().setItem(23,new ItemBuilder(Material.ANVIL)
                .setDisplayName("&aSmithing Lvl. &b"+PlayerDataHandler.getLevel(player, NonCombatStatType.SMITHING)).setGlowing(true).setLore(DescriptionBuilder.init()
                        .addLore("&7("+
                                FormattedNumber.getInstance().getCommaFormattedNumber(PlayerDataHandler
                                        .getExp(player,NonCombatStatType.SMITHING, PlayerDataHandler.ExpType.CURRENT),1)+"/"+
                                FormattedNumber.getInstance().getCommaFormattedNumber(PlayerDataHandler
                                        .getExp(player,NonCombatStatType.SMITHING, PlayerDataHandler.ExpType.GOAL),1)+")").build()).build(false));
        getInventory().setItem(24,new ItemBuilder(Material.WRITABLE_BOOK)
                .setDisplayName("&aBartering Lvl. &b"+PlayerDataHandler.getLevel(player, NonCombatStatType.BARTER)).setGlowing(true).setLore(DescriptionBuilder.init()
                        .addLore("&7("+
                                FormattedNumber.getInstance().getCommaFormattedNumber(PlayerDataHandler
                                        .getExp(player,NonCombatStatType.BARTER, PlayerDataHandler.ExpType.CURRENT),1)+"/"+
                                FormattedNumber.getInstance().getCommaFormattedNumber(PlayerDataHandler
                                        .getExp(player,NonCombatStatType.BARTER, PlayerDataHandler.ExpType.GOAL),1)+")").build()).build(false));

        getInventory().setItem(22,new ItemBuilder(Material.SHIELD).setGlowing(true).setDisplayName("&eClick to view combat skills!").build(false));

    }

    @Override
    public void onOpen(Player player) {

    }

    @Override
    public void onClose(Player player) {

    }
}
