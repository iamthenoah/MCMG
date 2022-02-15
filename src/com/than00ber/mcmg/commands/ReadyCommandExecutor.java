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

public class ReadyCommandExecutor extends CommandExecutor {

    public static String GAME_NAME = null;
    public static Integer CURRENT_COUNT = null;
    public static Integer TOTAL_COUNT = null;

    public ReadyCommandExecutor(Main instance, World world) {
        super("ready", instance, world);
    }

    @Override
    protected ActionResult execute(@NotNull CommandSender sender, @Nullable String[] args) {
        if (GAME_NAME == null) {
            return ActionResult.warn("There are no game to vote for.");
        }

        CURRENT_COUNT++;
        if (CURRENT_COUNT.equals(TOTAL_COUNT)) {
            String status = CURRENT_COUNT + "/" + TOTAL_COUNT;
            ChatUtil.toAll("Current vote count: " + ChatColor.YELLOW + status);

            Main.GAME_ENGINE.startGame(null);
            CURRENT_COUNT = null;
            TOTAL_COUNT = null;
            GAME_NAME = null;
        }

        return ActionResult.success();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String option, String[] args) {
        return null;
    }
}
