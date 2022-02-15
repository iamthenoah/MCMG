package com.than00ber.mcmg.games;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.util.ActionResult;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
            private int roundCount;

            @Override
            public void activate() {
                countdownIdle = GAME.getOptions().getIdleDuration();
                countdownRound = GAME.getOptions().getRoundDuration();
                roundCount = GAME.getOptions().getRoundCount();
                BAR = Bukkit.createBossBar(null, BarColor.WHITE, BarStyle.SEGMENTED_10);
                GAME.getCurrentPlayers().forEach(p -> BAR.addPlayer(p));
            }

            @Override
            public void deactivate() {
                BAR.removeAll();
            }

            @Override
            public void run() {
                if (countdownIdle >= 0) {
                    if (countdownIdle == 0) {
                        GAME.onRoundStarted();
                    }

                    BAR.setProgress((float) countdownIdle / GAME.getOptions().getIdleDuration());
                    BAR.setTitle("Next round starting in " + countdownIdle + " seconds.");
                    countdownIdle--;
                } else {
                    if (countdownRound <= 0) {
                        GAME.onRoundEnded();

                        if (roundCount == 0) {
                            endGame(null);
                        } else {
                            roundCount--;
                        }
                    }

                    BAR.setColor(BarColor.GREEN);
                    BAR.setProgress((float) countdownRound / GAME.getOptions().getRoundDuration());
                    BAR.setTitle(ChatColor.GREEN + "Round duration, " + countdownRound + " seconds.");
                    countdownRound--;
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

        Integer getIdleDuration();

        Integer getMinimumPlayer();

        List<Location> getSpawnLocations();

        Integer getPlaygroundRadius();

        Integer getRoundDuration();

        Integer getRoundCount();
    }

    private interface GameHandler extends Runnable {

        void activate();

        void deactivate();
    }
}
