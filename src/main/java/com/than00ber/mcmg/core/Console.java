package com.than00ber.mcmg.core;

import com.than00ber.mcmg.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public final class Console {

    public static void debug(Object... args) {
        log(ChatColor.GRAY, args);
    }

    public static void warn(Object... args) {
        log(ChatColor.GOLD, args);
    }

    public static void error(Object... args) {
        log(ChatColor.RED, args);
    }

    private static void log(ChatColor color, Object... args) {
        for (Object obj : args) {
            String message = TextUtil.addPluginPrefix(color + "" + obj);
            Bukkit.getConsoleSender().sendMessage(message);
        }
    }
}
