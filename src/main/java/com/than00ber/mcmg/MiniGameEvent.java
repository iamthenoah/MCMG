package com.than00ber.mcmg;

import org.bukkit.boss.BossBar;

public class MiniGameEvent {

    private int tick;
    private WinCondition<?> winCondition;
    private final BossBar bossBar;

    public MiniGameEvent(BossBar bossBar) {
        this.bossBar = bossBar;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public int getCurrentTick() {
        return tick;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public boolean hasEnded() {
        return winCondition != null;
    }

    public WinCondition<?> getWinCondition() {
        return winCondition;
    }

    public void setWinCondition(WinCondition<?> condition) {
        winCondition = condition;
    }
}
