package com.than00ber.mcmg.events;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.init.GameTeams;
import com.than00ber.mcmg.util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GlobalEventListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (Main.GAME_ENGINE.hasRunningGame()) {
            Player player = event.getPlayer();
            GameTeams.SPECTATORS.prepare(player);
            ChatUtil.toSelf(player,
                    ChatColor.GOLD + "A game has already started.",
                    ChatColor.GOLD + "You are now a spectator until the next round."
            );
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (Main.GAME_ENGINE.hasRunningGame()) {
            Player player = event.getPlayer();
            Main.GAME_ENGINE.getCurrentGame().removePlayer(player);
            ChatUtil.toAll(player.getDisplayName() + " has been removed from the game.");
        }
    }
}
