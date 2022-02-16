package com.than00ber.mcmg.commands;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.games.MiniGame;
import com.than00ber.mcmg.init.MiniGames;
import com.than00ber.mcmg.util.ActionResult;
import com.than00ber.mcmg.util.ChatUtil;
import com.than00ber.mcmg.util.ConfigUtil;
import com.than00ber.mcmg.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class GameCommandExecutor extends CommandExecutor {

    public GameCommandExecutor(Main instance, World world) {
        super("game", instance, world);
    }

    @Override
    protected ActionResult execute(@NotNull CommandSender sender, @Nullable String[] args) {
        return switch (args[0]) {
            case "play"     -> handleGameMount(args);
            case "start"    -> Main.GAME_ENGINE.startGame(getReason(sender, args, "started"));
            case "end"      -> Main.GAME_ENGINE.endGame(getReason(sender, args, "ended"));
            case "restart"  -> Main.GAME_ENGINE.restartGame(getReason(sender, args, "restarted"));
            default         -> CommandExecutor.INVALID_COMMAND;
        };
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String option, String[] args) {
        if (option.equals("play")) {
            return TextUtil.getMatching(args, MiniGames.MINI_GAMES.keySet().stream().toList());
        }
        return args.length == 0 ? TextUtil.getMatching(args, List.of("play", "start", "end", "restart")) : List.of();
    }

    private ActionResult handleGameMount(String[] args) {
        if (args.length == 0) {
            return CommandExecutor.INVALID_COMMAND;
        }

        Supplier<? extends MiniGame> supplier = MiniGames.MINI_GAMES.getOrDefault(args[1].toLowerCase(), null);

        if (supplier != null) {
            MiniGame game = supplier.get();
            ConfigUtil.loadConfigs(instance, game);
            Main.GAME_ENGINE.mount(game);

            String name = game.getGameName();
            int total = Bukkit.getOnlinePlayers().size();

            ReadyCommandExecutor.GAME_NAME = name;
            ReadyCommandExecutor.TOTAL_COUNT = total;
            ReadyCommandExecutor.CURRENT_COUNT = 0;

            String info = "Next game: " + ChatColor.BLUE + ChatColor.ITALIC + name;
            String vote = "Cast your vote now if you are ready. " + ChatColor.ITALIC + "(/ready)";
            String status = "Current vote count: " + ChatColor.YELLOW + "0/" + total;
            ChatUtil.toAll(info, vote, status);

            return ActionResult.success();
        }

        return ActionResult.failure("Game '" + args[1] + "' not found.");
    }

    private static String getReason(CommandSender sender, String[] args, String action) {
        String message = "Game was " + action + " by " + sender.getName();
        if (args.length > 2) {
            String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            message += " (" + reason + ")";
        }
        return message + ".";
    }
}
