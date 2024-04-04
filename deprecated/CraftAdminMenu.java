package com.SMPCore.gui;

import com.MenuAPI.GUISystem.AbstractClickableGUI;
import com.MenuAPI.GUISystem.Button;
import com.MenuAPI.GUISystem.ItemAssignedButton;
import com.MenuAPI.GUISystem.iPage;
import com.MenuAPI.Utilities.DecorationUtils;
import com.MenuAPI.Utilities.ItemBuilder;
import com.MenuAPI.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class CraftAdminMenu extends AbstractClickableGUI {


    final iPage backPage;

    public CraftAdminMenu(Player player, iPage backPage) {
        super(player, "Admin Crafting Menu", 3, false);

        this.backPage = backPage;

        init();


    }

    void init() {
        registerDefaultButtons();



        setupInventory();
    }

    @Override
    public int getGUIId() {
        return 1001;
    }

    @Override
    public void registerDefaultButtons() {
        getDefaultMap().put("default",new Button(-1,guiClickEvent -> getPlayer().sendMessage(Utils.color("&cYou cannot click here!")),false));
        setDefaultAction("default");
    }

    @Override
    public void setupInventory() {
        DecorationUtils.border(getInventory(), Material.BLACK_STAINED_GLASS_PANE);
        DecorationUtils.fillItem(getInventory(),Material.GRAY_STAINED_GLASS_PANE);

        registerPriority(new ItemAssignedButton(new ItemBuilder(Material.NAME_TAG)
                .setDisplayName("&eManage Crafting Recipes").
                setGlowing(true).build(false),new Button(11,guiClickEvent -> {

        },false)),
                new ItemAssignedButton(new ItemBuilder(Material.BARRIER)
                        .setDisplayName("&cClose").
                        setGlowing(true).build(false),new Button(15,guiClickEvent -> close(getPlayer()),false)));

    }

    @Override
    public void onOpen(Player player) {

    }

    @Override
    public void onClose(Player player) {

    }
}
