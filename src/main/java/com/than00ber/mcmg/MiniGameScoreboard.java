package com.than00ber.mcmg;

import com.than00ber.mcmg.init.MiniGames;
import com.than00ber.mcmg.minigames.MiniGame;
import com.than00ber.mcmg.util.config.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

public class MiniGameScoreboard {

    private static final String SCOREBOARD_DATA = "scoreboard-data.lock";

    private final Main instance;
    private final YamlConfiguration data;

    public MiniGameScoreboard(Main instance) {
        this.instance = instance;
        data = ConfigUtil.load(instance, SCOREBOARD_DATA);
    }

    public void setPlayerScore(Player player, MiniGame miniGame, int score) {
        data.set(getScorePath(player, miniGame), score);
        ConfigUtil.save(instance, SCOREBOARD_DATA, data);
    }

    public int getPlayerScore(Player player, MiniGame miniGame) {
        Object score = data.get(getScorePath(player, miniGame));
        return score == null ? 0 : (int) score;
    }

    public int getPlayerTotalScore(Player player) {
        int score = 0;
        for (Supplier<? extends MiniGame> miniGame : MiniGames.MINI_GAMES.values()) {
            score += getPlayerScore(player, miniGame.get());
        }
        return score;
    }

    public void showSideboard(final List<Player> players) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        final Scoreboard board = manager.getNewScoreboard();

        final Objective objective = board.registerNewObjective("test", "dummy", "???");
        objective.setDisplayName(ChatColor.BLUE + "" + ChatColor.BOLD + "MCMG Leaderboard");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        TreeMap<Integer, String> scores = new TreeMap<>();
        for (Player player : players) {
            int score = getPlayerTotalScore(player);
            String title = ScoreboardLevel.getColor(score) + player.getDisplayName();
            scores.put(score, title);
        }

        for (Map.Entry<Integer, String> entry : scores.entrySet()) {
            Score score = objective.getScore(entry.getValue());
            score.setScore(entry.getKey());
        }

        players.forEach(player -> player.setScoreboard(board));
    }

    public void hideScoreboard(final List<Player> players) {
        players.forEach(player -> player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard()));
    }

    private static String getScorePath(Player player, MiniGame miniGame) {
        return "scores." + miniGame.getMiniGameName() + "." + player.getUniqueId();
    }

    private enum ScoreboardLevel {
        DIRT(ChatColor.WHITE),
        STONE(ChatColor.YELLOW),
        GOLD(ChatColor.DARK_GREEN),
        DIAMOND(ChatColor.GREEN),
        NETHERITE(ChatColor.AQUA);

        final ChatColor color;

        ScoreboardLevel(ChatColor color) {
            this.color = color;
        }

        private static ChatColor getColor(int score) {
            int level = Math.max(0, Math.min(ScoreboardLevel.values().length - 1, score));
            return ScoreboardLevel.values()[level].color;
        }
    }
}
