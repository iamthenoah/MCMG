package com.than00ber.mcmg.game;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.objects.WinCondition;
import com.than00ber.mcmg.util.ActionResult;
import org.bukkit.Bukkit;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Note;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class GameEngine<G extends MiniGame> {

    private final Main INSTANCE;
    private G GAME;
    private Supplier<GameHandler> HANDLER_SUPPLIER;
    private GameHandler CURRENT_HANDLER;
    private GameState GAME_STATE;

    public GameEngine(Main instance) {
        INSTANCE = instance;
        GAME_STATE = GameState.EMPTY;
    }

    public G getCurrentGame() {
        return GAME;
    }

    public boolean hasGame() {
        return GAME != null;
    }

    public boolean hasIdleGame() {
        return GAME_STATE.equals(GameState.IDLE);
    }

    public boolean hasRunningGame() {
        return GAME_STATE.equals(GameState.ONGOING);
    }

    public ActionResult mount(G game) {
        if (hasRunningGame()) {
            return ActionResult.failure("Cannot mount a game while a game is running");
        }

        if (GAME != null && GAME.getEventListener() != null) {
            GAME.getEventListener().unregister();
        }
        GAME = game;
        HANDLER_SUPPLIER = () -> new GameHandler(INSTANCE) {

            private MiniGameEvent event;
            private int countdownGrace;
            private int countdownRound;

            @Override
            public void onActivate() {
                countdownGrace = GAME.getOptions().getDurationGrace();
                countdownRound = GAME.getOptions().getDurationRound();
                BossBar bar = Bukkit.createBossBar(null, BarColor.WHITE, BarStyle.SEGMENTED_10);
                GAME.getWorld().getPlayers().forEach(bar::addPlayer);
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
                        GAME.getWorld().getPlayers().forEach(p -> {
                            Note note = Note.natural(1, Note.Tone.A);
                            p.playNote(p.getLocation(), Instrument.XYLOPHONE, note);
                        });
                    }

                    countdownGrace--;
                    event.getBossBar().setProgress((float) countdownGrace / GAME.getOptions().getDurationGrace());
                    event.getBossBar().setTitle("Game starting in " + countdownGrace + " seconds");

                    if (countdownGrace == 0) {
                        event.getBossBar().setTitle("");
                        event.getBossBar().setProgress(1);
                        GAME.onRoundStarted(event);

                        if (event.getBossBar().getTitle().isEmpty()) {
                            event.getBossBar().setTitle("Time Remaining");
                        }
                    }
                } else {
                    WinCondition<?> condition =  GAME.getWinConditions().stream()
                            .filter(c -> c.check(GAME)).findAny().orElse(null);

                    if (condition != null) {
                        GAME.onRoundWon(condition);
                        endGame(null);
                    }

                    if (countdownRound == 0) {
                        countdownRound = GAME.getOptions().getDurationRound();
                        GAME.onRoundCycled(event);

                        if (event.getBossBar().getTitle().isEmpty()) {
                            event.getBossBar().setTitle("Time Remaining");
                        }

                        if (event.hasEnded()) {
                            GAME.onRoundWon(event.getWinCondition());
                            endGame(null);
                        }
                    }

                    countdownRound--;
                    event.getBossBar().setProgress((float) countdownRound / GAME.getOptions().getDurationRound());
                }
            }
        };

        GAME_STATE = GameState.IDLE;
        return ActionResult.success();
    }

    public ActionResult startGame(@Nullable String message) {
        if (GAME == null) {
            return ActionResult.warn("No game is set.");
        }
        if (CURRENT_HANDLER != null) {
            return ActionResult.warn("A game of " + GAME.getGameName() + " is already running.");
        }

        GAME.onGameStarted();
        if (GAME.hasEventListener()) {
            GAME.getEventListener().register();
        }

        CURRENT_HANDLER = HANDLER_SUPPLIER.get();
        CURRENT_HANDLER.activate();

        GAME_STATE = GameState.ONGOING;
        return ActionResult.success(message);
    }

    public ActionResult endGame(@Nullable String reason) {
        if (CURRENT_HANDLER == null || GAME == null) {
            return ActionResult.warn("No game is currently running.");
        }

        GAME.onGameEnded();
        if (GAME.getEventListener() != null) {
            // unregister game listeners
            GAME.getEventListener().unregister();
        }

        CURRENT_HANDLER.deactivate();
        CURRENT_HANDLER = null;

        GAME_STATE = GameState.IDLE;
        return ActionResult.success(reason);
    }

    public ActionResult restartGame(@Nullable String reason) {
        endGame(null);
        ActionResult startResult = startGame(null);
        return !startResult.isSuccessful()
                ? startResult
                : ActionResult.success(reason);
    }

    public enum GameState {
        IDLE, ONGOING, EMPTY
    }

    public interface Options {

        Integer getMinimumPlayer();

        List<Location> getSpawnLocations();

        Integer getPlaygroundRadius();

        Integer getDurationGrace();

        Integer getDurationRound();
    }
}
