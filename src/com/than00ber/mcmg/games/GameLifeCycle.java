package com.than00ber.mcmg.games;

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
     */
    void onRoundStarted();

    /**
     * Called when a round finishes.
     */
    void onRoundEnded();

    /**
     * Called when a round finishes along with the winning reason.
     * @param condition Win condition met.
     */
    void onRoundWon(WinCondition<?> condition);
}
