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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VoteCommandExecutor extends PluginCommandExecutor {

    private static final List<Player> QUEUED_PLAYERS = new ArrayList<>();
    private static Integer VOTING_POOL_ID = null;
    private static Integer REMINDER_ID = null;

    public VoteCommandExecutor(Main instance, World world) {
        super("vote", instance, world);
    }

    @Override
    protected ActionResult execute(@NotNull CommandSender sender, @Nullable String[] args) {
        if (Main.MINIGAME_ENGINE.hasGame()) {
            endCurrentVotingPool();
            int duration = args.length > 0 ? Integer.parseInt(args[0]) : 30;

            String info = "Next minigame: " + TextUtil.formatMiniGame(Main.MINIGAME_ENGINE.getCurrentGame());
            String vote = "Cast your vote now if you are ready. " + ChatColor.ITALIC + "(/ready)";
            String voteDuration = "Voting will last for " + duration + " seconds.";
            ChatUtil.toAll(info, vote, voteDuration);

            REMINDER_ID = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.INSTANCE, () -> {
                if (Main.MINIGAME_ENGINE.hasRunningGame()) {
                    endCurrentVotingPool();
                    return;
                }

                for (Player player : Main.WORLD.getPlayers()) {
                    ChatUtil.toSelf(player, duration / 2 + " seconds remaining to vote.");

                    if (!QUEUED_PLAYERS.contains(player)) {
                        ChatUtil.toSelf(player, ChatColor.YELLOW + "You have not voted yet! " + ChatColor.ITALIC + "(/ready)");
                    }
                }
            }, 20L * (duration / 2));

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
                } catch (Exception e) {
                    ActionResult result = Main.MINIGAME_ENGINE.startMiniGame(QUEUED_PLAYERS, null);

                    if (!result.isSuccessful()) {
                        ChatUtil.toAll("Vote failed.");
                        ChatUtil.toAll(result.getFormattedMessages());
                    }
                } finally {
                    endCurrentVotingPool();
                }
            }, 20L * duration);

            return ActionResult.success();
        }
        return ActionResult.warn("No minigame set.");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String option, String[] args) {
        return null;
    }

    public static ActionResult voteIsReady(Player player) {
        if (VOTING_POOL_ID != null) {
            if (!QUEUED_PLAYERS.contains(player)) {
                ChatUtil.toAll(TextUtil.formatPlayer(player) + " is ready!");
                QUEUED_PLAYERS.add(player);

                if (QUEUED_PLAYERS.size() == Main.WORLD.getPlayers().size()) {
                    Main.MINIGAME_ENGINE.startMiniGame(QUEUED_PLAYERS, null);
                    endCurrentVotingPool();
                    return ActionResult.success("Everyone is ready!");
                }

                return ActionResult.success();
            }
            return ActionResult.info("You have already voted!");
        }
        return ActionResult.warn("There is minigame to vote for.");
    }

    public static void endCurrentVotingPool() {
        Optional.ofNullable(VOTING_POOL_ID).ifPresent(id -> Bukkit.getScheduler().cancelTask(id));
        Optional.ofNullable(REMINDER_ID).ifPresent(id -> Bukkit.getScheduler().cancelTask(id));
        QUEUED_PLAYERS.clear();
        VOTING_POOL_ID = null;
        REMINDER_ID = null;
    }
}
