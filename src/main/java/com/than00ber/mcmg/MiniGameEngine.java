package com.than00ber.mcmg;

import com.than00ber.mcmg.events.MiniGameEventListener;
import com.than00ber.mcmg.init.MiniGameTeams;
import com.than00ber.mcmg.minigames.MiniGame;
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
    private List<Player> participants; // TODO - revisit how players are queued
    private G minigame;
    private Supplier<MiniGameHandler> handlerSupplier;
    private MiniGameHandler currentHandler;
    private MiniGameRoundState roundState;

    public MiniGameEngine(Main instance) {
        this.instance = instance;
        roundState = MiniGameRoundState.NOT_RUNNING;
    }

    public G getCurrentGame() {
        return minigame;
    }

    public boolean hasGame() {
        return minigame != null;
    }

    public boolean hasRunningGame() {
        return currentHandler != null;
    }

    public MiniGameRoundState getRoundState() {
        return roundState;
    }

    public ActionResult mount(G nextGame) {
        if (hasRunningGame()) return ActionResult.failure("A minigame is still running.");

        if (minigame != null && minigame.hasEventListener()) {
            minigame.getEventListener().unregister();
        }

        handlerSupplier = () -> new MiniGameHandler(instance) {

            private MiniGameEvent event;
            private int countdownGrace;
            private int countdownRound;

            @Override
            public void onActivate() {
                roundState = MiniGameRoundState.GRACE;
                countdownGrace = minigame.getOptions().getDurationGrace();
                countdownRound = minigame.getOptions().getDurationRound();
                BossBar bar = Bukkit.createBossBar(null, BarColor.WHITE, BarStyle.SEGMENTED_10);
                minigame.getWorld().getPlayers().forEach(bar::addPlayer);
                event = new MiniGameEvent(bar);
            }

            @Override
            public void onDeactivate() {
                roundState = MiniGameRoundState.NOT_RUNNING;
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
                    if (roundState != MiniGameRoundState.ONGOING) {
                        roundState = MiniGameRoundState.ONGOING;
                    }

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

        participants = null;
        minigame = nextGame;
        return ActionResult.success();
    }

    public ActionResult startMiniGame(List<Player> players, @Nullable String message) {
        ActionResult invalid = checkCanStart(players);
        if (!invalid.isSuccessful()) return invalid;

        participants = players;
        minigame.getWorld().getWorldBorder().setSize(minigame.getOptions().getPlaygroundRadius());
        minigame.getWorld().getWorldBorder().setCenter(minigame.getOptions().getPlaygroundSpawn());

        unregisterTeams();
        registerTeams(minigame);
        minigame.onMinigameStarted(participants);
        Optional.ofNullable(minigame.getEventListener()).ifPresent(MiniGameEventListener::register);
        for (Player player : minigame.getWorld().getPlayers()) {

            if (!participants.contains(player)) {
                MiniGameTeams.SPECTATORS.prepare(player);
                String s = TextUtil.formatMiniGame(minigame) + ChatColor.GOLD;
                ChatUtil.toSelf(player, s + " minigame started and you were not ready.");
            }
        }

        currentHandler = handlerSupplier.get();
        currentHandler.activate();

        return ActionResult.success(message);
    }

    public ActionResult endMiniGame(@Nullable String reason) {
        if (!hasGame()) return ActionResult.warn("No minigame is currently running.");

        minigame.getWorld().getWorldBorder().reset();
        minigame.onMinigameEnded();
        currentHandler.deactivate();
        currentHandler = null;

        unregisterTeams();
        Optional.ofNullable(minigame.getEventListener()).ifPresent(MiniGameEventListener::unregister);
        Bukkit.getScheduler().cancelTasks(instance);

        return ActionResult.success(reason);
    }

    public ActionResult restartMiniGame(@Nullable String reason) {
        if (!hasGame()) return ActionResult.warn("No minigame is currently running.");

        minigame.onMinigameEnded();
        Optional.ofNullable(currentHandler).ifPresent(MiniGameHandler::deactivate);
        currentHandler = null;

        if (participants == null) participants = Main.WORLD.getPlayers();
        ActionResult startResult = startMiniGame(participants, null);
        return !startResult.isSuccessful() ? startResult : ActionResult.success(reason);
    }

    private ActionResult checkCanStart(List<Player> participants) {
        if (!hasGame()) {
            return ActionResult.warn("No minigame is set.");
        }
        String name = TextUtil.formatMiniGame(minigame);
        if (hasRunningGame()) {
            return ActionResult.warn("A minigame of " + name + ChatColor.GOLD + " is already running.");
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

    private static void registerTeams(MiniGame game) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();

        if (manager != null) {
            Scoreboard scoreboard = manager.getMainScoreboard();

            for (MiniGameTeam miniGameTeam : game.getMiniGameTeams()) {
                Team team = scoreboard.registerNewTeam(miniGameTeam.getTeamId());
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, miniGameTeam.getVisibility());
            }
        }
    }

    private static void unregisterTeams() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();

        if (manager != null) {
            Scoreboard scoreboard = manager.getMainScoreboard();
            scoreboard.getTeams().forEach(Team::unregister);
        }
    }


    public interface Options {

        Integer getMinimumPlayer();

        Location getPlaygroundSpawn();

        Integer getPlaygroundRadius();

        Integer getDurationGrace();

        Integer getDurationRound();
    }

    public enum MiniGameRoundState {
        NOT_RUNNING, GRACE, ONGOING
    }
}
