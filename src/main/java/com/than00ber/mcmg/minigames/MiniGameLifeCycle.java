package com.than00ber.mcmg.minigames;

import com.than00ber.mcmg.events.MiniGameEvent;
import com.than00ber.mcmg.objects.WinCondition;
import org.bukkit.entity.Player;

import java.util.List;

public interface MiniGameLifeCycle {
    /**
     * Called when the minigame has started.
     * Usually to set the environment.
     * @param participants Participating players.
     */
    void onMinigameStarted(List<Player> participants);

    /**
     * Called when the minigame has ended.
     * Usually to reset the environment.
     */
    void onMinigameEnded();

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
