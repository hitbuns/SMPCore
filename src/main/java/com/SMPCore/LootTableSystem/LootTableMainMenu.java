package com.SMPCore.LootTableSystem;

import com.MenuAPI.GUISystem.AbstractClickableGUI;
import com.MenuAPI.GUISystem.Button;
import com.MenuAPI.GUISystem.iPage;
import com.MenuAPI.Utilities.DecorationUtils;
import com.MenuAPI.Utilities.ItemBuilder;
import com.MenuAPI.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class LootTableMainMenu extends AbstractClickableGUI {

    final iPage backPage;

    public LootTableMainMenu(Player player, iPage backPage) {
        super(player, "Loot-Table Manager", 3, false);

        this.backPage = backPage;

        init();
    }

    void init() {
        registerDefaultButtons();

        registerPriority(new Button(11,guiClickEvent -> {
                    close(getPlayer(),true);
                    getPlayer().sendMessage(Utils.color("&eUse /lt update <id> to create new or update existing loot tables!"));
                },false),
                new Button(15,guiClickEvent -> openPage(new LootTableManager(getPlayer(),this)),false),
                new Button(24,guiClickEvent -> closePage(backPage),false));

        setupInventory();
    }

    @Override
    public int getGUIId() {
        return 34;
    }

    @Override
    public void registerDefaultButtons() {
        getDefaultMap().put("default",new Button(-1,guiClickEvent ->
                guiClickEvent.getPlayer().sendMessage(Utils.color("&cYou cannot click here!")),false));
        setDefaultAction("default");
    }

    @Override
    public void setupInventory() {

        DecorationUtils.border(getInventory(), Material.BLACK_STAINED_GLASS_PANE);
        DecorationUtils.fillItem(getInventory(),Material.GRAY_STAINED_GLASS_PANE);

        getInventory().setItem(11,new ItemBuilder(Material.BARREL).setDisplayName("&eCreate a new Loot Table")
                .build(false));

        getInventory().setItem(15,new ItemBuilder(Material.WRITABLE_BOOK).setDisplayName("&cLoot Table Manager")
                .build(false));

        getInventory().setItem(22, new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("&cBack &ePage")
                .build(false));

    }

    @Override
    public void onOpen(Player player) {

    }

    @Override
    public void onClose(Player player) {
        openPage(backPage);
    }
}
