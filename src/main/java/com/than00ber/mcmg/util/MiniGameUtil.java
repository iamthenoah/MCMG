package com.than00ber.mcmg.util;

import com.google.common.collect.ImmutableMap;
import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.core.MiniGameItem;
import com.than00ber.mcmg.core.MiniGameTeam;
import com.than00ber.mcmg.core.WinCondition;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MiniGameUtil {

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

    public static void resetPlayer(Player player) {
        DisguiseAPI.undisguiseToAll(player);
        player.setCollidable(true);
        player.setInvisible(false);
        player.setGlowing(false);
        player.setLevel(0);
        player.setExp(0);
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        player.setHealth(20);
        for (PotionEffect potion : player.getActivePotionEffects()) {
            player.removePotionEffect(potion.getType());
        }
    }

    public static void sendToGameSpawn(Player player) {
        player.teleport(Main.MINIGAME_ENGINE.getCurrentGame().getOptions().getPlaygroundSpawn());
    }

    public static void clearMonsters() {
        for (Entity entity : Main.WORLD.getEntities()) {
            if (entity instanceof Monster) entity.remove();
        }
    }


    public static void prepareWorld(boolean isGameEnding) {
        MiniGameUtil.clearMonsters();

        Main.WORLD.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, isGameEnding);
        Main.WORLD.setGameRule(GameRule.DO_WEATHER_CYCLE, isGameEnding);
        Main.WORLD.setGameRule(GameRule.MOB_GRIEFING, isGameEnding);
        Main.WORLD.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, isGameEnding);
        Main.WORLD.setGameRule(GameRule.DO_ENTITY_DROPS, isGameEnding);
        Main.WORLD.setGameRule(GameRule.SHOW_DEATH_MESSAGES, isGameEnding);
        Main.WORLD.setGameRule(GameRule.LOG_ADMIN_COMMANDS, isGameEnding);
        Main.WORLD.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, !isGameEnding);
        Main.WORLD.setGameRule(GameRule.KEEP_INVENTORY, !isGameEnding);

        Main.WORLD.setDifficulty(Difficulty.NORMAL);
        Main.WORLD.setThundering(false);
        Main.WORLD.setStorm(false);
        Main.WORLD.setTime(6000);
    }

    public static void disguiseAsBlock(Player player, Block block) {
        player.setInvisible(true);
        Sound sound = block.getBlockData().getSoundGroup().getPlaceSound();
        Material material = block.getType();
        player.playSound(player.getLocation(), sound, 1, 1);

        MiscDisguise disguise = new MiscDisguise(DisguiseType.FALLING_BLOCK, material);
        DisguiseAPI.setActionBarShown(player, false);
        DisguiseAPI.disguiseToAll(player, disguise);

        String name = material.name().replace('_', ' ');
        String formatted = ChatColor.ITALIC + WordUtils.capitalize(name.toLowerCase());
        String message = ChatColor.RESET + "You are disguised as a " + ChatColor.YELLOW + formatted;
        ChatUtil.toActionBar(player, message);
    }

    public static void giveMiniGameItem(Player player, MiniGameItem item) {
        giveMiniGameItem(player, item, 1);
    }

    public static void giveMiniGameItem(Player player, MiniGameItem item, int amount) {
        ItemStack stack = item.toItemStack();
        stack.setAmount(amount);
        player.getInventory().addItem(stack);
        player.setCooldown(stack.getType(), item.getStartingCooldown());
    }

    public static void giveMiniGameItemAt(Player player, MiniGameItem item, int index) {
        giveMiniGameItemAt(player, item, 1, index);
    }

    public static void giveMiniGameItemAt(Player player, MiniGameItem item, int amount, int index) {
        ItemStack stack = item.toItemStack();
        stack.setAmount(amount);
        player.getInventory().setItem(index, stack);
        player.setCooldown(stack.getType(), item.getStartingCooldown());
    }
}
