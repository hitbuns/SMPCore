package com.SMPCore.LootTableSystem;

import com.MenuAPI.GUISystem.AreYouSureGUI;
import com.MenuAPI.GUISystem.iPage;
import org.bukkit.entity.Player;

public class LootTableCollectionRemovePrompt extends AreYouSureGUI {

    public LootTableCollectionRemovePrompt(Player player,String lootTableCollection, iPage page) {
        super(player, "Remove Loot Table Collection = "+lootTableCollection,(guiClickEvent, responseType) -> {
            if (responseType == ResponseType.YES) {

                RewardsAPI.getInstance().setRewardPath(lootTableCollection);

            }
        },(abstractGUI, player1) -> {}, page);
    }

}
