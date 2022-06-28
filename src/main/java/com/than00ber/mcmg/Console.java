package com.than00ber.mcmg;

import com.than00ber.mcmg.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public final class Console {

    private final boolean isDebug;

    public Console(boolean isDebug) {
        this.isDebug = isDebug;
    }

    public void debug(Object... args) {
        if (isDebug) log(ChatColor.GRAY, args);
    }

    public void warn(Object... args) {
        log(ChatColor.GOLD, args);
    }

    public void error(Object... args) {
        log(ChatColor.RED, args);
    }

    private void log(ChatColor color, Object... args) {
        for (Object obj : args) {
            String message = TextUtil.addPluginPrefix(color + "" + obj);
            Bukkit.getConsoleSender().sendMessage(message);
        }
    }
}
