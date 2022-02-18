package com.than00ber.mcmg.game;

import com.than00ber.mcmg.Main;
import org.bukkit.Bukkit;

public abstract class GameHandler implements Runnable {

    private final Main INSTANCE;
    private int ID;

    protected GameHandler(Main instance) {
        INSTANCE = instance;
    }

    public void activate() {
        onActivate();
        ID = Bukkit.getScheduler().scheduleSyncRepeatingTask(INSTANCE, this, 0, 20);
    }

    public void deactivate() {
        onDeactivate();
        Bukkit.getScheduler().cancelTask(ID);
    }

    @Override
    public abstract void run();

    public abstract void onActivate();

    public abstract void onDeactivate();
}
