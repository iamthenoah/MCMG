package com.than00ber.mcmg.game;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.game.events.MiniGameEventListener;
import com.than00ber.mcmg.init.MiniGameTeams;
import com.than00ber.mcmg.objects.MiniGameTeam;
import com.than00ber.mcmg.objects.WinCondition;
import com.than00ber.mcmg.util.ChatUtil;
import com.than00ber.mcmg.util.Console;
import com.than00ber.mcmg.util.config.ConfigProperty;
import com.than00ber.mcmg.util.config.Configurable;
import com.than00ber.mcmg.util.config.MiniGameProperty;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.*;

public abstract class MiniGame implements MiniGameLifeCycle, Configurable {

    public static final MiniGameProperty.LocationProperty PLAYGROUND_SPAWN = new MiniGameProperty.LocationProperty("playground.spawn");
    public static final MiniGameProperty.IntegerProperty PLAYGROUND_RADIUS = new MiniGameProperty.IntegerProperty("playground.radius").validate(i -> i > 0);
    public static final MiniGameProperty.IntegerProperty DURATION_GRACE = new MiniGameProperty.IntegerProperty("duration.grace", 30).validate(i -> i > 0 && i < 86400);
    public static final MiniGameProperty.IntegerProperty DURATION_ROUND = new MiniGameProperty.IntegerProperty("duration.round", 120).validate(i -> i > 0 && i < 84600);
    public static final MiniGameProperty.IntegerProperty PLAYER_MINIMUM = new MiniGameProperty.IntegerProperty("player.minimum", 1).validate(i -> i > 0 && i <= Main.MINIGAME_ENGINE.getCurrentGame().getParticipants().size());

    protected final HashMap<Player, MiniGameTeam> players;
    private final List<MiniGameProperty<?>> properties;
    private final World world;
    private MiniGameEventListener<?> listener;

    public MiniGame(World world) {
        this.world = world;
        players = new HashMap<>();
        properties = new ArrayList<>();
        getGameTeams().forEach(t -> properties.addAll(t.getProperties()));
        addProperties(
                PLAYGROUND_SPAWN,
                PLAYGROUND_RADIUS,
                DURATION_GRACE,
                PLAYER_MINIMUM,
                DURATION_ROUND
        );
    }

    public World getWorld() {
        return world;
    }

    public ImmutableMap<Player, MiniGameTeam> getParticipants() {
        return ImmutableMap.copyOf(players);
    }

    public MiniGameEventListener<?> getEventListener() {
        return listener;
    }

    protected final void setEventListener(MiniGameEventListener<?> eventListener) {
        listener = eventListener;
    }

    public boolean hasEventListener() {
        return listener != null;
    }

    protected final void addProperties(MiniGameProperty<?>... props) {
        properties.addAll(List.of(props));
    }

    @Override
    public final String getConfigName() {
        return getGameName().toLowerCase() + "-latest";
    }

    @Override
    public final ImmutableList<? extends ConfigProperty<?>> getProperties() {
        return ImmutableList.copyOf(properties);
    }

    public final boolean isParticipant(Player player) {
        return players.containsKey(player);
    }

    public final boolean isInTeam(Player player, MiniGameTeam team) {
        return isParticipant(player) && players.get(player).equals(team);
    }

    public final void switchTeam(Player player, MiniGameTeam newMiniGameTeam) {
        if (isParticipant(player)) {
            addToScoreboardTeam(player, newMiniGameTeam);
            players.replace(player, newMiniGameTeam);
            newMiniGameTeam.prepare(player);
        }
    }

    public final void removePlayer(Player player) {
        if (isParticipant(player)) {
            players.remove(player);
        }
    }

    private void addToScoreboardTeam(Player player, MiniGameTeam newMiniGameTeam) {
        MiniGameTeam currentMiniGameTeam = players.get(player);
        ScoreboardManager manager = Bukkit.getScoreboardManager();

        if (manager != null) {
            Scoreboard scoreboard = manager.getMainScoreboard();
            Team currentTeam = scoreboard.getTeam(currentMiniGameTeam.getTeamId());
            Team newTeam = scoreboard.getTeam(newMiniGameTeam.getTeamId());

            if (currentTeam == null || newTeam == null) {
                Console.warn("A team is not registered.");
                return;
            }

            if (currentTeam.hasEntry(player.getDisplayName())) {
                currentTeam.removeEntry(player.getDisplayName());
            }

            newTeam.addEntry(player.getDisplayName());
        }
    }

