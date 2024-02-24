package com.SMPCore.gui;

import com.MenuAPI.GUISystem.AbstractClickableGUI;
import com.MenuAPI.GUISystem.Button;
import com.MenuAPI.GUISystem.iPage;
import com.MenuAPI.Utilities.DecorationUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ClaimMenu extends AbstractClickableGUI {

    final iPage backPage;

    public ClaimMenu(Player player, iPage backPage) {
        super(player, "Manage Claims", 3, false);

        this.backPage =backPage;

        init();
    }

    void init() {
        registerDefaultButtons();


        setupInventory();
    }

    @Override
    public int getGUIId() {
        return 200;
    }

    @Override
    public void registerDefaultButtons() {
        getDefaultMap().put("default",
                new Button(-1,guiClickEvent -> {},false));
        setDefaultAction("default");
    }

    @Override
    public void setupInventory() {

        DecorationUtils.border(getInventory(), Material.BLACK_STAINED_GLASS_PANE);
        DecorationUtils.fillItem(getInventory(),Material.GRAY_STAINED_GLASS_PANE);


        

    }

    @Override
    public void onOpen(Player player) {

    }

    @Override
    public void onClose(Player player) {

    }
}
