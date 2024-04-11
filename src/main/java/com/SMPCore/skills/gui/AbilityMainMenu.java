package com.SMPCore.skills.gui;

import com.MenuAPI.GUISystem.AbstractClickableGUI;
import com.MenuAPI.GUISystem.Button;
import com.MenuAPI.GUISystem.ItemAssignedButton;
import com.MenuAPI.GUISystem.iPage;
import com.MenuAPI.Utilities.DecorationUtils;
import com.MenuAPI.Utilities.DescriptionBuilder;
import com.MenuAPI.Utilities.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class AbilityMainMenu extends AbstractClickableGUI {

    final iPage backPage;

    public AbilityMainMenu(Player player, iPage backPage) {
        super(player, "Ability Selector", 4, false);
        this.backPage = backPage;

        init();
    }

    void init() {
        registerDefaultButtons();

        registerPriority(new ItemAssignedButton(new ItemBuilder(Material.IRON_PICKAXE)
                .setGlowing(true).setDisplayName("&bMining Abilities").setLore(DescriptionBuilder.init()
                        .addLore("&7&lClick to select a mining ability")
                        .build()).build(false), new Button(11,guiClickEvent -> {

        },false))
        );

        setupInventory();
    }

    @Override
    public int getGUIId() {
        return 300;
    }

    @Override
    public void registerDefaultButtons() {
        getDefaultMap().put("default",new Button(-1,guiClickEvent -> {},false));
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
