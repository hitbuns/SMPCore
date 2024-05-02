package com.SMPCore.LootTableSystem;

import com.MenuAPI.ChatMessageResponseSystem.ChatMessageResponderHandlerList;
import com.MenuAPI.GUISystem.AbstractClickableGUI;
import com.MenuAPI.GUISystem.Button;
import com.MenuAPI.Utilities.DecorationUtils;
import com.MenuAPI.Utilities.DescriptionBuilder;
import com.MenuAPI.Utilities.ItemBuilder;
import com.MenuAPI.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class LootTableEntryModifier extends AbstractClickableGUI implements ChatMessageResponderHandlerList.ChatMessageResponder {

    final LootTableEntryListManager lootTableEntryListManager;
    final RewardsAPI.LootTableEntry.DescriptiveLootTableEntry descriptiveLootTableEntry;

    public LootTableEntryModifier(Player player, RewardsAPI.LootTableEntry.DescriptiveLootTableEntry descriptiveLootTableEntry,
                                  LootTableEntryListManager lootTableEntryListManager) {
        super(player, "Modifying Loot Table Entry",3,false);


        this.lootTableEntryListManager = lootTableEntryListManager;
        this.descriptiveLootTableEntry = descriptiveLootTableEntry;


        init();
    }

    void init() {
        registerDefaultButtons();

        registerPriority(new Button(11,guiClickEvent -> openPage(new LootTableChanceModifier(getPlayer()
        ,this)),false),
                new Button(12,guiClickEvent -> {

                    switch (guiClickEvent.getClickType()) {

                        case LEFT,SHIFT_LEFT -> {
                            close(getPlayer(),true);
                            this.changeType = ChangeType.COMMAND;
                            ChatMessageResponderHandlerList.getInstance().addMessageResponders(getPlayer(),this);
                            getPlayer().sendMessage(Utils.color("&ePlease type the reward command that will run without '/' in front using %player% as a player placeholder. To cancel this process type 'cancel'"));
                        }
                        case RIGHT -> openPage(new MISelectorPrompt(getPlayer(),this));
                        case SHIFT_RIGHT -> openPage(new MoneyEntryModifier(getPlayer(),this));
                    }


                },false),
                new Button(14,guiClickEvent -> {
                    close(getPlayer(),true);
                    this.changeType = ChangeType.DESCRIPTOR;
                    ChatMessageResponderHandlerList.getInstance().addMessageResponders(getPlayer(),this);
                    getPlayer().sendMessage(Utils.color("&ePlease type a reward description. To cancel this process type 'cancel'"));

                },false),
                new Button(15,guiClickEvent -> openPage(new MaterialSelectorGUI(getPlayer(),this)),false),
                new Button(22,guiClickEvent -> openPage(lootTableEntryListManager),false));

        setupInventory();
    }

    @Override
    public int getGUIId() {
        return 36;
    }

    @Override
    public void registerDefaultButtons() {
        getDefaultMap().put("default",new Button(-1,guiClickEvent -> guiClickEvent
                .getPlayer().sendMessage(Utils.color("&cYou cannot click here!")),false));
        setDefaultAction("default");
    }


    @Override
    public void setupInventory() {

        DecorationUtils.border(getInventory(), Material.BLACK_STAINED_GLASS_PANE);
        DecorationUtils.fillItem(getInventory(),Material.GRAY_STAINED_GLASS_PANE);

        getInventory().setItem(22,new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("&4Back &cPage").build(false));
    }

    void updateSetUp() {
        getInventory().setItem(11,new ItemBuilder(Material.SUNFLOWER).setDisplayName("&eChance: &a"+descriptiveLootTableEntry.getChance()+"%").build(false));
        getInventory().setItem(12,new ItemBuilder(Material.BOOK).setDisplayName("&eCommand: "+(descriptiveLootTableEntry.getCommand() != null ?
                "&a/"+descriptiveLootTableEntry.getCommand() : "&cNot Set"))
                .setLore(DescriptionBuilder.init().addLore("&7Left-click to do a custom command!",
                        "&7Right-click to select an item as a reward from MI selector",
                        "&7Shift-Right-click to select money as a reward").build()).build(false));
        getInventory().setItem(14,new ItemBuilder(Material.PAPER).setDisplayName("&eDescriptor:")
                .setLore(DescriptionBuilder.init().addLore(descriptiveLootTableEntry.getDescriptor() != null ?
                        descriptiveLootTableEntry.getDescriptor() : "",false,25).build()).build(false));
        getInventory().setItem(15,new ItemBuilder(descriptiveLootTableEntry.getMaterial() != null ? descriptiveLootTableEntry
                .getMaterial(): Material.BARRIER).setDisplayName("&eMaterial: "+
                (descriptiveLootTableEntry.getMaterial() != null ? "&a"+descriptiveLootTableEntry.getMaterial().name() : "&cNot Set")).build(false));
    }

    @Override
    public void onOpen(Player player) {
        updateSetUp();
    }

    @Override
    public void onClose(Player player) {

    }

    ChangeType changeType;

    public enum ChangeType {

        COMMAND,
        DESCRIPTOR
        ;

    }

    @Override
    public boolean run(Player player, String s) {

        if (s.equalsIgnoreCase("cancel")) {
            ChatMessageResponderHandlerList.getInstance()
                    .setGUIOpenable(getPlayer(),true);
            open(getPlayer());
            return true;
        }

        if (changeType == ChangeType.COMMAND)
        descriptiveLootTableEntry.setCommand(s);
        else descriptiveLootTableEntry.setDescriptor(Utils.color(s));

        ChatMessageResponderHandlerList.getInstance()
                .setGUIOpenable(getPlayer(),true);
        open(getPlayer());

        return true;
    }
}
