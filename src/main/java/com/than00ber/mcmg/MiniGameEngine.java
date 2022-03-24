package com.than00ber.mcmg;

import com.than00ber.mcmg.events.MiniGameEventListener;
import com.than00ber.mcmg.init.MiniGameTeams;
import com.than00ber.mcmg.minigames.MiniGame;
import com.than00ber.mcmg.objects.MiniGameTeam;
import com.than00ber.mcmg.objects.WinCondition;
import com.than00ber.mcmg.util.ActionResult;
import com.than00ber.mcmg.util.ChatUtil;
import com.than00ber.mcmg.util.TextUtil;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class MiniGameEngine<G extends MiniGame> {

    private final Main instance;
    private G minigame;
    private Supplier<MiniGameHandler> handlerSupplier;
    private MiniGameHandler currentHandler;
    private GameState gameState;

    public MiniGameEngine(Main instance) {
        this.instance = instance;
        gameState = GameState.EMPTY;
    }

    public G getCurrentGame() {
        return minigame;
    }

    public boolean hasGame() {
        return minigame != null;
    }

    public boolean hasIdleGame() {
        return gameState.equals(GameState.IDLE);
    }

    public boolean hasRunningGame() {
        return gameState.equals(GameState.ONGOING);
    }

    public ActionResult mount(G nextGame) {
        if (hasRunningGame()) {
            return ActionResult.failure("Cannot mount a minigame while a minigame is running");
        }

        if (minigame != null && minigame.hasEventListener()) {
            minigame.getEventListener().unregister();
        }

        handlerSupplier = () -> new MiniGameHandler(instance) {

            private MiniGameEvent event;
            private int countdownGrace;
            private int countdownRound;

            @Override
            public void onActivate() {
                countdownGrace = minigame.getOptions().getDurationGrace();
                countdownRound = minigame.getOptions().getDurationRound();
                BossBar bar = Bukkit.createBossBar(null, BarColor.WHITE, BarStyle.SEGMENTED_10);
                minigame.getWorld().getPlayers().forEach(bar::addPlayer);
                event = new MiniGameEvent(bar);
            }

            @Override
            public void onDeactivate() {
                event.getBossBar().removeAll();
            }

            @Override
            public void run() {
                if (countdownGrace > 0) {
                    if (countdownGrace <= 3) {
                        minigame.getWorld().getPlayers().forEach(p -> {
                            Note note = Note.natural(1, Note.Tone.A);
                            p.playNote(p.getLocation(), Instrument.XYLOPHONE, note);
                        });
                    }

                    countdownGrace--;
                    event.getBossBar().setProgress((float) countdownGrace / minigame.getOptions().getDurationGrace());
                    event.getBossBar().setTitle("Minigame starting in " + countdownGrace + " seconds");

                    if (countdownGrace == 0) {
                        event.getBossBar().setTitle("");
                        event.getBossBar().setProgress(1);
                        minigame.onRoundStarted(event);

                        if (event.getBossBar().getTitle().isEmpty()) {
                            event.getBossBar().setTitle("Time Remaining");
                        }
                    }
                } else {
                    WinCondition<?> condition =  minigame.getWinConditions().stream()
                            .filter(c -> c.check(minigame)).findAny().orElse(null);

                    if (condition != null) {
                        minigame.onRoundWon(condition);
                        endMiniGame(null);
                    } else if (countdownRound == 0) {
                        countdownRound = minigame.getOptions().getDurationRound();
                        minigame.onRoundCycled(event);

                        if (event.getBossBar().getTitle().isEmpty()) {
                            event.getBossBar().setTitle("Time Remaining");
                        }

                        if (event.hasEnded()) {
                            minigame.onRoundWon(event.getWinCondition());
                            endMiniGame(null);
                        }
                    } else {
                        countdownRound--;
                        event.getBossBar().setProgress((float) countdownRound / minigame.getOptions().getDurationRound());
                    }
                }
            }
        };

        minigame = nextGame;
        gameState = GameState.IDLE;
        return ActionResult.success();
    }

    public ActionResult startMiniGame(List<Player> participants, @Nullable String message) {
        ActionResult invalid = validateOptions(participants);
        if (!invalid.isSuccessful()) return invalid;

        minigame.getWorld().getWorldBorder().setSize(minigame.getOptions().getPlaygroundRadius());
        minigame.getWorld().getWorldBorder().setCenter(minigame.getOptions().getPlaygroundSpawn());

        unregisterTeams();
        registerTeams();
        minigame.onMinigameStarted(participants);
        Optional.ofNullable(minigame.getEventListener()).ifPresent(MiniGameEventListener::register);
        for (Player player : minigame.getWorld().getPlayers()) {
            if (!participants.contains(player)) MiniGameTeams.SPECTATORS.prepare(player);
        }

        currentHandler = handlerSupplier.get();
        currentHandler.activate();

        gameState = GameState.ONGOING;
        return ActionResult.success(message);
    }

    public ActionResult endMiniGame(@Nullable String reason) {
        if (currentHandler == null || minigame == null) {
            return ActionResult.warn("No minigame is currently running.");
        }

        minigame.getWorld().getWorldBorder().reset();

        minigame.onMinigameEnded();
        unregisterTeams();
        Optional.ofNullable(minigame.getEventListener()).ifPresent(MiniGameEventListener::unregister);

        currentHandler.deactivate();
        currentHandler = null;
        handlerSupplier = null;
        minigame = null;

        gameState = GameState.EMPTY;
        return ActionResult.success(reason);
    }

    public ActionResult restartMiniGame(@Nullable String reason) {
        currentHandler.deactivate();
        ActionResult startResult = startMiniGame(minigame.getParticipants().keySet().asList(), null);
        return !startResult.isSuccessful() ? startResult : ActionResult.success(reason);
    }

    private void registerTeams() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();

        if (manager != null) {
            Scoreboard scoreboard = manager.getMainScoreboard();

            for (MiniGameTeam miniGameTeam : minigame.getMiniGameTeams()) {
                Team team = scoreboard.registerNewTeam(miniGameTeam.getTeamId());
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, miniGameTeam.getVisibility());
            }
        }
    }

    private void unregisterTeams() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager != null) {
            Scoreboard scoreboard = manager.getMainScoreboard();
            scoreboard.getTeams().forEach(Team::unregister);
        }
    }

    private ActionResult validateOptions(List<Player> participants) {
        if (minigame == null) {
            return ActionResult.warn("No minigame is set.");
        }
        String name = TextUtil.formatMiniGame(minigame);
        if (currentHandler != null) {
            return ActionResult.warn("A minigame of " + name + " is already running.");
        }
        if (minigame.getOptions().getPlaygroundSpawn() == null) {
            return ActionResult.warn("No playground spawn set for minigame " + name);
        }
        if (minigame.getOptions().getPlaygroundRadius() == null) {
            return ActionResult.warn("No playground radius set for minigame " + name);
        }
        int minimum = minigame.getOptions().getMinimumPlayer();
        if (minimum > participants.size()) {
            String reason = ChatColor.GOLD + "(" + participants.size() + " of " + minimum + ")";
            return ActionResult.warn("Not enough participants to play " + name + " " + reason);
        }
        return ActionResult.success();
    }

    private abstract static class MiniGameHandler implements Runnable {

        private final Main instance;
        private int id;

        protected MiniGameHandler(Main instance) {
            this.instance = instance;
        }

        public void activate() {
            onActivate();
            id = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, this, 0, 20);
        }

        public void deactivate() {
            onDeactivate();
            Bukkit.getScheduler().cancelTask(id);
        }

        @Override
        public abstract void run();

        public abstract void onActivate();

        public abstract void onDeactivate();
    }

    public enum GameState {
        IDLE, ONGOING, EMPTY
    }

    public interface Options {

        Integer getMinimumPlayer();

        Location getPlaygroundSpawn();

        Integer getPlaygroundRadius();

        Integer getDurationGrace();

        Integer getDurationRound();
    }
}