    public final void sendToGameSpawn(Player player) {
        player.teleport(PLAYGROUND_SPAWN.get());
    }

    public MiniGameEngine.Options getOptions() {
        return new MiniGameEngine.Options() {
            @Override
            public Integer getMinimumPlayer() {
                return PLAYER_MINIMUM.get();
            }

            @Override
            public Location getPlaygroundSpawn() {
                return PLAYGROUND_SPAWN.get();
            }

            @Override
            public Integer getPlaygroundRadius() {
                return PLAYGROUND_RADIUS.get();
            }

            @Override
            public Integer getDurationGrace() {
                return DURATION_GRACE.get();
            }

            @Override
            public Integer getDurationRound() {
                return DURATION_ROUND.get();
            }
        };
    }

    @Override
    public void onMinigameStarted() {
        clearMonsters();

        getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        getWorld().setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        getWorld().setGameRule(GameRule.MOB_GRIEFING, false);
        getWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        getWorld().setGameRule(GameRule.DO_ENTITY_DROPS, false);
        getWorld().setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
        getWorld().setGameRule(GameRule.LOG_ADMIN_COMMANDS, false);
        getWorld().setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        getWorld().setGameRule(GameRule.KEEP_INVENTORY, true);

        getWorld().setDifficulty(Difficulty.NORMAL);
        getWorld().setThundering(false);
        getWorld().setStorm(false);
        getWorld().setTime(6000);

        assignRandomRoles();
        ChatUtil.showRoundStartScreen(getParticipants());
        players.keySet().forEach(this::sendToGameSpawn);
    }

    @Override
    public void onMinigameEnded() {
        clearMonsters();

        getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        getWorld().setGameRule(GameRule.DO_WEATHER_CYCLE, true);
        getWorld().setGameRule(GameRule.MOB_GRIEFING, true);
        getWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, true);
        getWorld().setGameRule(GameRule.DO_ENTITY_DROPS, true);
        getWorld().setGameRule(GameRule.SHOW_DEATH_MESSAGES, true);
        getWorld().setGameRule(GameRule.LOG_ADMIN_COMMANDS, true);
        getWorld().setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, false);
        getWorld().setGameRule(GameRule.KEEP_INVENTORY, false);

        getWorld().setDifficulty(Difficulty.NORMAL);
        getWorld().setThundering(false);
        getWorld().setStorm(false);
        getWorld().setTime(6000);

        getWorld().getPlayers().forEach(player -> {
            MiniGameTeams.resetPlayer(player);
            sendToGameSpawn(player);
        });
    }

    @Override
    public void onRoundWon(WinCondition<?> condition) {
        ChatUtil.showRoundEndScreen(getParticipants(), getGameTeams(), condition);
    }

    private void assignRandomRoles() {
        players.clear();
        List<Player> queued = world.getPlayers(); // TODO - add queuing system
        int total = queued.size();
        Random random = new Random();

        do {
            int i = random.nextInt(getGameTeams().size());
            MiniGameTeam team = getGameTeams().get(i);

            if (!team.isSpectator() && total >= team.getThreshold()) {
                int frequency = Collections.frequency(players.values(), team);

                if (frequency / (float) total <= team.getWeight()) {
                    Player player = queued.get(random.nextInt(queued.size()));
                    players.put(player, team);
                    queued.remove(player);
                }
            }
        } while (!queued.isEmpty());

        for (MiniGameTeam team : getGameTeams()) {
            if (team.isRequired() && !players.containsValue(team)) {
                assignRandomRoles();
                break;
            }
        }

        players.forEach((p, t) -> {
            t.prepare(p);
            addToScoreboardTeam(p, t);

            if (t.disableWhileGrace()) {
                int duration = DURATION_GRACE.get() * 20;
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration, 100));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, 10));
                p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, duration, 250));
            }
        });
    }

    protected void clearMonsters() {
        for (Entity entity : getWorld().getEntities()) {
            if (entity instanceof Monster) entity.remove(); // TODO - add minigame util class
        }
    }

    public abstract String getGameName();

    public abstract ImmutableList<MiniGameTeam> getGameTeams();

    public abstract ImmutableList<WinCondition> getWinConditions();
}
