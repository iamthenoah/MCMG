package com.than00ber.mcmg.util;

import com.than00ber.mcmg.Main;
import org.bukkit.ChatColor;

public class TextUtil {

    public static String addPluginPrefix(String message) {
        return ChatColor.GRAY + "[" + Main.PLUGIN_ID + "] " + ChatColor.RESET + message;
    }
}
