package com.than00ber.mcmg.games.example;

import com.than00ber.mcmg.games.GameEngine;
import com.than00ber.mcmg.games.GameTeam;
import com.than00ber.mcmg.games.MiniGame;
import com.than00ber.mcmg.games.WinCondition;
import com.than00ber.mcmg.init.GameTeams;
import com.than00ber.mcmg.init.WinConditions;
import com.than00ber.mcmg.util.Console;
import com.than00ber.mcmg.util.config.ConfigProperty;
import com.than00ber.mcmg.util.config.Configurable;
import com.than00ber.mcmg.util.config.GameProperty;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExampleGame extends MiniGame implements Configurable<ExampleGame> {

    protected final GameProperty.LocationProperty playgroundSpawn = new GameProperty.LocationProperty("playground.spawn");
    protected final GameProperty.IntegerProperty playgroundRadius = new GameProperty.IntegerProperty("playground.radius").validate(i -> i > 0);
    protected final GameProperty.IntegerProperty idleDuration = new GameProperty.IntegerProperty("idle.duration", 30).validate(i -> i > 0 && i < 86400);
    protected final GameProperty.IntegerProperty playerMinimum = new GameProperty.IntegerProperty("player.minimum", 1).validate(i -> i > 0 && i <= Bukkit.getServer().getMaxPlayers());
    protected final GameProperty.IntegerProperty durationRound = new GameProperty.IntegerProperty("round.duration", 120).validate(i -> i > 0 && i < 84600);
    protected final GameProperty.IntegerProperty countRound = new GameProperty.IntegerProperty("round.count", 1).validate(i -> i > 0);

    @Override
    public String getGameName() {
        return "ExampleGame";
    }

    @Override
    public GameEngine.Options getOptions() {
        return new GameEngine.Options() {
            @Override
            public Integer getIdleDuration() {
                return idleDuration.get();
            }

            @Override
            public Integer getMinimumPlayer() {
                return playerMinimum.get();
            }

            @Override
            public List<Location> getSpawnLocations() {
                return List.of(playgroundSpawn.get());
            }

            @Override
            public Integer getPlaygroundRadius() {
                return playgroundRadius.get();
            }

            @Override
            public Integer getRoundDuration() {
                return durationRound.get();
            }

            @Override
            public Integer getRoundCount() {
                return countRound.get();
            }
        };
    }

    @Override
    public List<Player> getCurrentPlayers() {
        return new ArrayList<>(Bukkit.getServer().getOnlinePlayers());
    }

    @Override
    public List<GameTeam> getGameTeams() {
        return List.of(
                GameTeams.VILLAGER,
                GameTeams.WEREWOLF,
                GameTeams.TRAITOR,
                GameTeams.VAMPIRE,
                GameTeams.POSSESSED
        );
    }

    @Override
    public List<WinCondition<?>> getWinConditions() {
        return List.of(
                WinConditions.EVERYONE_DEAD
        );
    }

    @Override
    public String getConfigName() {
        return getGameName().toLowerCase() + "-latest.lock";
    }

    @Override
    public List<? extends ConfigProperty<?>> getProperties() {
        return Arrays.asList(
                playgroundSpawn,
                playgroundRadius,
                playerMinimum,
                durationRound,
                idleDuration
        );
    }

    @Override
    public void onGameStarted() {
        Console.debug("ExampleGame#onGameStarted");
    }

    @Override
    public void onGameEnded() {
        Console.debug("ExampleGame#onGameEnded");
    }

    @Override
    public void onRoundStarted() {
        Console.debug("ExampleGame#onRoundStarted");
    }

    @Override
    public void onRoundEnded() {
        Console.debug("ExampleGame#onRoundEnded");
    }

    @Override
    public void onRoundWon(WinCondition<?> condition) {
        Console.debug("ExampleGame#onRoundWon");
    }
}
