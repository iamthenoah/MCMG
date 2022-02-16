package com.than00ber.mcmg.game;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class MiniGameEventListener<G> implements Listener {

    public final G GAME;

    protected MiniGameEventListener(G game) {
        GAME = game;
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }
}
