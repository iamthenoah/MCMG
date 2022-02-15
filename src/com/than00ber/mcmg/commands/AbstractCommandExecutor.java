package com.than00ber.mcmg.commands;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.util.ActionResult;
import com.than00ber.mcmg.util.ChatUtil;
import com.than00ber.mcmg.util.Console;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public abstract class AbstractCommandExecutor implements CommandExecutor, TabCompleter {

    protected static final ActionResult INVALID_COMMAND = ActionResult.failure("Invalid command format.");

    protected final Main instance;
    protected final World world;

    protected AbstractCommandExecutor(String name, Main instance, World world) {
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
                ChatUtil.toSelf(sender, messages);
                Console.debug(messages);
            }
        } catch (Exception e) {
            Console.error(e);
            Console.error(e.getStackTrace());
            ChatUtil.toSelf(sender, INVALID_COMMAND.getFormattedMessages());
            String shouldBe = ChatColor.RED + "Should be: " + ChatColor.ITALIC + command.getUsage();
            ChatUtil.toSelf(sender, shouldBe);
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return this.onTabComplete(sender, args[0], Arrays.copyOfRange(args, 1, args.length));
    }

    protected <T> List<String> getMatching(String[] args, List<T> list, Function<T, String> function) {
        if (args.length == 0) return list.stream().map(function).toList();
        String last = args[args.length - 1];
        ArrayList<String> matching = new ArrayList<>();

        for (T obj : list) {
            String name = function.apply(obj);

            if (StringUtil.startsWithIgnoreCase(name, last)) {
                matching.add(name);
            }
        }

        matching.sort(String.CASE_INSENSITIVE_ORDER);
        return matching;
    }

    protected abstract ActionResult execute(@NotNull CommandSender sender, @Nullable String[] args);

    public abstract List<String> onTabComplete(CommandSender sender, String option, String[] args);
}
