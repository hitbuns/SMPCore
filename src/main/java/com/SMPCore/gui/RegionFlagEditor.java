package com.SMPCore.gui;

import com.MenuAPI.ChatMessageResponseSystem.ChatMessageResponderHandlerList;
import com.MenuAPI.GUISystem.AbstractModifiableListMenu;
import com.MenuAPI.GUISystem.GUIClickRunnable;
import com.MenuAPI.GUISystem.iPage;
import com.MenuAPI.Utilities.DecorationUtils;
import com.MenuAPI.Utilities.DescriptionBuilder;
import com.MenuAPI.Utilities.ItemBuilder;
import com.MenuAPI.Utils;
import com.SMPCore.Main;
import com.SMPCore.Utilities.FlagRegistryConfig;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RegionFlagEditor extends AbstractModifiableListMenu<Flag> implements ChatMessageResponderHandlerList.ChatMessageResponder {

    static Flag<?>[] flags;
    final ProtectedRegion protectedRegion;
    boolean ready = false;
    ItemStack itemStack = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("&cX").setGlowing(true).build(false);
    boolean viewOnly;


    public static void updateFlagsRegistry() {

        if (FlagRegistryConfig.Instance == null) FlagRegistryConfig.register(Main.Instance);

        flags = WorldGuard.getInstance().getFlagRegistry().getAll().stream()
                .filter(flag -> FlagRegistryConfig.Instance.isFlagEnabled(flag)).sorted((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.getName(),
                        o2.getName())).toArray(Flag[]::new);
    }

    public RegionFlagEditor(Player player,boolean viewOnly,ProtectedRegion protectedRegion, iPage backPage) {
        super(player, "Editing Region Flags...", backPage, 4, Flag.class);

        this.protectedRegion = protectedRegion;
        this.viewOnly = viewOnly;

        if (protectedRegion == null) {
            close(getPlayer(),true);
            return;
        }

        init(9,17,31,10,11,12,13,14,15,16,19,20,21,22,23,24,25,26);

    }

    @Override
    public ItemStack a(int i) {

        if (flags.length < i) return getDefaultFillItem().clone();

        Flag<?> flag = flags[i-1];
        if (flag instanceof StateFlag stateFlag) {
            StateFlag.State state = protectedRegion.getFlag(stateFlag);

            if (state == null) state = stateFlag.getDefault();

            assert state != null;

            ItemStack itemStack1 = new ItemBuilder(Material.ITEM_FRAME)
                    .setDisplayName("&9"+flag.getName()).setLore(DescriptionBuilder.init()
                            .addLore("&7Value: &a"+state.name(),"","&e>| Click to edit value").build())
                    .build(false);

            NBTItem nbtItem = new NBTItem(itemStack1);
            nbtItem.setInteger("clickValue",i-1);
            return nbtItem.getItem();

        } else if (flag instanceof BooleanFlag booleanFlag) {

            boolean state = protectedRegion.getFlag(booleanFlag).booleanValue();

            ItemStack itemStack1 = new ItemBuilder(Material.ITEM_FRAME)
                    .setDisplayName("&9"+flag.getName()).setLore(DescriptionBuilder.init()
                            .addLore("&7Value: &a"+state,"","&e>| Click to edit value").build())
                    .build(false);

            NBTItem nbtItem = new NBTItem(itemStack1);
            nbtItem.setInteger("clickValue",i-1);
            return nbtItem.getItem();

        } else if (flag instanceof StringFlag stateFlag) {
            String state = protectedRegion.getFlag(stateFlag);

            if (state == null) state = stateFlag.getDefault();

            ItemStack itemStack1 = new ItemBuilder(Material.ITEM_FRAME)
                    .setDisplayName("&9"+flag.getName()).setLore(DescriptionBuilder.init()
                            .addLore("&7Value: &a"+state,"","&e>| Click to edit value").build())
                    .build(false);

            NBTItem nbtItem = new NBTItem(itemStack1);
            nbtItem.setInteger("clickValue",i-1);
            return nbtItem.getItem();
        }

        return new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).setDisplayName(flag.getName())
                .setLore(DescriptionBuilder.init()
                        .addLore("&7Value: &a"+protectedRegion.getFlag(flag),"","&4[!] &cUnable to modify this flag!").build()).build(false);
    }

    @Override
    public Flag<?>[] getIndexes() {
        return flags;
    }

    @Override
    public ItemStack getDefaultFillItem() {
        return itemStack;
    }

    @Override
    public GUIClickRunnable onGUIClick() {
        return guiClickEvent -> {

            ItemStack itemStack1 = guiClickEvent.getCurrentItem();

            if (Utils.isNullorAir(itemStack1)) return;

            NBTItem nbtItem = new NBTItem(itemStack1);
            if (nbtItem.hasKey("clickValue")) {

                if (viewOnly) {
                    getPlayer().sendMessage(Utils.color("&cYou are currently in view mode only as you do not have permission to edit this region flag claims!"));
                    return;
                }

                Flag<?> flag = flags[nbtItem.getInteger("clickValue")];

                if (flag instanceof StateFlag stateFlag) {
                    openPage(new StateFlagEditor(getPlayer(),this,stateFlag));
                } else if (flag instanceof BooleanFlag booleanFlag) {
                    openPage(new BooleanFlagEditor(getPlayer(),this,booleanFlag));
                } else if (flag instanceof StringFlag stringFlag) {
                    close(getPlayer(),true);
                    this.choice = stringFlag;
                    ChatMessageResponderHandlerList.getInstance().addMessageResponders(getPlayer(),this);

                    getPlayer().sendMessage(Utils.color("&eType a valid string input for this flag. To cancel this process, type '!cancel'. To put the input as NULL, type in '!null'"));

                }

            }

        };
    }

    StringFlag choice;

    @Override
    public int getGUIId() {
        return 101;
    }

    @Override
    public void setupInventory() {
        DecorationUtils.border(getInventory(),Material.BLACK_STAINED_GLASS_PANE);
        DecorationUtils.fillItem(getInventory(),Material.GRAY_STAINED_GLASS_PANE);
        ready = true;
    }

    @Override
    public void onOpen(Player player) {
        update();
    }

    @Override
    public void onClose(Player player) {

    }

    @Override
    public boolean run(Player player, String s) {

        if (s.equalsIgnoreCase("!cancel")) {
            ChatMessageResponderHandlerList.getInstance().removeMessageResponders(getPlayer(),this);
            this.open(player);
            return true;
        }

        String v = s.equalsIgnoreCase("!null") ? null :
                s;

        protectedRegion.setFlag(choice,v);
        ChatMessageResponderHandlerList.getInstance().removeMessageResponders(getPlayer(),this);
        getPlayer().sendMessage(Utils.color("&a&lSUCCESS &eSet Flag '"+choice.getName()+"' value to '&a"+
                v+"'"));
        this.open(player);

        return true;
    }
}
