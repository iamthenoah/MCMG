package com.than00ber.mcmg.games;

import org.bukkit.entity.Player;

import java.util.List;

public abstract class MiniGame implements GameLifeCycle {

    public abstract String getGameName();

    protected abstract GameEngine.Options getOptions();

    public abstract List<Player> getCurrentPlayers();

    public abstract List<GameTeam> getGameTeams();

    public abstract List<WinCondition<?>> getWinConditions();
}
