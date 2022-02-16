package com.than00ber.mcmg.games;

import com.than00ber.mcmg.util.config.ConfigProperty;
import com.than00ber.mcmg.util.config.Configurable;
import com.than00ber.mcmg.util.config.GameProperty;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class MiniGame implements GameLifeCycle, Configurable {

    protected final GameProperty.LocationProperty playgroundSpawn = new GameProperty.LocationProperty("playground.spawn");
    protected final GameProperty.IntegerProperty playgroundRadius = new GameProperty.IntegerProperty("playground.radius").validate(i -> i > 0);
    protected final GameProperty.IntegerProperty idleDuration = new GameProperty.IntegerProperty("idle.duration", 5).validate(i -> i > 0 && i < 86400);
    protected final GameProperty.IntegerProperty playerMinimum = new GameProperty.IntegerProperty("player.minimum", 1).validate(i -> i > 0 && i <= getCurrentPlayers().size());
    protected final GameProperty.IntegerProperty roundDuration = new GameProperty.IntegerProperty("round.duration", 10).validate(i -> i > 0 && i < 84600);
    protected final GameProperty.IntegerProperty roundCount = new GameProperty.IntegerProperty("round.count", 1).validate(i -> i > 0);

    @Override
    public String getConfigName() {
        return getGameName().toLowerCase() + "-latest.lock";
    }

    @Override
    public List<? extends ConfigProperty<?>> getProperties() {
        List<ConfigProperty<?>> properties = new ArrayList<>();
        getGameTeams().forEach(t -> properties.addAll(t.getProperties()));
        properties.add(playgroundSpawn);
        properties.add(playgroundRadius);
        properties.add(idleDuration);
        properties.add(playerMinimum);
        properties.add(roundDuration);
        properties.add(roundCount);
        return properties;
    }

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
                return roundDuration.get();
            }

            @Override
            public Integer getRoundCount() {
                return roundCount.get();
            }
        };
    }

    public abstract String getGameName();

    public abstract HashMap<Player, GameTeam> getCurrentPlayers();

    public abstract List<GameTeam> getGameTeams();

    public abstract List<WinCondition> getWinConditions();
}
