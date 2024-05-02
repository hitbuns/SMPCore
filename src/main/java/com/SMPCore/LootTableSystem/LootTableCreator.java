package com.SMPCore.LootTableSystem;

import com.MenuAPI.GUISystem.AbstractClickableGUI;
import com.MenuAPI.GUISystem.Button;
import com.MenuAPI.GUISystem.iPage;
import com.MenuAPI.Utilities.DecorationUtils;
import com.MenuAPI.Utilities.ItemBuilder;
import com.MenuAPI.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class LootTableCreator extends AbstractClickableGUI {

    /*

    String id
    List<DescriptiveLootTableEntry> list

    #Variables
    double chance
    String command
    String descriptor
    Material material

     */

    String id;
    List<RewardsAPI.LootTableEntry.DescriptiveLootTableEntry> list;
    boolean restricted = false;
    final iPage backPage;


    public LootTableCreator(Player player, String lootTableId, iPage backPage) {
        super(player, "Modifying Loot Table Id = "+lootTableId,3,false);

        this.backPage =backPage;

        if (lootTableId == null || lootTableId.equalsIgnoreCase("")) {
            restricted = true;
            getPlayer().sendMessage(Utils.color("&cYou cannot have a null ID"));
            return;
        }

        this.id = lootTableId.replace(".","_");
        this.list = RewardsAPI.getInstance().getRewardsPackage(id);

        init();
    }

    void init() {
        registerDefaultButtons();

        registerPriority(new Button(11,guiClickEvent -> openPage(new LootTableEntryListManager(getPlayer(),this)),false),
                new Button(15,guiClickEvent -> {
                    RewardsAPI.getInstance().setRewardPath(id,this.list.toArray(RewardsAPI.LootTableEntry.DescriptiveLootTableEntry[]::new));
                    getPlayer().sendMessage(Utils.color("&eSuccessfully saved Loot Table id = &a"+id));
                    openPage(backPage);
                },false));


        setupInventory();
    }

    @Override
    public int getGUIId() {
        return 33;
    }

    @Override
    public void registerDefaultButtons() {
        getDefaultMap().put("default",new Button(-1,guiClickEvent -> guiClickEvent.getPlayer()
                .sendMessage(Utils.color("&cYou cannot click here!")),false));
        setDefaultAction("default");
    }

    @Override
    public void setupInventory() {

        DecorationUtils.border(getInventory(), Material.BLACK_STAINED_GLASS_PANE);
        DecorationUtils.fillItem(getInventory(),Material.GRAY_STAINED_GLASS_PANE);

        getInventory().setItem(11,new ItemBuilder(Material.WRITABLE_BOOK).setDisplayName(
                "&7Manage Loot Table Entries"
        ).build(false));

        getInventory().setItem(13,new ItemBuilder(Material.BARREL).setDisplayName("&eLoot Table Id: &a"+
                id).build(false));
        getInventory().setItem(15,new ItemBuilder(Material.EMERALD).setDisplayName("&eUpdate Changes")
                .build(false));

    }

    @Override
    public void onOpen(Player player) {
        if (restricted) close(player,true);
    }

    @Override
    public void onClose(Player player) {

    }
}
