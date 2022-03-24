package com.than00ber.mcmg.commands;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.util.ActionResult;
import com.than00ber.mcmg.util.ChatUtil;
import com.than00ber.mcmg.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ReadyCommandExecutor extends PluginCommandExecutor {

    public static String MINIGAME_NAME = null;
    public static Integer VOTING_POOL_ID;
    public static int CURRENT_COUNT;
    public static int TOTAL_COUNT;

    public ReadyCommandExecutor(Main instance, World world) {
        super("ready", instance, world);
    }

    @Override
    protected ActionResult execute(@NotNull CommandSender sender, @Nullable String[] args) {
        if (MINIGAME_NAME == null) {
            return ActionResult.warn("There are no minigame to vote for.");
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
        String info = "Next minigame: " + TextUtil.formatMiniGame(name);
        String vote = "Cast your vote now if you are ready. " + ChatColor.ITALIC + "(/ready)";
        String status = "Current vote count: " + ChatColor.YELLOW + "0/" + total;
        ChatUtil.toAll(info, vote, status);

        if (VOTING_POOL_ID != null) {
            Bukkit.getScheduler().cancelTask(VOTING_POOL_ID);
        }

        VOTING_POOL_ID = Bukkit.getScheduler().scheduleSyncDelayedTask(
                Main.INSTANCE, ReadyCommandExecutor::voteFailed, 20 * 30
        );

        MINIGAME_NAME = name;
        TOTAL_COUNT = total;
        CURRENT_COUNT = 0;
    }

    public static void voteFailed() {
        if (MINIGAME_NAME != null && !Main.MINIGAME_ENGINE.hasIdleGame() && !Main.MINIGAME_ENGINE.hasRunningGame()) {
            String info = ChatColor.RED + "Vote failed for minigame " + TextUtil.formatMiniGame(MINIGAME_NAME);
            String status = ChatColor.RED + "Not enough players were ready to play.";
            ChatUtil.toAll(info, status);

            MINIGAME_NAME = null;
            TOTAL_COUNT = 0;
            CURRENT_COUNT = 0;
        }
    }

    public static void voteSucceeded() {
        Main.MINIGAME_ENGINE.startMiniGame(null);

        MINIGAME_NAME = null;
        TOTAL_COUNT = 0;
        CURRENT_COUNT = 0;
    }
}
