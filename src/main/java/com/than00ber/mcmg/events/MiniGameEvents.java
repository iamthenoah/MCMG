package com.than00ber.mcmg.events;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.core.MiniGameEngine;
import com.than00ber.mcmg.minigames.MiniGame;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public abstract class MiniGameEvents<G extends MiniGame> implements Listener {

    protected final Main instance;
    protected final G minigame;

    protected MiniGameEvents(Main instance, G minigame) {
        this.instance = instance;
        this.minigame = minigame;
    }

    public void unregister() {
        HandlerList.unregisterAll(minigame.getEventListener());
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(minigame.getEventListener(), instance);
    }

    @EventHandler
    public void handleDisableDamageWhileGrace(EntityDamageByEntityEvent event) {
        if (Main.MINIGAME_ENGINE.getRoundState() == MiniGameEngine.MiniGameRoundState.GRACE) {
            event.setCancelled(true);
            event.setDamage(0);
        }
    }

    @EventHandler
    public void handleDisableDamageWhileGrace(ProjectileHitEvent event) {
        if (Main.MINIGAME_ENGINE.getRoundState() == MiniGameEngine.MiniGameRoundState.GRACE) {
            event.setCancelled(true);
        }
    }
}
