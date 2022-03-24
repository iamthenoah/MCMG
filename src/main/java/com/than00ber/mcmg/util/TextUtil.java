package com.than00ber.mcmg.util;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.objects.MiniGameTeam;
import org.bukkit.ChatColor;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TextUtil {

    public static String addPluginPrefix(String message) {
        return ChatColor.GRAY + "[" + Main.PLUGIN_ID + "] " + ChatColor.RESET + message;
    }

    public static  <T> List<String> getMatching(String[] args, List<T> list) {
        return getMatching(args, list, s -> (String) s);
    }

    public static  <T> List<String> getMatching(String[] args, List<T> list, Function<T, String> function) {
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

    public static String formatGameTeam(MiniGameTeam team) {
        return team.getColor() + "" + ChatColor.BOLD + team.getDisplayName();
    }

    public static String[] formatObjective(MiniGameTeam team) {
        List<String> info = new ArrayList<>();
        info.add("> " + formatGameTeam(team));
        if (team.getObjective() != null) {
            info.add("\u0020\u0020Objective: " + team.getColor() + team.getObjective());
        }
        return info.toArray(new String[0]);
    }
}
