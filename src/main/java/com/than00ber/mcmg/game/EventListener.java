package com.than00ber.mcmg.game;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class EventListener<G> implements Listener {

    public final G GAME;

    protected EventListener(G game) {
        GAME = game;
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }
}
