package com.than00ber.mcmg.game;

import com.than00ber.mcmg.objects.GameTeam;
import com.than00ber.mcmg.objects.WinCondition;
import com.than00ber.mcmg.util.config.ConfigProperty;
import com.than00ber.mcmg.util.config.Configurable;
import com.than00ber.mcmg.util.config.GameProperty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class MiniGame implements GameLifeCycle, Configurable {

    protected final GameProperty.LocationProperty playgroundSpawn = new GameProperty.LocationProperty("playground.spawn");
    protected final GameProperty.IntegerProperty playgroundRadius = new GameProperty.IntegerProperty("playground.radius").validate(i -> i > 0);
    protected final GameProperty.IntegerProperty durationIdle = new GameProperty.IntegerProperty("duration.idle", 5).validate(i -> i > 0 && i < 86400);
    protected final GameProperty.IntegerProperty durationRound = new GameProperty.IntegerProperty("duration.round", 10).validate(i -> i > 0 && i < 84600);
    protected final GameProperty.IntegerProperty playerMinimum = new GameProperty.IntegerProperty("player.minimum", 1).validate(i -> i > 0 && i <= getCurrentPlayers().size());

    private final List<GameProperty<?>> properties;
    private EventListener<?> listener;
    private final World world;

    public MiniGame(World world) {
        properties = new ArrayList<>();
        properties.add(playgroundSpawn);
        properties.add(playgroundRadius);
        properties.add(durationIdle);
        properties.add(playerMinimum);
        properties.add(durationRound);
        getGameTeams().forEach(t -> properties.addAll(t.getProperties()));
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public EventListener<?> getEventListener() {
        return listener;
    }

    protected final void setEventListener(EventListener<?> listener) {
        this.listener = listener;
    }

    protected final void addProperties(GameProperty<?>... properties) {
        this.properties.addAll(List.of(properties));
    }

    @Override
    public final String getConfigName() {
        return getGameName().toLowerCase() + "-latest.lock";
    }

    @Override
    public final List<? extends ConfigProperty<?>> getProperties() {
        return properties;
    }

    public GameEngine.Options getOptions() {
        return new GameEngine.Options() {
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
            public Integer getDurationIdle() {
                return durationIdle.get();
            }

            @Override
            public Integer getDurationRound() {
                return durationRound.get();
            }
        };
    }

    public abstract String getGameName();

    public abstract HashMap<Player, GameTeam> getCurrentPlayers();

    public abstract List<GameTeam> getGameTeams();

    public abstract List<WinCondition> getWinConditions();
}
