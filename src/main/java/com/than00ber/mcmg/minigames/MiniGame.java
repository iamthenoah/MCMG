package com.than00ber.mcmg.minigames;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.core.MiniGameEngine;
import com.than00ber.mcmg.core.MiniGameEvent;
import com.than00ber.mcmg.core.MiniGameTeam;
import com.than00ber.mcmg.core.WinCondition;
import com.than00ber.mcmg.core.configuration.Configurable;
import com.than00ber.mcmg.core.configuration.ConfigurableProperty;
import com.than00ber.mcmg.events.MiniGameEvents;
import com.than00ber.mcmg.registries.Teams;
import com.than00ber.mcmg.util.MiniGameUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.stream.Collectors;

public abstract class MiniGame implements Configurable, MiniGameLifeCycle {

    public static final ConfigurableProperty.LocationProperty PLAYGROUND_SPAWN = new ConfigurableProperty.LocationProperty("playground.spawn", Main.WORLD.getSpawnLocation());
    public static final ConfigurableProperty.IntegerProperty PLAYGROUND_RADIUS = new ConfigurableProperty.IntegerProperty("playground.radius", 100).verify(ConfigurableProperty.CheckIf.POSITIVE_INT);
    public static final ConfigurableProperty.IntegerProperty DURATION_GRACE = new ConfigurableProperty.IntegerProperty("duration.grace", 30).verify(ConfigurableProperty.CheckIf.LESS_THAN_A_DAY);
    public static final ConfigurableProperty.IntegerProperty DURATION_ROUND = new ConfigurableProperty.IntegerProperty("duration.round", 120).verify(ConfigurableProperty.CheckIf.LESS_THAN_A_DAY);
    public static final ConfigurableProperty.IntegerProperty PLAYER_MINIMUM = new ConfigurableProperty.IntegerProperty("player.minimum", 2).verify(ConfigurableProperty.CheckIf.LESS_THEN_PLAYERS);

    protected final HashMap<Player, MiniGameTeam> originalPlayerRoles;
    protected final HashMap<Player, MiniGameTeam> currentPlayerRoles;
    private final List<ConfigurableProperty<?>> properties;
    private MiniGameEvents<?> listener;
    private final World world;

