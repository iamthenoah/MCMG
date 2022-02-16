package com.than00ber.mcmg.game;

import org.bukkit.boss.BossBar;

public class MiniGameEvent {

    private boolean hasEnded;
    private final BossBar bossBar;

    public MiniGameEvent(BossBar bossBar) {
        this.bossBar = bossBar;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public boolean hasEnded() {
        return hasEnded;
    }

    public void setGameEnded() {
        hasEnded = true;
    }
}
