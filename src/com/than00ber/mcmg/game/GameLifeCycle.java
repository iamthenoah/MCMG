package com.than00ber.mcmg.game;

import com.than00ber.mcmg.objects.WinCondition;

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
     * @param event MiniGameEvent event.
     */
    void onRoundStarted(MiniGameEvent event);

    /**
     * Called when a round has cycled.
     * @param event MiniGameEvent event.
     */
    void onRoundCycled(MiniGameEvent event);

    /**
     * Called when a round finishes along with the winning reason.
     * @param condition Win condition met.
     */
    void onRoundWon(WinCondition<?> condition);
}