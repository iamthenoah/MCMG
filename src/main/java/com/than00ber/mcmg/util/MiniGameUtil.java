package com.than00ber.mcmg.util;

import com.than00ber.mcmg.Main;
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
import org.bukkit.potion.PotionEffect;

public class MiniGameUtil {

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
}
