package com.than00ber.mcmg.util;

import org.bukkit.command.CommandSender;

public class ChatUtil {

    public static void toSelf(CommandSender sender, String... messages) {
        for (String message : messages) {
            sender.sendMessage(TextUtil.addPluginPrefix(message));
        }
    }
}
