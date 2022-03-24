package com.than00ber.mcmg.game;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.objects.MiniGameTeam;
import com.than00ber.mcmg.objects.WinCondition;
import com.than00ber.mcmg.util.ActionResult;
import org.bukkit.Bukkit;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Note;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Nullable;

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

        minigame = nextGame;

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
                    if (countdownRound == 0) {
                        countdownRound = minigame.getOptions().getDurationRound();
                        minigame.onRoundCycled(event);

                        if (event.getBossBar().getTitle().isEmpty()) {
                            event.getBossBar().setTitle("Time Remaining");
                        }

                        if (event.hasEnded()) {
                            minigame.onRoundWon(event.getWinCondition());
                            endGame(null);
                        }
                    }

                    WinCondition<?> condition =  minigame.getWinConditions().stream()
                            .filter(c -> c.check(minigame)).findAny().orElse(null);

                    if (condition != null) {
                        minigame.onRoundWon(condition);
                        endGame(null);
                    }

                    countdownRound--;
                    event.getBossBar().setProgress((float) countdownRound / minigame.getOptions().getDurationRound());
                }

            }
        };

        gameState = GameState.IDLE;
        return ActionResult.success();
    }

    public ActionResult startGame(@Nullable String message) {
        if (minigame == null) {
            return ActionResult.warn("No minigame is set.");
        }
        if (currentHandler != null) {
            return ActionResult.warn("A minigame of " + minigame.getGameName() + " is already running.");
        }
        if (minigame.getOptions().getPlaygroundSpawn() == null) {
            return ActionResult.warn("No playground spawn set for minigame " + minigame.getGameName());
        }
        if (minigame.getOptions().getPlaygroundRadius() == null) {
            return ActionResult.warn("No playground radius set for minigame " + minigame.getGameName());
        }

        minigame.getWorld().getWorldBorder().setSize(minigame.getOptions().getPlaygroundRadius());
        minigame.getWorld().getWorldBorder().setCenter(minigame.getOptions().getPlaygroundSpawn());

        unregisterTeams();
        registerTeams();
        minigame.onMinigameStarted();
        if (minigame.hasEventListener()) {
            minigame.getEventListener().register();
        }

        currentHandler = handlerSupplier.get();
        currentHandler.activate();

        gameState = GameState.ONGOING;
        return ActionResult.success(message);
    }

    public ActionResult endGame(@Nullable String reason) {
        if (currentHandler == null || minigame == null) {
            return ActionResult.warn("No minigame is currently running.");
        }

        minigame.getWorld().getWorldBorder().reset();

        minigame.onMinigameEnded();
        unregisterTeams();
        if (minigame.hasEventListener()) {
            minigame.getEventListener().unregister();
        }

        currentHandler.deactivate();
        currentHandler = null;

        gameState = GameState.IDLE;
        return ActionResult.success(reason);
    }

    public ActionResult restartGame(@Nullable String reason) {
        endGame(null);
        ActionResult startResult = startGame(null);
        return !startResult.isSuccessful()
                ? startResult
                : ActionResult.success(reason);
    }

    private void registerTeams() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();

        if (manager != null) {
            Scoreboard scoreboard = manager.getMainScoreboard();

            for (MiniGameTeam miniGameTeam : minigame.getGameTeams()) {
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

    public enum GameState {
        IDLE, ONGOING, EMPTY
    }

    public interface Options {

        @Nullable Integer getMinimumPlayer();

        @Nullable Location getPlaygroundSpawn();

        @Nullable Integer getPlaygroundRadius();

        @Nullable Integer getDurationGrace();

        @Nullable Integer getDurationRound();
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
}
