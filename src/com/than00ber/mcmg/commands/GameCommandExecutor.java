package com.than00ber.mcmg.commands;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.util.ActionResult;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class GameCommandExecutor extends AbstractCommandExecutor {

    public GameCommandExecutor(Main instance, World world) {
        super("game", instance, world);
    }

    @Override
    protected ActionResult execute(@NotNull CommandSender sender, @Nullable String[] args) {
        return switch (args[0]) {
            case "start"    -> Main.GAME_ENGINE.startGame(getReason(sender, args, "started"));
            case "end"      -> Main.GAME_ENGINE.endGame(getReason(sender, args, "ended"));
            case "restart"  -> Main.GAME_ENGINE.restartGame(getReason(sender, args, "restarted"));
            default         -> AbstractCommandExecutor.INVALID_COMMAND;
        };
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String option, String[] args) {
        return args.length == 0 ? getMatching(args, List.of("start", "end", "restart"), s -> s) : List.of();
    }

    private static String getReason(CommandSender sender, String[] args, String action) {
        String message = "Game was " + action + " by " + sender.getName();
        if (args.length > 2) {
            String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            return message + " (" + reason + ")";
        }
        return message + ".";
    }
}
