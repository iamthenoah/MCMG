package com.than00ber.mcmg.core;

import com.than00ber.mcmg.Main;
import org.bukkit.Bukkit;

public abstract class MiniGameHandler implements Runnable {

    private final Main instance;
    private int id;

    protected MiniGameHandler(Main instance) {
        this.instance = instance;
    }

    public void activate() {
        onActivate();
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, this, 0, 20);
    }

    public void deactivate() {
        onDeactivate();
        Bukkit.getScheduler().cancelTask(id);
    }

    @Override
    public abstract void run();

    public abstract void onActivate();

    public abstract void onDeactivate();
}
