package com.than00ber.mcmg.game.events;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.game.MiniGame;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class EventListener<G extends MiniGame> implements Listener {

    public final G game;
    private final Main instance;

    protected EventListener(Main instance, G game) {
        this.instance = instance;
        this.game = game;
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(game.getEventListener(), instance);
    }
}
