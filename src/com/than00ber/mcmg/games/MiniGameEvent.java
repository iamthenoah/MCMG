package com.than00ber.mcmg.games;

import org.bukkit.World;
import org.bukkit.boss.BossBar;

public class MiniGameEvent {

    private final BossBar bossBar;
    private final World world;

    public MiniGameEvent(BossBar bossBar, World world) {
        this.bossBar = bossBar;
        this.world = world;
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public World getWorld() {
        return world;
    }

    /**
     * RoundStarted
     */
    public static class RoundStarted extends MiniGameEvent {

        public RoundStarted(BossBar bossBar, World world) {
            super(bossBar, world);
        }
    }

    /**
     * RoundCycled
     */
    public static class RoundCycled extends MiniGameEvent {

        private boolean hasEnded;

        public RoundCycled(BossBar bossBar, World world) {
            super(bossBar, world);
        }

        public boolean hasEnded() {
            return hasEnded;
        }

        public void setEnded(boolean ended) {
            hasEnded = ended;
        }
    }
}
