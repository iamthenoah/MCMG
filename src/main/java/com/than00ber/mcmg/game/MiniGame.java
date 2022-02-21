package com.than00ber.mcmg.game;

import com.than00ber.mcmg.init.GameTeams;
import com.than00ber.mcmg.objects.GameTeam;
import com.than00ber.mcmg.objects.WinCondition;
import com.than00ber.mcmg.util.ChatUtil;
import com.than00ber.mcmg.util.config.ConfigProperty;
import com.than00ber.mcmg.util.config.Configurable;
import com.than00ber.mcmg.util.config.GameProperty;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public abstract class MiniGame implements GameLifeCycle, Configurable {

    protected final GameProperty.LocationProperty playgroundSpawn = new GameProperty.LocationProperty("playground.spawn");
    protected final GameProperty.IntegerProperty playgroundRadius = new GameProperty.IntegerProperty("playground.radius").validate(i -> i > 0);
    protected final GameProperty.IntegerProperty durationGrace = new GameProperty.IntegerProperty("duration.grace", 5).validate(i -> i > 0 && i < 86400);
    protected final GameProperty.IntegerProperty durationRound = new GameProperty.IntegerProperty("duration.round", 10).validate(i -> i > 0 && i < 84600);
    protected final GameProperty.IntegerProperty playerMinimum = new GameProperty.IntegerProperty("player.minimum", 1).validate(i -> i > 0 && i <= getParticipants().size());

    private final HashMap<Player, GameTeam> players;
    private final List<GameProperty<?>> properties;
    private EventListener<?> listener;
    private final World world;

    public MiniGame(World world) {
        properties = new ArrayList<>();
        addProperties(
                playgroundSpawn,
                playgroundRadius,
                durationGrace,
                playerMinimum,
                durationRound
        );
        getGameTeams().forEach(t -> properties.addAll(t.getProperties()));
        players = new HashMap<>();
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public HashMap<Player, GameTeam> getParticipants() {
        return players;
    }

    public EventListener<?> getEventListener() {
        return listener;
    }

    protected final void setEventListener(EventListener<?> listener) {
        this.listener = listener;
    }

    public boolean hasEventListener() {
        return listener != null;
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

    public final void switchTeam(Player player, GameTeam newTeam) {
        getParticipants().replace(player, newTeam);
        newTeam.prepare(player);
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
            public Integer getDurationGrace() {
                return durationGrace.get();
            }

            @Override
            public Integer getDurationRound() {
                return durationRound.get();
            }
        };
    }

    @Override
    public void onGameStarted() {
        getWorld().getWorldBorder().reset();
        getWorld().setThundering(false);
        getWorld().setStorm(false);
        getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        getWorld().setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        getWorld().setGameRule(GameRule.MOB_GRIEFING, false);
        getWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        getWorld().setGameRule(GameRule.DO_ENTITY_DROPS, false);
        getWorld().setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
        getWorld().setGameRule(GameRule.LOG_ADMIN_COMMANDS, false);
        getWorld().setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        getWorld().setGameRule(GameRule.KEEP_INVENTORY, true);

        // assigns random player roles
        assignRandomRoles();
        ChatUtil.showRoundStartScreen(getParticipants());
    }

    @Override
    public void onGameEnded() {
        getWorld().getWorldBorder().reset();
        getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        getWorld().setGameRule(GameRule.DO_WEATHER_CYCLE, true);
        getWorld().setGameRule(GameRule.MOB_GRIEFING, true);
        getWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, true);
        getWorld().setGameRule(GameRule.DO_ENTITY_DROPS, true);
        getWorld().setGameRule(GameRule.SHOW_DEATH_MESSAGES, true);
        getWorld().setGameRule(GameRule.LOG_ADMIN_COMMANDS, true);
        getWorld().setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, false);
        getWorld().setGameRule(GameRule.KEEP_INVENTORY, false);

        // reset players
        getParticipants().forEach((p, r) -> GameTeams.resetPlayer(p));
    }

    @Override
    public void onRoundWon(WinCondition<?> condition) {
        ChatUtil.showRoundEndScreen(getParticipants(), getGameTeams(), condition);
    }

    private void assignRandomRoles() {
        Random random = new Random();

        // TODO - add queuing system
        List<Player> queued = world.getPlayers();
        int total = queued.size();
        players.clear();

        do {
            int i = random.nextInt(getGameTeams().size());
            GameTeam team = getGameTeams().get(i);

            if (!team.isSpectator() && total >= team.getThreshold()) {
                int frequency = Collections.frequency(getParticipants().values(), team);

                if (frequency / (float) total <= team.getWeight()) {
                    Player player = queued.get(0);
                    players.put(player, team);
                    queued.remove(player);
                }
            }
        } while (!queued.isEmpty());

        // ensure required roles are present
        for (GameTeam team : getGameTeams()) {
            if (team.isRequired() && !getParticipants().containsValue(team)) {
                assignRandomRoles();
                break;
            }
        }

        // prepares every player per given role
        getParticipants().forEach((p, r) -> r.prepare(p));
    }

    public abstract String getGameName();

    public abstract List<GameTeam> getGameTeams();

    public abstract List<WinCondition> getWinConditions();
}