    public MiniGame(World world) {
        this.world = world;
        currentPlayerRoles = new HashMap<>();
        originalPlayerRoles = new HashMap<>();
        properties = new ArrayList<>();
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

    public ImmutableMap<Player, MiniGameTeam> getCurrentPlayerRoles() {
        return ImmutableMap.copyOf(currentPlayerRoles);
    }

    public ImmutableMap<Player, MiniGameTeam> getOriginalPlayerRoles() {
        return ImmutableMap.copyOf(originalPlayerRoles);
    }

    public MiniGameEvents<?> getEventListener() {
        return listener;
    }

    protected final void setEventListener(MiniGameEvents<?> eventListener) {
        listener = eventListener;
    }

    public boolean hasEventListener() {
        return listener != null;
    }

    protected final void addProperties(ConfigurableProperty<?>... props) {
        properties.addAll(List.of(props));
    }

    @Override
    public final ImmutableList<ConfigurableProperty<?>> getProperties() {
        return ImmutableList.copyOf(properties);
    }

    public final boolean isParticipant(Player player) {
        return getCurrentPlayerRoles().containsKey(player);
    }

    public final boolean isInTeam(Player player, MiniGameTeam team) {
        return isParticipant(player) && Objects.equals(getCurrentPlayerRoles().get(player), team);
    }

    public final ImmutableList<Player> getAllInTeam(MiniGameTeam team) {
        Map<Player, MiniGameTeam> filtered = currentPlayerRoles.entrySet().stream()
                .filter(entry -> entry.getValue().equals(team))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return ImmutableList.copyOf(filtered.keySet());
    }

    public final void switchTeam(Player player, MiniGameTeam team) {
        team.prepare(player);
        addToScoreboardTeam(player, team);
        currentPlayerRoles.put(player, team);
    }

    public final void addPlayer(Player player, MiniGameTeam team) {
        originalPlayerRoles.put(player, team);
        switchTeam(player, team);
    }

    public final void removePlayer(Player player) {
        originalPlayerRoles.remove(player);
        currentPlayerRoles.remove(player);
    }

    private void addToScoreboardTeam(Player player, MiniGameTeam newMiniGameTeam) {
        MiniGameTeam previousMiniGameTeam = getCurrentPlayerRoles().get(player);
        if (previousMiniGameTeam == null) previousMiniGameTeam = Teams.SPECTATORS;
        ScoreboardManager manager = Bukkit.getScoreboardManager();

        if (manager != null) {
            Scoreboard scoreboard = manager.getMainScoreboard();
            Team currentTeam = scoreboard.getTeam(previousMiniGameTeam.getName());
            Team newTeam = scoreboard.getTeam(newMiniGameTeam.getName());

            if (!previousMiniGameTeam.isSpectator() && currentTeam == null) {
                Main.CONSOLE.warn("Current player team not registered " + previousMiniGameTeam.getVisibleName());
            }
            if (!newMiniGameTeam.isSpectator() && newTeam == null) {
                Main.CONSOLE.warn("New player team not registered " + newMiniGameTeam.getVisibleName());
            }

            if (currentTeam == null || newTeam == null) return;
            if (currentTeam.hasEntry(player.getDisplayName())) {
                currentTeam.removeEntry(player.getDisplayName());
            }

            newTeam.addEntry(player.getDisplayName());
        }
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
    public void onMinigameStarted(List<Player> participants) {
        MiniGameUtil.prepareWorld(false);
        currentPlayerRoles.clear();
        originalPlayerRoles.clear();
        assignRandomRoles(participants);
        MiniGameUtil.showRoundStartScreen(getOriginalPlayerRoles());
        currentPlayerRoles.keySet().forEach(MiniGameUtil::sendToGameSpawn);
    }

    @Override
    public void onMinigameEnded() {
        MiniGameUtil.prepareWorld(true);
        currentPlayerRoles.clear();
        originalPlayerRoles.clear();
        getWorld().getPlayers().forEach(player -> {
            MiniGameUtil.resetPlayer(player);
            MiniGameUtil.sendToGameSpawn(player);
        });
    }

    @Override
    public void onMiniGameTick(MiniGameEvent event) { }

    @Override
    public void onRoundStarted(MiniGameEvent event) { }

    @Override
    public void onRoundWon(WinCondition<?> condition) {
        MiniGameUtil.showRoundEndScreen(getOriginalPlayerRoles(), getMiniGameTeams(), condition);
    }

    private void assignRandomRoles(List<Player> participants) {
        Collections.shuffle(participants);
        int totalParticipants = participants.size();

        for (MiniGameTeam team : getMiniGameTeams()) {
            if (!team.isSpectator() && totalParticipants < team.getThreshold()) continue;
            int count = (int) Math.min(participants.size(), Math.ceil(team.getWeight() * totalParticipants));

            for (int i = 0; i < count; i++) {
                originalPlayerRoles.put(participants.remove(i), team);
            }
        }

//        originalPlayerRoles.clear();
//        List<Player> queued = new ArrayList<>(participants);
//        int total = queued.size();
//        Random random = new Random();
//
//        do {
//            int i = random.nextInt(getMiniGameTeams().size());
//            MiniGameTeam team = getMiniGameTeams().get(i);
//
//            if (!team.isSpectator() && total >= team.getThreshold()) {
//                int frequency = Collections.frequency(originalPlayerRoles.values(), team);
//
//                if (frequency / (float) total <= team.getWeight()) {
//                    Player player = queued.get(random.nextInt(queued.size()));
//                    originalPlayerRoles.put(player, team);
//                    queued.remove(player);
//                }
//            }
//        } while (!queued.isEmpty());
//
//        for (MiniGameTeam team : getMiniGameTeams()) {
//            if (team.isRequired() && !originalPlayerRoles.containsValue(team)) {
//                assignRandomRoles(queued);
//                break;
//            }
//        }

        currentPlayerRoles.putAll(originalPlayerRoles);
        originalPlayerRoles.forEach((player, team) -> {
            team.prepare(player);
            addToScoreboardTeam(player, team);

            if (team.disableWhileGrace()) {
                int duration = DURATION_GRACE.get() * 20;
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration + 20, 100));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, 10));
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, duration, 250));
            }
        });
    }

    public abstract ImmutableList<MiniGameTeam> getMiniGameTeams();

    public abstract ImmutableList<WinCondition> getWinConditions();
}
