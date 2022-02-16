package com.than00ber.mcmg.game;

import com.than00ber.mcmg.objects.WinCondition;
import org.bukkit.boss.BossBar;

public interface GameLifeCycle {
    /**
     * Called when the game has started.
     * Usually to set the environment.
     */
    void onGameStarted();

    /**
     * Called when the game has ended.
     * Usually to reset the environment.
     */
    void onGameEnded();

    /**
     * Called when a round begins.
     * Usually to assign player roles.
     * @param bar Progress bar object.
     */
    void onRoundStarted(BossBar bar);

    /**
     * Called when a round has cycled.
     * @param bar Progress bar object.
     */
    void onRoundCycled(BossBar bar);

    /**
     * Called when a round finishes along with the winning reason.
     * @param condition Win condition met.
     */
    void onRoundWon(WinCondition<?> condition);
}
