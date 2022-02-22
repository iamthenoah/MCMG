package com.than00ber.mcmg.game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.than00ber.mcmg.game.events.PluginEventListener;
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

    protected final HashMap<Player, GameTeam> players;
    private final List<GameProperty<?>> properties;
    private final World world;
    private PluginEventListener<?> listener;

    public MiniGame(World world) {
        this.world = world;
        players = new HashMap<>();
        properties = new ArrayList<>();
        getGameTeams().forEach(t -> properties.addAll(t.getProperties()));
        addProperties(
                playgroundSpawn,
                playgroundRadius,
                durationGrace,
                playerMinimum,
                durationRound
        );
    }

    public World getWorld() {
        return world;
    }

    public ImmutableMap<Player, GameTeam> getParticipants() {
        return ImmutableMap.copyOf(players);
    }

    public PluginEventListener<?> getEventListener() {
        return listener;
    }

    protected final void setEventListener(PluginEventListener<?> listener) {
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
    public final ImmutableList<? extends ConfigProperty<?>> getProperties() {
        return ImmutableList.copyOf(properties);
    }

    public final void switchTeam(Player player, GameTeam newTeam) {
        players.replace(player, newTeam);
        newTeam.prepare(player);
    }

    public GameEngine.Options getOptions() {
        return new GameEngine.Options() {
            @Override
            public Integer getMinimumPlayer() {
                return playerMinimum.get();
            }

            @Override
            public Location getPlaygroundSpawn() {
                return playgroundSpawn.get();
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
        getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        getWorld().setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        getWorld().setGameRule(GameRule.MOB_GRIEFING, false);
        getWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        getWorld().setGameRule(GameRule.DO_ENTITY_DROPS, false);
        getWorld().setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
        getWorld().setGameRule(GameRule.LOG_ADMIN_COMMANDS, false);
        getWorld().setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        getWorld().setGameRule(GameRule.KEEP_INVENTORY, true);

        getWorld().setThundering(false);
        getWorld().setStorm(false);
        getWorld().setTime(6000);

        assignRandomRoles();
        ChatUtil.showRoundStartScreen(getParticipants());
        players.keySet().forEach(p -> p.teleport(playgroundSpawn.get().add(0, 1, 0)));
    }

    @Override
    public void onGameEnded() {
        getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        getWorld().setGameRule(GameRule.DO_WEATHER_CYCLE, true);
        getWorld().setGameRule(GameRule.MOB_GRIEFING, true);
        getWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, true);
        getWorld().setGameRule(GameRule.DO_ENTITY_DROPS, true);
        getWorld().setGameRule(GameRule.SHOW_DEATH_MESSAGES, true);
        getWorld().setGameRule(GameRule.LOG_ADMIN_COMMANDS, true);
        getWorld().setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, false);
        getWorld().setGameRule(GameRule.KEEP_INVENTORY, false);

        players.keySet().forEach(GameTeams::resetPlayer);
        players.keySet().forEach(p -> p.teleport(playgroundSpawn.get()));
    }

    @Override
    public void onRoundWon(WinCondition<?> condition) {
        ChatUtil.showRoundEndScreen(getParticipants(), getGameTeams(), condition);
    }

    private void assignRandomRoles() {
        Random random = new Random();

        List<Player> queued = world.getPlayers(); // TODO - add queuing system
        int total = queued.size();
        players.clear();

        do {
            int i = random.nextInt(getGameTeams().size());
            GameTeam team = getGameTeams().get(i);

            if (!team.isSpectator() && total >= team.getThreshold()) {
                int frequency = Collections.frequency(players.values(), team);

                if (frequency / (float) total <= team.getWeight()) {
                    Player player = queued.get(0);
                    players.put(player, team);
                    queued.remove(player);
                }
            }
        } while (!queued.isEmpty());

        for (GameTeam team : getGameTeams()) {
            if (team.isRequired() && !players.containsValue(team)) {
                assignRandomRoles();
                break;
            }
        }

        players.forEach((p, r) -> r.prepare(p));
    }

    public abstract String getGameName();

    public abstract ImmutableList<GameTeam> getGameTeams();

    public abstract ImmutableList<WinCondition> getWinConditions();
}
