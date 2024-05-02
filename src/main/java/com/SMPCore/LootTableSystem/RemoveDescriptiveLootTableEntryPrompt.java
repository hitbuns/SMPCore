package com.SMPCore.LootTableSystem;

import com.MenuAPI.GUISystem.AreYouSureGUI;
import org.bukkit.entity.Player;

public class RemoveDescriptiveLootTableEntryPrompt extends AreYouSureGUI {

    public RemoveDescriptiveLootTableEntryPrompt(Player player, LootTableEntryListManager lootTableEntryListManager,
                                                 RewardsAPI.LootTableEntry.DescriptiveLootTableEntry descriptiveLootTableEntry) {
        super(player, "Remove Loot Table Entry?",(guiClickEvent, responseType) -> {

            if (responseType == ResponseType.YES) {

                lootTableEntryListManager.lootTableCreator.list.remove(descriptiveLootTableEntry);
                lootTableEntryListManager.updateEntries();
                lootTableEntryListManager.update();

            }

        },(abstractGUI, player1) ->  {

        }, lootTableEntryListManager);
    }


}
