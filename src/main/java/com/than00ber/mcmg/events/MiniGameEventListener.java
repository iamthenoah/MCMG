package com.than00ber.mcmg.events;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.minigames.MiniGame;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class MiniGameEventListener<G extends MiniGame> implements Listener {

    protected final G minigame;
    protected final Main instance;

    protected MiniGameEventListener(Main instance, G game) {
        this.instance = instance;
        this.minigame = game;
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(minigame.getEventListener(), instance);
    }
}
