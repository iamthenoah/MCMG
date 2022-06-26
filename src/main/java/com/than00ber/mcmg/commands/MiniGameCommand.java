package com.than00ber.mcmg.commands;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.init.MiniGames;
import com.than00ber.mcmg.minigames.MiniGame;
import com.than00ber.mcmg.util.ActionResult;
import com.than00ber.mcmg.util.ChatUtil;
import com.than00ber.mcmg.util.TextUtil;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class MiniGameCommand extends PluginCommand {

    public MiniGameCommand(Main instance, World world) {
        super("minigame", instance, world);
    }

    @Override
    protected ActionResult execute(@NotNull CommandSender sender, @Nullable String[] args) {
        ActionResult result = switch (args[0]) {
            case "play"     -> handleGameMount(args);
            case "start"    -> Main.MINIGAME_ENGINE.startMiniGame(Main.WORLD.getPlayers(), getReason(sender, args, "started"));
            case "end"      -> Main.MINIGAME_ENGINE.endMiniGame(getReason(sender, args, "ended"));
            case "restart"  -> Main.MINIGAME_ENGINE.restartMiniGame(getReason(sender, args, "restarted"));
            default         -> PluginCommand.INVALID_COMMAND;
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
            return TextUtil.getMatching(args, MiniGames.MINIGAMES.getRegistryKeys());
        }
        return args.length == 0 ? TextUtil.getMatching(args, List.of("play", "start", "end", "restart")) : List.of();
    }

    private ActionResult handleGameMount(String[] args) {
        if (args.length == 0) return PluginCommand.INVALID_COMMAND;

        MiniGame game = MiniGames.MINIGAMES.get(args[1].toLowerCase());
        ActionResult result = Main.MINIGAME_ENGINE.mount(game);
        if (!result.isSuccessful()) return result;
//        ConfigUtil.loadConfigs(instance, game);

        return ActionResult.info("Minigame set to " + TextUtil.formatMiniGame(game));
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
