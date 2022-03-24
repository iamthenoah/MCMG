package com.than00ber.mcmg.commands;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.init.MiniGames;
import com.than00ber.mcmg.minigames.MiniGame;
import com.than00ber.mcmg.util.ActionResult;
import com.than00ber.mcmg.util.ChatUtil;
import com.than00ber.mcmg.util.TextUtil;
import com.than00ber.mcmg.util.config.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class MiniGameCommandExecutor extends PluginCommandExecutor {

    public MiniGameCommandExecutor(Main instance, World world) {
        super("minigame", instance, world);
    }

    @Override
    protected ActionResult execute(@NotNull CommandSender sender, @Nullable String[] args) {
        ActionResult result = switch (args[0]) {
            case "play"     -> handleGameMount(args);
            case "start"    -> Main.MINIGAME_ENGINE.startGame(getReason(sender, args, "started"));
            case "end"      -> Main.MINIGAME_ENGINE.endGame(getReason(sender, args, "ended"));
            case "restart"  -> Main.MINIGAME_ENGINE.restartGame(getReason(sender, args, "restarted"));
            default         -> PluginCommandExecutor.INVALID_COMMAND;
        };

        if (result.isSuccessful()) {
            ChatUtil.toAll(result.getFormattedMessages());
            return ActionResult.success();
        }
        return result;
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
            return PluginCommandExecutor.INVALID_COMMAND;
        }

        Supplier<? extends MiniGame> supplier = MiniGames.MINI_GAMES.getOrDefault(args[1].toLowerCase(), null);

        if (supplier != null) {
            MiniGame game = supplier.get();
            ConfigUtil.loadConfigs(instance, game);

            ActionResult result = Main.MINIGAME_ENGINE.mount(game);
            if (!result.isSuccessful()) return result;

            ReadyCommandExecutor.setVote(game.getMiniGameName(), Bukkit.getOnlinePlayers().size());

            return ActionResult.success();
        }
        return ActionResult.failure("Minigame '" + args[1] + "' not found.");
    }

    private static String getReason(CommandSender sender, String[] args, String action) {
        String message = "Minigame was " + action + " by " + sender.getName();
        if (args.length > 2) {
            String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            message += " (" + reason + ")";
        }
        return message + ".";
    }
}
