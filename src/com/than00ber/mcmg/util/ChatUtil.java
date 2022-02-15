package com.than00ber.mcmg.util;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ChatUtil {

    public static void toSelf(CommandSender sender, String... messages) {
        for (String message : messages) {
            sender.sendMessage(TextUtil.addPluginPrefix(message));
        }
    }

    public static void toAll(String... messages) {
        for (String message : messages) {
            Bukkit.broadcastMessage(TextUtil.addPluginPrefix(message));
        }
    }
}
