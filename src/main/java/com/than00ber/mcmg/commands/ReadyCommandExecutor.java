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
            Main.GAME_ENGINE.startGame(null);
            CURRENT_COUNT = 0;
            TOTAL_COUNT = 0;
            GAME_NAME = null;
        }

        return ActionResult.success();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String option, String[] args) {
        return null;
    }
}