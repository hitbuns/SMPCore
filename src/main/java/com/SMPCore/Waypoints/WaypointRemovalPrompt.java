package com.SMPCore.Waypoints;

import com.MenuAPI.GUISystem.AreYouSureGUI;
import com.MenuAPI.GUISystem.iPage;
import org.bukkit.entity.Player;

public class WaypointRemovalPrompt extends AreYouSureGUI {
    public WaypointRemovalPrompt(Player player, WaypointConfig.WayPointInfo wayPointInfo, iPage backPage) {
        super(player, "Remove Waypoint = {"+
                (wayPointInfo != null ? wayPointInfo.getId() : null)+"}", (guiClickEvent, responseType) -> {

            if (responseType == ResponseType.YES) {
                try {
                    WaypointConfig.getInstance().deregister(wayPointInfo.getId());
                } catch (Exception ignored) {

                }
            }

        }, (abstractGUI, player1) -> {}, backPage);
    }
}
