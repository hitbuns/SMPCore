package com.SMPCore.Waypoints;

import com.MenuAPI.GUISystem.AreYouSureGUI;
import com.MenuAPI.GUISystem.iPage;
import com.MenuAPI.Utils;
import org.bukkit.entity.Player;

public class WaypointUpdatePrompt extends AreYouSureGUI {

    public WaypointUpdatePrompt(Player player, WaypointConfig.WayPointInfo wayPointInfo, iPage backPage) {
        super(player, "Are you sure you want to update this waypoint?",
                (guiClickEvent, responseType) -> {

            if (responseType == ResponseType.YES && wayPointInfo != null) {
                WaypointConfig.getInstance().register(wayPointInfo,true);
                player.sendMessage(Utils.color("&a["+wayPointInfo.getId()+"] &eYou have successfully updated this waypoint!"));
            }

                }, (abstractGUI, player1) -> {}, backPage);
    }

    public WaypointUpdatePrompt(Player player, WaypointConfig.WayPointInfo wayPointInfo) {
        this(player,wayPointInfo,null);
    }

}
