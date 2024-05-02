package com.SMPCore.LootTableSystem;

import com.MenuAPI.ChatMessageResponseSystem.ChatMessageResponderHandlerList;
import com.MenuAPI.GUISystem.*;
import com.MenuAPI.Utilities.DecorationUtils;
import com.MenuAPI.Utilities.DescriptionBuilder;
import com.MenuAPI.Utilities.ItemBuilder;
import com.MenuAPI.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MIQualityBoundarySelector extends AbstractClickableGUI {

    public enum MIQualityType {

        PRECISE,
        BOUNDS
        ;


    }

    MIQualityType miQualityType = MIQualityType.BOUNDS;

    int min = 0,max = 150,precise = 100;
    final MISelectorPrompt backPage;
    final String miItemId;

    public MIQualityBoundarySelector(Player player,String miItemId, MISelectorPrompt backPage) {
        super(player, "Select boundaries for MI Quality",3,false);

        this.backPage = backPage;
        this.miItemId = miItemId;

        init();
    }

    void init () {
        registerDefaultButtons();

        registerPriority(new Button(11,guiClickEvent -> {

            if (miQualityType == MIQualityType.BOUNDS) {
                miValueChanger.changeType = ChangeType.MIN;
                openPage(new ChangerMenu(getPlayer(),
                        this));
            }

                },false),
                new Button(13,guiClickEvent -> {

                    if (miQualityType == MIQualityType.PRECISE) {
                        miValueChanger.changeType = ChangeType.PRECISE;
                        openPage(new ChangerMenu(getPlayer(),
                                this));
                    }

                },false),
                new Button(15,guiClickEvent -> {

                    if (miQualityType == MIQualityType.BOUNDS) {
                        miValueChanger.changeType = ChangeType.MAX;
                        openPage(new ChangerMenu(getPlayer(),
                                this));
                    }

                },false),
                new Button(26,guiClickEvent -> {
                    this.miQualityType = miQualityType == MIQualityType.PRECISE ? MIQualityType.BOUNDS : MIQualityType.PRECISE;
                    update();
                },false));

        registerPriority(new ItemAssignedButton(new ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                .setDisplayName("&aBuild Command").build(false),new Button(22,
                guiClickEvent -> {
                    backPage.lootTableEntryModifier
                            .descriptiveLootTableEntry.setCommand("lqi %player% "+miItemId+" "+
                                    (miQualityType == MIQualityType.BOUNDS ? min + " "+ max  :
                                            precise));
                    openPage(backPage.lootTableEntryModifier);
                },false)));

        setupInventory();
    }


    @Override
    public int getGUIId() {
        return 10007;
    }

    @Override
    public void registerDefaultButtons() {
        getDefaultMap().put("default",new Button(-1,guiClickEvent ->
                getPlayer().sendMessage(Utils.color("&cYou cannot click here!")),false));

        setDefaultAction("default");
    }

    @Override
    public void setupInventory() {

        DecorationUtils.border(getInventory(), Material.BLACK_STAINED_GLASS_PANE);
        DecorationUtils.fillItem(getInventory(),Material.GRAY_STAINED_GLASS_PANE);

    }

    void update() {

        boolean b = miQualityType == MIQualityType.BOUNDS;

        ItemStack itemStack1 = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

        getInventory().setItem(11, b ? new ItemBuilder(Material.WRITABLE_BOOK)
                .setDisplayName("&7Min Quality: &a" +min+"%")
                .setLore(DescriptionBuilder.init()
                        .addLore("&7>| Click to change/edit the min quality!").build()).build(false) :
                itemStack1.clone());
        getInventory().setItem(15, b ? new ItemBuilder(Material.WRITABLE_BOOK)
                .setDisplayName("&7Max Quality: &a" +max+"%")
                .setLore(DescriptionBuilder.init()
                        .addLore("&7>| Click to change/edit the max quality!").build()).build(false) : itemStack1
                .clone());

        getInventory().setItem(13, !b ? new ItemBuilder(Material.WRITABLE_BOOK)
                .setDisplayName("&7Quality: &a" +precise+"%")
                .setLore(DescriptionBuilder.init()
                        .addLore("&7>| Click to change/edit the precise quality!").build()).build(false) : itemStack1
                .clone());

        getInventory().setItem(26,new ItemBuilder(miQualityType == MIQualityType.PRECISE ? Material.GLOWSTONE_DUST :
                Material.REDSTONE)
                .setDisplayName("&7Quality Boundary Type: &a"+miQualityType.name())
                .setLore(DescriptionBuilder.init()
                        .addLore("&7&oClick to change the type of MI Quality").build())
                .build(false));


    }

    @Override
    public void onOpen(Player player) {
        update();
    }

    @Override
    public void onClose(Player player) {

    }

    MIValueChanger miValueChanger = new MIValueChanger();


    public enum ChangeType {
        MIN,
        MAX,
        PRECISE
        ;

    }

    public class MIValueChanger implements AbstractValueChangerMenu.ChangeModifierType<Integer> {

        ChangeType changeType;
        static final Integer[] ints = new Integer[] {-5,-2,-1,1,2,5};


        @Override
        public Integer[] getTypeValues() {
            return ints;
        }

        @Override
        public String getDisplayId() {
            return changeType.name();
        }

        @Override
        public String getUnits() {
            return "%";
        }

        @Override
        public int getTypeValue() {
            return 0;
        }

        @Override
        public Integer getMin() {
            return 0;
        }

        @Override
        public Integer getMax() {
            return Integer.MAX_VALUE;
        }

        @Override
        public ValueChangeRunnable<Integer> getChangeRunnable() {
            return (s, i, integer) -> {
                switch (changeType) {
                    case MAX -> max = integer;
                    case MIN -> min = integer;
                    case PRECISE -> precise = integer;
                };
            };
        }

        @Override
        public ValueGrabRunnable<Integer> getGrabRunnable() {
            return (s, i) -> switch (changeType) {
                case PRECISE -> precise;
                case MAX -> max;
                case MIN -> min;
            };
        }
    }

    static class ChangerMenu extends AbstractValueChangerMenu implements ChatMessageResponderHandlerList.ChatMessageResponder {

        final MIQualityBoundarySelector miQualityBoundarySelector;

        public ChangerMenu(Player player,MIQualityBoundarySelector miQualityBoundarySelector) {
            super(player, "Modifying "+miQualityBoundarySelector
                    .miValueChanger
                    .changeType.name(), miQualityBoundarySelector, miQualityBoundarySelector.miValueChanger);

            this.miQualityBoundarySelector = miQualityBoundarySelector;

            registerPriority(new ItemAssignedButton(new ItemBuilder(Material.KNOWLEDGE_BOOK)
                    .setDisplayName("&eSet a custom % quality amount")
                    .setLore(DescriptionBuilder.init()
                            .addLore("&7>| Click to set a custom amount for the quality %").build()).build(false),
                    new Button(4,guiClickEvent -> {
                        close(getPlayer(),true);
                        ChatMessageResponderHandlerList.getInstance().addMessageResponders(getPlayer(),this);
                        player.sendMessage(Utils.color("&ePlease provide a valid integer input for the % quality. To cancel this process, type 'cancel'"));
                    },false)));


        }

        @Override
        public int getGUIId() {
            return 10006;
        }

        @Override
        public boolean run(Player player, String s) {
            if (s.equalsIgnoreCase("cancel")) {
                ChatMessageResponderHandlerList.getInstance()
                        .setGUIOpenable(getPlayer(),true);
                open(getPlayer());
                return true;
            }

            try {

                switch (miQualityBoundarySelector.miValueChanger.changeType) {
                    case MIN -> this.miQualityBoundarySelector.min =
                            Math.max(0,Integer.parseInt(s));
                    case MAX -> this.miQualityBoundarySelector.max =
                            Math.max(0,Integer.parseInt(s));
                    case PRECISE -> this.miQualityBoundarySelector.precise =
                            Math.max(0,Integer.parseInt(s));
                }

                ChatMessageResponderHandlerList.getInstance()
                        .setGUIOpenable(getPlayer(), true);
                open(player);
                return true;
            } catch (Exception exception) {
                player.sendMessage(Utils.color("&ePlease provide a valid integer input for the % quality. To cancel this process, type 'cancel'"));
                return false;
            }
        }
    }

}
