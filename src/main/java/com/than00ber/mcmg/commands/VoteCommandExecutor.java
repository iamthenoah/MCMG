package com.than00ber.mcmg.commands;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.util.ActionResult;
import com.than00ber.mcmg.util.ChatUtil;
import com.than00ber.mcmg.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VoteCommandExecutor extends PluginCommandExecutor {

    private static final List<Player> QUEUED_PLAYERS = new ArrayList<>();

    public static String MINIGAME_NAME = null;
    private static Integer VOTING_POOL_ID = null;
    private static Integer REMINDER_ID = null;

    public VoteCommandExecutor(Main instance, World world) {
        super("ready", instance, world);
    }

    @Override
    protected ActionResult execute(@NotNull CommandSender sender, @Nullable String[] args) {
        if (sender instanceof Player player) {
            if (MINIGAME_NAME == null) return ActionResult.warn("There is no minigame to vote for.");
            ChatUtil.toAll(ChatColor.AQUA + player.getDisplayName() + ChatColor.RESET + " is ready!");
            QUEUED_PLAYERS.add(player);
            return ActionResult.success();
        }
        return PluginCommandExecutor.NOT_A_PLAYER;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String option, String[] args) {
        return null;
    }

    public static void setVote(String name, int voteDuration) {
        endCurrentVotingPool();
        MINIGAME_NAME = name;

        String info = "Next minigame: " + TextUtil.formatMiniGame(name);
        String vote = "Cast your vote now if you are ready. " + ChatColor.ITALIC + "(/ready)";
        String duration = "Voting will last for " + voteDuration + " seconds.";
        ChatUtil.toAll(info, vote, duration);

        Optional.ofNullable(VOTING_POOL_ID).ifPresent(id -> Bukkit.getScheduler().cancelTask(id));
        Optional.ofNullable(REMINDER_ID).ifPresent(id -> Bukkit.getScheduler().cancelTask(id));

        REMINDER_ID = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.INSTANCE, () -> {
            if (Main.MINIGAME_ENGINE.hasRunningGame()) {
                endCurrentVotingPool();
                return;
            }

            for (Player player : Main.WORLD.getPlayers()) {
                ChatUtil.toSelf(player, voteDuration / 2 + " seconds remaining to vote.");

                if (!QUEUED_PLAYERS.contains(player)) {
                    ChatUtil.toSelf(player, ChatColor.YELLOW + "You have not voted yet! " + ChatColor.ITALIC + "(/ready)");
                }
            }
        }, 20L * (voteDuration / 2));

        VOTING_POOL_ID = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.INSTANCE, () -> {
            if (Main.MINIGAME_ENGINE.hasRunningGame()) {
                endCurrentVotingPool();
                return;
            }

            try {
                ActionResult result = Main.MINIGAME_ENGINE.startMiniGame(QUEUED_PLAYERS, null);

                if (!result.isSuccessful()) {
                    ChatUtil.toAll("Vote failed.");
                    ChatUtil.toAll(result.getFormattedMessages());
                }
            } finally {
                endCurrentVotingPool();
            }
        }, 20L * voteDuration);
    }

    public static void endCurrentVotingPool() {
        Optional.ofNullable(VOTING_POOL_ID).ifPresent(id -> Bukkit.getScheduler().cancelTask(id));
        Optional.ofNullable(REMINDER_ID).ifPresent(id -> Bukkit.getScheduler().cancelTask(id));
        QUEUED_PLAYERS.clear();
        MINIGAME_NAME = null;
        VOTING_POOL_ID = null;
        REMINDER_ID = null;
    }

    public static boolean hasOngoingPoll() {
        return VOTING_POOL_ID != null;
    }
}
