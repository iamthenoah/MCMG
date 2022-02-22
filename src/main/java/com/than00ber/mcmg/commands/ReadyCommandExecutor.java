package com.than00ber.mcmg.commands;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.util.ActionResult;
import com.than00ber.mcmg.util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ReadyCommandExecutor extends PluginCommandExecutor {

    public static String GAME_NAME = null;
    public static int CURRENT_COUNT;
    public static int TOTAL_COUNT;

    public ReadyCommandExecutor(Main instance, World world) {
        super("ready", instance, world);
    }

    @Override
    protected ActionResult execute(@NotNull CommandSender sender, @Nullable String[] args) {
        if (GAME_NAME == null) {
            return ActionResult.warn("There are no game to vote for.");
        }

        CURRENT_COUNT++;
        String status = CURRENT_COUNT + "/" + TOTAL_COUNT;
        ChatUtil.toAll("Current vote count: " + ChatColor.YELLOW + status);

        if (CURRENT_COUNT == TOTAL_COUNT) {
            voteSucceeded();
        }

        return ActionResult.success();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String option, String[] args) {
        return null;
    }

    public static void setVote(String name, int total) {
        String info = "Next game: " + ChatColor.BLUE + name;
        String vote = "Cast your vote now if you are ready. " + ChatColor.ITALIC + "(/ready)";
        String status = "Current vote count: " + ChatColor.YELLOW + "0/" + total;
        ChatUtil.toAll(info, vote, status);

        GAME_NAME = name;
        TOTAL_COUNT = total;
        CURRENT_COUNT = 0;
    }

    public static void voteFailed() {
        if (GAME_NAME != null && !Main.GAME_ENGINE.hasIdleGame() && !Main.GAME_ENGINE.hasRunningGame()) {
            String info = ChatColor.RED + "Vote failed for game " + ChatColor.BLUE + GAME_NAME;
            String status = ChatColor.RED + "Not enough players were ready to play.";
            ChatUtil.toAll(info, status);

            GAME_NAME = null;
            TOTAL_COUNT = 0;
            CURRENT_COUNT = 0;
        }
    }

    public static void voteSucceeded() {
        Main.GAME_ENGINE.startGame(null);

        GAME_NAME = null;
        TOTAL_COUNT = 0;
        CURRENT_COUNT = 0;
    }
}
