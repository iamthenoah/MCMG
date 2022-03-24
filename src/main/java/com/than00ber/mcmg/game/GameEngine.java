package com.than00ber.mcmg.game;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.objects.GameTeam;
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

public class GameEngine<G extends MiniGame> {

    private final Main instance;
    private G game;
    private Supplier<GameHandler> handlerSupplier;
    private GameHandler currentHandler;
    private GameState gameState;

    public GameEngine(Main instance) {
        this.instance = instance;
        gameState = GameState.EMPTY;
    }

    public G getCurrentGame() {
        return game;
    }

    public boolean hasGame() {
        return game != null;
    }

    public boolean hasIdleGame() {
        return gameState.equals(GameState.IDLE);
    }

    public boolean hasRunningGame() {
        return gameState.equals(GameState.ONGOING);
    }

    public ActionResult mount(G nextGame) {
        if (hasRunningGame()) {
            return ActionResult.failure("Cannot mount a game while a game is running");
        }

        if (game != null && game.hasEventListener()) {
            game.getEventListener().unregister();
        }

        game = nextGame;

        handlerSupplier = () -> new GameHandler(instance) {

            private MiniGameEvent event;
            private int countdownGrace;
            private int countdownRound;

            @Override
            public void onActivate() {
                countdownGrace = game.getOptions().getDurationGrace();
                countdownRound = game.getOptions().getDurationRound();
                BossBar bar = Bukkit.createBossBar(null, BarColor.WHITE, BarStyle.SEGMENTED_10);
                game.getWorld().getPlayers().forEach(bar::addPlayer);
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
                        game.getWorld().getPlayers().forEach(p -> {
                            Note note = Note.natural(1, Note.Tone.A);
                            p.playNote(p.getLocation(), Instrument.XYLOPHONE, note);
                        });
                    }

                    countdownGrace--;
                    event.getBossBar().setProgress((float) countdownGrace / game.getOptions().getDurationGrace());
                    event.getBossBar().setTitle("Game starting in " + countdownGrace + " seconds");

                    if (countdownGrace == 0) {
                        event.getBossBar().setTitle("");
                        event.getBossBar().setProgress(1);
                        game.onRoundStarted(event);

                        if (event.getBossBar().getTitle().isEmpty()) {
                            event.getBossBar().setTitle("Time Remaining");
                        }
                    }
                } else {
                    if (countdownRound == 0) {
                        countdownRound = game.getOptions().getDurationRound();
                        game.onRoundCycled(event);

                        if (event.getBossBar().getTitle().isEmpty()) {
                            event.getBossBar().setTitle("Time Remaining");
                        }

                        if (event.hasEnded()) {
                            game.onRoundWon(event.getWinCondition());
                            endGame(null);
                        }
                    }

                    WinCondition<?> condition =  game.getWinConditions().stream()
                            .filter(c -> c.check(game)).findAny().orElse(null);

                    if (condition != null) {
                        game.onRoundWon(condition);
                        endGame(null);
                    }

                    countdownRound--;
                    event.getBossBar().setProgress((float) countdownRound / game.getOptions().getDurationRound());
                }

            }
        };

        gameState = GameState.IDLE;
        return ActionResult.success();
    }

    public ActionResult startGame(@Nullable String message) {
        if (game == null) {
            return ActionResult.warn("No game is set.");
        }
        if (currentHandler != null) {
            return ActionResult.warn("A game of " + game.getGameName() + " is already running.");
        }
        if (game.getOptions().getPlaygroundSpawn() == null) {
            return ActionResult.warn("No playground spawn set for game " + game.getGameName());
        }
        if (game.getOptions().getPlaygroundRadius() == null) {
            return ActionResult.warn("No playground radius set for game " + game.getGameName());
        }

        game.getWorld().getWorldBorder().setSize(game.getOptions().getPlaygroundRadius());
        game.getWorld().getWorldBorder().setCenter(game.getOptions().getPlaygroundSpawn());

        unregisterTeams();
        registerTeams();
        game.onGameStarted();
        if (game.hasEventListener()) {
            game.getEventListener().register();
        }

        currentHandler = handlerSupplier.get();
        currentHandler.activate();

        gameState = GameState.ONGOING;
        return ActionResult.success(message);
    }

    public ActionResult endGame(@Nullable String reason) {
        if (currentHandler == null || game == null) {
            return ActionResult.warn("No game is currently running.");
        }

        game.getWorld().getWorldBorder().reset();

        game.onGameEnded();
        unregisterTeams();
        if (game.hasEventListener()) {
            game.getEventListener().unregister();
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

            for (GameTeam gameTeam : game.getGameTeams()) {
                Team team = scoreboard.registerNewTeam(gameTeam.getTeamId());
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, gameTeam.getVisibility());
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
}
