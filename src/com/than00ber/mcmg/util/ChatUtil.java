package com.than00ber.mcmg.util;

import com.than00ber.mcmg.objects.GameTeam;
import com.than00ber.mcmg.objects.WinCondition;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
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

    public static void showRoundStartScreen(HashMap<Player, GameTeam> players) {
        players.forEach((player, team) -> {
            ChatUtil.toSelf(player, "");
            ChatUtil.toSelf(player, TextUtil.formatObjective(team));
            ChatUtil.toSelf(player, "");
            String comment = ChatColor.ITALIC + team.getCatchPhrase();
            player.sendTitle(TextUtil.formatRole(team), comment, 5, 50, 15);
            player.playSound(player.getLocation(), team.getSound(), 100, 1);
        });
    }

    public static void showRoundEndScreen(HashMap<Player, GameTeam> players, List<GameTeam> teams, WinCondition<?> condition) {
        players.forEach((player, role) -> {
            // scoreboard
            ChatUtil.toSelf(player, ChatColor.YELLOW + " ---------- Scoreboard ----------");
            ChatUtil.toSelf(player, "");
            for (GameTeam team : teams) {
                Map<Player, GameTeam> filtered = players.entrySet().stream()
                        .filter(entry -> entry.getValue().equals(team))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                if (!filtered.isEmpty()) {
                    String names = "\u0020\u0020" + team.getColor() + filtered.keySet().stream()
                            .map(Player::getDisplayName)
                            .collect(Collectors.joining(", "));

                    String s = "> In the " + TextUtil.formatRole(team).toUpperCase() + ChatColor.RESET + " team was...";
                    ChatUtil.toSelf(player, s);
                    ChatUtil.toSelf(player, String.join(", ", names));
                }
            }
            ChatUtil.toSelf(player, "");

            // title
            boolean won = condition.getWinners().contains(role);
            String title = condition.getTitleFor(role);
            String sub = condition.getSubTitleFor(role);
            player.sendTitle(ChatColor.BOLD + title, sub,5, 100, 30);
            Sound sound = won
                    ? Sound.UI_TOAST_CHALLENGE_COMPLETE
                    : Sound.ENTITY_CHICKEN_HURT;

            player.playSound(player.getLocation(), sound, 100, 1);
        });
    }
}
