package com.than00ber.mcmg.game;

import com.than00ber.mcmg.objects.GameTeam;
import com.than00ber.mcmg.objects.WinCondition;
import com.than00ber.mcmg.util.config.ConfigProperty;
import com.than00ber.mcmg.util.config.Configurable;
import com.than00ber.mcmg.util.config.GameProperty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public abstract class MiniGame implements GameLifeCycle, Configurable {

    protected final GameProperty.LocationProperty playgroundSpawn = new GameProperty.LocationProperty("playground.spawn");
    protected final GameProperty.IntegerProperty playgroundRadius = new GameProperty.IntegerProperty("playground.radius").validate(i -> i > 0);
    protected final GameProperty.IntegerProperty durationIdle = new GameProperty.IntegerProperty("duration.idle", 5).validate(i -> i > 0 && i < 86400);
    protected final GameProperty.IntegerProperty durationRound = new GameProperty.IntegerProperty("duration.round", 10).validate(i -> i > 0 && i < 84600);
    protected final GameProperty.IntegerProperty playerMinimum = new GameProperty.IntegerProperty("player.minimum", 1).validate(i -> i > 0 && i <= getPlayers().size());

    private final HashMap<Player, GameTeam> players;
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
        players = new HashMap<>();
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public HashMap<Player, GameTeam> getPlayers() {
        return players;
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

    protected void assignRandomRoles() {
        Random random = new Random();

        // TODO - add queuing system
        List<Player> queued = world.getPlayers();
        int total = queued.size();
        players.clear();

        do {
            int i = random.nextInt(getGameTeams().size());
            GameTeam team = getGameTeams().get(i);

            if (!team.isSpectator() && total >= team.getThreshold()) {
                int frequency = Collections.frequency(getPlayers().values(), team);

                if (frequency / (float) total <= team.getWeight()) {
                    Player player = queued.get(0);
                    players.put(player, team);
                    queued.remove(player);
                }
            }
        } while (!queued.isEmpty());
    }

    public abstract String getGameName();

    public abstract List<GameTeam> getGameTeams();

    public abstract List<WinCondition> getWinConditions();
}
