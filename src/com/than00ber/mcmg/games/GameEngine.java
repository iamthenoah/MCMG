package com.than00ber.mcmg.games;

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

    public GameEngine(Main instance) {
        INSTANCE = instance;
    }

    public void mount(G game) {
        GAME = game;
        HANDLER_SUPPLIER = () -> new GameHandler() {

            private BossBar BAR;
            private int countdownIdle;
            private int countdownRound;

            @Override
            public void activate() {
                countdownIdle = GAME.getOptions().getDurationIdle();
                countdownRound = GAME.getOptions().getDurationRound();
                BAR = Bukkit.createBossBar(null, BarColor.WHITE, BarStyle.SEGMENTED_10);
                GAME.getCurrentPlayers().forEach((p, r) -> BAR.addPlayer(p));
            }

            @Override
            public void deactivate() {
                BAR.removeAll();
            }

            @Override
            public void run() {

                if (countdownIdle > 0) {
                    if (countdownIdle < 3) {
                        GAME.getCurrentPlayers().forEach((p, r) -> p.playNote(p.getLocation(), Instrument.XYLOPHONE, Note.natural(1, Note.Tone.A)));
                    }

                    countdownIdle--;
                    BAR.setProgress((float) countdownIdle / GAME.getOptions().getDurationIdle());
                    BAR.setTitle("Game starting in " + countdownIdle + " seconds.");

                    if (countdownIdle == 0) {
                        BAR.setTitle("");
                        BAR.setProgress(1);
                        GAME.onRoundStarted(BAR);
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
                        GAME.onRoundCycled(BAR);
                    }

                    countdownRound--;
                    BAR.setProgress((float) countdownRound / GAME.getOptions().getDurationRound());
                }
            }
        };
    }

    public ActionResult startGame(@Nullable String message) {
        if (GAME == null) {
            return ActionResult.warn("No game is set.");
        }
        if (CURRENT_HANDLER != null) {
            return ActionResult.warn("A game of " + GAME.getGameName() + " is already running.");
        }

        GAME.onGameStarted();

        CURRENT_HANDLER = HANDLER_SUPPLIER.get();
        CURRENT_HANDLER.activate();
        HANDLER_ID = Bukkit.getScheduler().scheduleSyncRepeatingTask(INSTANCE, CURRENT_HANDLER, 0, 20);

        return ActionResult.success(message);
    }

    public ActionResult endGame(@Nullable String reason) {
        if (CURRENT_HANDLER == null || GAME == null) {
            return ActionResult.warn("No game is currently running.");
        }

        GAME.onGameEnded();

        CURRENT_HANDLER.deactivate();
        Bukkit.getScheduler().cancelTask(HANDLER_ID);
        CURRENT_HANDLER = null;
        HANDLER_ID = null;

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
        return GAME != null;
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
}
