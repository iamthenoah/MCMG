package com.than00ber.mcmg.init;

import com.than00ber.mcmg.games.MiniGame;
import com.than00ber.mcmg.games.WinCondition;

public class WinConditions {

    public static final WinCondition<MiniGame> EVERYONE_DEAD = new WinCondition.Builder<MiniGame>()
            .setLoseReason("Nobody survived.")
            .setCondition(state -> state.getCurrentPlayers().isEmpty())
            .build();
}
