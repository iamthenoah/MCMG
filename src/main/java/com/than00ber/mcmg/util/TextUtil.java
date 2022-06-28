package com.than00ber.mcmg.util;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.core.MiniGameTeam;
import com.than00ber.mcmg.minigames.MiniGame;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TextUtil {

    public static String addPluginPrefix(String message) {
        return ChatColor.GRAY + "[" + Main.PLUGIN_ID + "] " + ChatColor.RESET + message;
    }

    public static <T> List<String> getMatching(String[] args, List<T> list) {
        return getMatching(args, list, s -> (String) s);
    }

    public static <T> List<String> getMatching(String[] args, List<T> list, Function<T, String> function) {
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

    public static String simplify(String text) {
        return ChatColor.stripColor(text
                .replaceAll("[-+.^:,']", "")
                .replaceAll(" ", "_")
                .toLowerCase()
        );
    }

    public static String formatPlayer(Player player) {
        return ChatColor.AQUA + player.getDisplayName() + ChatColor.RESET;
    }

    public static String formatGameTeam(MiniGameTeam team) {
        return team.getColor() + "" + ChatColor.BOLD + team.getVisibleName() + ChatColor.RESET;
    }

    public static String formatObjective(MiniGameTeam team) {
        return team.getObjective() != null
                ? formatGameTeam(team) + ChatColor.RESET + ": " + team.getObjective()
                : "You are in the " + formatGameTeam(team) + " team.";
    }

    public static String formatMiniGame(MiniGame minigame) {
        return formatMiniGame(minigame.getName());
    }

    public static String formatMiniGame(String name) {
        return ChatColor.BLUE + name + ChatColor.RESET;
    }
}
