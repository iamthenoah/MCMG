package com.than00ber.mcmg.commands;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.util.ActionResult;
import com.than00ber.mcmg.util.ChatUtil;
import com.than00ber.mcmg.util.Console;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public abstract class CommandExecutor implements org.bukkit.command.CommandExecutor, TabCompleter {

    protected static final ActionResult INVALID_COMMAND = ActionResult.failure("Invalid command format.");

    protected final Main instance;
    protected final World world;

    protected CommandExecutor(String name, Main instance, World world) {
        instance.getCommand(name).setExecutor(this);
        instance.getCommand(name).setTabCompleter(this);

        this.instance = instance;
        this.world = world;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            ActionResult result = execute(sender, args);

            if (result.hasMessages()) {
                String[] messages = result.getFormattedMessages();
                Console.debug(messages);
                ChatUtil.toSelf(sender, messages);
            }
        } catch (Exception e) {
            Console.error(e);
            Console.error(e.getStackTrace());
            ChatUtil.toSelf(sender, INVALID_COMMAND.getFormattedMessages());
            ChatUtil.toSelf(sender, ChatColor.RED + "Should be: " + ChatColor.ITALIC + command.getUsage());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return this.onTabComplete(sender, args[0], Arrays.copyOfRange(args, 1, args.length));
    }

    protected abstract ActionResult execute(@NotNull CommandSender sender, @Nullable String[] args);

    public abstract List<String> onTabComplete(CommandSender sender, String option, String[] args);
}