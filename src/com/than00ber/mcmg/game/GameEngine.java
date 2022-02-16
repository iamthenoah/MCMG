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
    private Integer HANDLER_ID;
    private GameState GAME_STATE;

    public GameEngine(Main instance) {
        INSTANCE = instance;
        GAME_STATE = GameState.EMPTY;
    }

    public ActionResult mount(G game) {
        if (hasRunningGame()) {
            return ActionResult.failure("Cannot mount a game while game is running");
        }

        GAME = game;
        HANDLER_SUPPLIER = () -> new GameHandler() {

            private MiniGameEvent event;
            private int countdownIdle;
            private int countdownRound;

            @Override
            public void activate() {
                countdownIdle = GAME.getOptions().getDurationIdle();
                countdownRound = GAME.getOptions().getDurationRound();
                BossBar bar = Bukkit.createBossBar(null, BarColor.WHITE, BarStyle.SEGMENTED_10);
                GAME.getCurrentPlayers().forEach((p, r) -> bar.addPlayer(p));
                event = new MiniGameEvent(bar);
            }

            @Override
            public void deactivate() {
                event.getBossBar().removeAll();
            }

            @Override
            public void run() {

                if (countdownIdle > 0) {
                    if (countdownIdle < 3) {
                        GAME.getCurrentPlayers().forEach((p, r) -> p.playNote(p.getLocation(), Instrument.XYLOPHONE, Note.natural(1, Note.Tone.A)));
                    }

                    countdownIdle--;
                    event.getBossBar().setProgress((float) countdownIdle / GAME.getOptions().getDurationIdle());
                    event.getBossBar().setTitle("Game starting in " + countdownIdle + " seconds.");

                    if (countdownIdle == 0) {
                        event.getBossBar().setTitle("");
                        event.getBossBar().setProgress(1);
                        GAME.onRoundStarted(event);
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

                        if (event.hasEnded()) {
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

        Bukkit.getPluginManager().registerEvents(GAME.getEventListener(), INSTANCE); // register new game listener
        GAME.onGameStarted();

        CURRENT_HANDLER = HANDLER_SUPPLIER.get();
        CURRENT_HANDLER.activate();
        HANDLER_ID = Bukkit.getScheduler().scheduleSyncRepeatingTask(INSTANCE, CURRENT_HANDLER, 0, 20);

        GAME_STATE = GameState.ONGOING;
        return ActionResult.success(message);
    }

    public ActionResult endGame(@Nullable String reason) {
        if (CURRENT_HANDLER == null || GAME == null) {
            return ActionResult.warn("No game is currently running.");
        }

        GAME.getEventListener().unregister();
        GAME.onGameEnded();

        CURRENT_HANDLER.deactivate();
        Bukkit.getScheduler().cancelTask(HANDLER_ID);
        CURRENT_HANDLER = null;
        HANDLER_ID = null;

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

    public G getCurrentGame() {
        return GAME;
    }

    public boolean hasRunningGame() {
        return GAME_STATE.equals(GameState.ONGOING);
    }

    public interface Options {

        Integer getMinimumPlayer();

        List<Location> getSpawnLocations();

        Integer getPlaygroundRadius();

        Integer getDurationIdle();

        Integer getDurationRound();
    }

    private interface GameHandler extends Runnable {

        void activate();

        void deactivate();
    }

    public enum GameState {
        IDLE, ONGOING, EMPTY
    }
}
