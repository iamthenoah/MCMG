package com.than00ber.mcmg.events;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.MiniGameItem;
import com.than00ber.mcmg.init.MiniGameTeams;
import com.than00ber.mcmg.util.ChatUtil;
import com.than00ber.mcmg.util.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class GlobalEvents implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (Main.MINIGAME_ENGINE.hasRunningGame()) {
            Player player = event.getPlayer();
            MiniGameTeams.SPECTATORS.prepare(player);
            ChatUtil.toSelf(player,
                    ChatColor.GOLD + "A minigame has already started.",
                    ChatColor.GOLD + "You are now a spectator until the next round."
            );
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (Main.MINIGAME_ENGINE.hasRunningGame()) {
            Player player = event.getPlayer();
            MiniGameTeams.resetPlayer(player);
            Main.MINIGAME_ENGINE.getCurrentGame().removePlayer(player);
            ChatUtil.toAll(TextUtil.formatPlayer(player) + " has been removed from the minigame.");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getItemMeta() != null) {
            ItemMeta meta = event.getItem().getItemMeta();
            String name = ChatColor.stripColor(meta.getDisplayName());

            if (MiniGameItem.TOGGLEABLE_ITEMS.containsKey(name)) {
                MiniGameItem.TOGGLEABLE_ITEMS.get(name).getAction().onClick(event);
            }
        }
    }
}
