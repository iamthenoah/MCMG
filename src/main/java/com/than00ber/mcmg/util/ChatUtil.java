package com.than00ber.mcmg.util;

import com.google.common.collect.ImmutableMap;
import com.than00ber.mcmg.MiniGameTeam;
import com.than00ber.mcmg.WinCondition;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public static void showRoundStartScreen(ImmutableMap<Player, MiniGameTeam> players) {
        players.forEach((player, team) -> {
            ChatUtil.toSelf(player, "");
            ChatUtil.toSelf(player, TextUtil.formatObjective(team));
            ChatUtil.toSelf(player, "");
            String comment = ChatColor.ITALIC + team.getCatchPhrase();
            player.sendTitle(TextUtil.formatGameTeam(team), comment, 5, 100, 15);
            player.playSound(player.getLocation(), team.getSound(), 100, 1);
        });
    }

    public static void showRoundEndScreen(ImmutableMap<Player, MiniGameTeam> players, List<MiniGameTeam> teams, WinCondition<?> condition) {
        players.forEach((player, role) -> {
            // scoreboard
            ChatUtil.toSelf(player, ChatColor.YELLOW + " ---------- Scoreboard ----------");
            for (MiniGameTeam team : teams) {
                Map<Player, MiniGameTeam> filtered = players.entrySet().stream()
                        .filter(entry -> entry.getValue().equals(team))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                if (!filtered.isEmpty()) {
                    String names = "\u0020\u0020" + team.getColor() + filtered.keySet().stream()
                            .map(TextUtil::formatPlayer)
                            .collect(Collectors.joining(", "));

                    String status = condition.getWinners().contains(team)
                            ? ChatColor.GREEN + " [won] "
                            : ChatColor.RED + " [lost] ";
                    ChatUtil.toSelf(player, TextUtil.formatGameTeam(team) + status);
                    ChatUtil.toSelf(player, String.join(", ", names));
                }
            }

            // title
            boolean won = condition.getWinners().contains(role);
            String title = condition.getTitleFor(role);
            String sub = condition.getSubTitleFor(role);
            player.sendTitle(ChatColor.BOLD + title, sub,5, 100, 30);
            Sound sound = won ? Sound.UI_TOAST_CHALLENGE_COMPLETE : Sound.ENTITY_CHICKEN_HURT;

            player.playSound(player.getLocation(), sound, 100, 1);
        });
    }
}
