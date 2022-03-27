package com.than00ber.mcmg.commands;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.minigames.MiniGame;
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

public class ReadyCommandExecutor extends PluginCommandExecutor {

    public ReadyCommandExecutor(Main instance, World world) {
        super("ready", instance, world);
    }

    @Override
    protected ActionResult execute(@NotNull CommandSender sender, @Nullable String[] args) {
        if (sender instanceof Player player) {
            return VoteCommandExecutor.voteIsReady(player);
        }
        return PluginCommandExecutor.NOT_A_PLAYER;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String option, String[] args) {
        return null;
    }
}
