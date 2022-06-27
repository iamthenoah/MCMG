package com.than00ber.mcmg.util;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

    public static void toActionBar(Player player, String message) {
        TextComponent component = new TextComponent();
        component.setText(message);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
    }
}
