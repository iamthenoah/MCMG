package com.than00ber.mcmg.minigames;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.core.Registry;
import com.than00ber.mcmg.core.*;
import com.than00ber.mcmg.core.config.ConfigProperty;
import com.than00ber.mcmg.core.config.MiniGameProperty;
import com.than00ber.mcmg.events.MiniGameEvents;
import com.than00ber.mcmg.registries.Teams;
import com.than00ber.mcmg.util.ChatUtil;
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
import java.util.stream.Collectors;

public abstract class MiniGame implements Registry.Object, MiniGameLifeCycle {

    public static final MiniGameProperty.LocationProperty PLAYGROUND_SPAWN = new MiniGameProperty.LocationProperty("playground.spawn", Main.WORLD.getSpawnLocation());
    public static final MiniGameProperty.IntegerProperty PLAYGROUND_RADIUS = new MiniGameProperty.IntegerProperty("playground.radius", 100).validate(MiniGameProperty.IntegerProperty.POSITIVE);
    public static final MiniGameProperty.IntegerProperty DURATION_GRACE = new MiniGameProperty.IntegerProperty("duration.grace", 30).validate(i -> i > 0 && i < 86400);
    public static final MiniGameProperty.IntegerProperty DURATION_ROUND = new MiniGameProperty.IntegerProperty("duration.round", 120).validate(i -> i > 0 && i < 84600);
    public static final MiniGameProperty.IntegerProperty PLAYER_MINIMUM = new MiniGameProperty.IntegerProperty("player.minimum", 2).validate(i -> i > 1 && i <= Main.WORLD.getPlayers().size());

    protected final HashMap<Player, MiniGameTeam> originalPlayerRoles;
    protected final HashMap<Player, MiniGameTeam> currentPlayerRoles;
    private final List<MiniGameProperty<?>> properties;
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

    protected final void addProperties(MiniGameProperty<?>... props) {
        properties.addAll(List.of(props));
    }

    @Override
    public final ImmutableList<? extends ConfigProperty<?>> getProperties() {
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
                Console.warn("Current player team not registered " + previousMiniGameTeam.getVisibleName());
            }
            if (!newMiniGameTeam.isSpectator() && newTeam == null) {
                Console.warn("New player team not registered " + newMiniGameTeam.getVisibleName());
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
        prepareWorld(false);
        currentPlayerRoles.clear();
        originalPlayerRoles.clear();
        assignRandomRoles(participants);
        ChatUtil.showRoundStartScreen(getOriginalPlayerRoles());
        currentPlayerRoles.keySet().forEach(this::sendToGameSpawn);
    }

    @Override
    public void onMinigameEnded() {
        prepareWorld(true);
        currentPlayerRoles.clear();
        originalPlayerRoles.clear();
        getWorld().getPlayers().forEach(player -> {
            Teams.resetPlayer(player);
            sendToGameSpawn(player);
        });
    }

    @Override
    public void onMiniGameTick(MiniGameEvent event) { }

    @Override
    public void onRoundStarted(MiniGameEvent event) { }

    @Override
    public void onRoundWon(WinCondition<?> condition) {
        ChatUtil.showRoundEndScreen(getOriginalPlayerRoles(), getMiniGameTeams(), condition);
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

    private void prepareWorld(boolean isGameEnding) {
        clearMonsters();

        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, isGameEnding);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, isGameEnding);
        world.setGameRule(GameRule.MOB_GRIEFING, isGameEnding);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, isGameEnding);
        world.setGameRule(GameRule.DO_ENTITY_DROPS, isGameEnding);
        world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, isGameEnding);
        world.setGameRule(GameRule.LOG_ADMIN_COMMANDS, isGameEnding);
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, !isGameEnding);
        world.setGameRule(GameRule.KEEP_INVENTORY, !isGameEnding);

        world.setDifficulty(Difficulty.NORMAL);
        world.setThundering(false);
        world.setStorm(false);
        world.setTime(6000);
    }

    public final void sendToGameSpawn(Player player) {
        player.teleport(PLAYGROUND_SPAWN.get());
    }

    protected void clearMonsters() {
        for (Entity entity : getWorld().getEntities()) {
            if (entity instanceof Monster) entity.remove(); // TODO - add minigame util class
        }
    }

    public abstract ImmutableList<MiniGameTeam> getMiniGameTeams();

    public abstract ImmutableList<WinCondition> getWinConditions();
}
