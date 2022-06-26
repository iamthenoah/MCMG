package com.than00ber.mcmg.events;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.core.MiniGameItem;
import com.than00ber.mcmg.registries.AllItems;
import com.than00ber.mcmg.registries.AllTeams;
import com.than00ber.mcmg.util.ChatUtil;
import com.than00ber.mcmg.util.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Optional;

public class GlobalEvents implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (Main.MINIGAME_ENGINE.hasRunningGame()) {
            Player player = event.getPlayer();
            AllTeams.SPECTATORS.prepare(player);
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
            AllTeams.resetPlayer(player);
            Main.MINIGAME_ENGINE.getCurrentGame().removePlayer(player);
            ChatUtil.toAll(TextUtil.formatPlayer(player) + " has been removed from the minigame.");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() != null) {
            ItemMeta meta = event.getItem().getItemMeta();

            if (meta != null) {
                String name = ChatColor.stripColor(meta.getDisplayName()
                        .replaceAll("[-+.^:,']","")
                        .replaceAll(" ", "_")
                ).toLowerCase(); // TODO - review this
                MiniGameItem item = AllItems.ITEMS.get(name);
                Optional.ofNullable(item)
                        .flatMap(i -> Optional.ofNullable(i.getAction()))
                        .ifPresent(a -> a.onClick(event));
            }
        }
    }

    @EventHandler
    public void onEntityPickupItemEvent(EntityPickupItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDropItemEvent(EntityDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }
}
