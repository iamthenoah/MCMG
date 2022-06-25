package com.than00ber.mcmg.init;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.MiniGameEngine;
import com.than00ber.mcmg.MiniGameItem;
import com.than00ber.mcmg.minigames.PropHuntMiniGame;
import com.than00ber.mcmg.util.ChatUtil;
import com.than00ber.mcmg.util.ScheduleUtil;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MiniGameItems {

    /**
     * Werewolf Items
     */
    public static final MiniGameItem SURVIVORS_WEAPON = new MiniGameItem.Builder(Material.WOODEN_HOE)
            .setName("Survivor's weapon")
            .addTooltip(ChatColor.ITALIC + "Who said you can't trust hoes?")
            .unbreakable()
            .build();
    public static final MiniGameItem SURVIVORS_FOOD = new MiniGameItem.Builder(Material.COOKED_SALMON)
            .setName("Survivor's Fish")
            .build();
    public static final MiniGameItem RULE_BOOK = new MiniGameItem.Builder(Material.WRITTEN_BOOK)
            .setName(ChatColor.YELLOW + "WWRPG Rule Book")
            .setMeta(() -> {
                List<String> pages = new ArrayList<>();
                pages.add("This is a line");
                pages.add("This is another line");
                BookMeta meta = (BookMeta) new ItemStack(Material.WRITTEN_BOOK).getItemMeta();
                meta.setTitle(ChatColor.YELLOW + "WWRPG Rule Book");
                meta.setAuthor("The WWRPG Plugin");
                meta.setPages(pages);
                return meta;
            })
            .build();

    /**
     * HideNSeek Items
     */
    public static final MiniGameItem SEEKERS_AXE = new MiniGameItem.Builder(Material.GOLDEN_AXE)
            .setName(ChatColor.YELLOW + "Seeker's Axe")
            .unbreakable()
            .build();
    public static final MiniGameItem SEEKERS_BOW = new MiniGameItem.Builder(Material.BOW)
            .setName(ChatColor.YELLOW + "Seeker's Bow")
            .unbreakable()
            .build();

    /**
     * PropHunt Items
     */
    public static final MiniGameItem HUNTERS_SWORD = new MiniGameItem.Builder(Material.IRON_SWORD)
            .setName(ChatColor.AQUA + "Hunter's Sword")
            .unbreakable()
            .build();
    public static final MiniGameItem HUNTERS_BOW = new MiniGameItem.Builder(Material.BOW)
            .setName(ChatColor.AQUA + "Hunter's Bow")
            .unbreakable()
            .build();
    public static final MiniGameItem HUNTERS_ARROWS = new MiniGameItem.Builder(Material.ARROW)
            .setName(ChatColor.AQUA + "Hunter's Arrow")
            .build();
    public static final MiniGameItem PROP_COMPASS = new MiniGameItem.Builder(Material.COMPASS)
            .setName(ChatColor.DARK_AQUA + "Prop Compass")
            .addTooltip("Reveals the location of the closest props.")
            .setMeta(() -> {
                CompassMeta meta = (CompassMeta) new ItemStack(Material.COMPASS).getItemMeta();
                meta.setLodestone(null);
                meta.setLodestoneTracked(false);
                return meta;
            })
            .onToggled(PropHuntMiniGame.PROP_COMPASS_DURATION, PropHuntMiniGame.PROP_COMPASS_COOLDOWN, event -> {
                Player player = event.getPlayer();
                double range = (double) PropHuntMiniGame.PLAYGROUND_RADIUS.get() * 2;
                double distance = Double.POSITIVE_INFINITY;
                Player target = null;

                for (Entity entity : player.getNearbyEntities(range, range, range)) {
                    if (!(entity instanceof Player)) continue;
                    double to = player.getLocation().distance(entity.getLocation());
                    if (to > distance) continue;
                    if (!Main.MINIGAME_ENGINE.getCurrentGame().isInTeam((Player) entity, MiniGameTeams.PROPS)) continue;
                    distance = to;
                    target = (Player) entity;
                }

                if (target != null) {
                    Player prop = target;
                    ItemStack item = player.getInventory().getItemInMainHand();
                    CompassMeta meta = (CompassMeta) item.getItemMeta();
                    int duration = PropHuntMiniGame.PROP_COMPASS_DURATION.get() * 20;
                    prop.playSound(prop.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 1, 1);

                    ScheduleUtil.doWhile(duration, 10, () -> {
                        String title = ChatColor.BOLD + "A Hunter sees you!";
                        prop.sendTitle(ChatColor.GOLD + title, "", 0, 5, 5);
                    });

                    ScheduleUtil.doWhile(duration, 5, () -> {
                        ChatUtil.toActionBar(prop, ChatColor.GOLD + "Your position is compromised!");
                        meta.setLodestone(prop.getLocation());
                        item.setItemMeta(meta);
                    }, () -> {
                        ChatUtil.toActionBar(prop, ChatColor.GREEN + "You are now hidden again...");
                        meta.setLodestone(null);
                        item.setItemMeta(meta);
                    });
                }
            })
            .build();
    public static final MiniGameItem STUN_INK = new MiniGameItem.Builder(Material.INK_SAC)
            .setName(ChatColor.DARK_AQUA + "Stun Juice")
            .addTooltip("Blinds any nearby hunter for a brief moment.")
            .onTriggered(PropHuntMiniGame.STUN_JUICE_COOLDOWN, event -> {
                Player player = event.getPlayer();
                double range = PropHuntMiniGame.STUN_JUICE_RANGE.get();

                for (Entity entity : player.getNearbyEntities(range, range, range)) {
                    if (entity instanceof Player victim) {
                        if (Main.MINIGAME_ENGINE.getCurrentGame().isInTeam(victim, MiniGameTeams.HUNTERS)) {
                            int duration = PropHuntMiniGame.STUN_JUICE_DURATION.get() * 20;
                            victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration + 20, 5));
                            victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, 5));
                        }
                    }
                }
            })
            .build();
    public static final MiniGameItem GLOW_DUST = new MiniGameItem.Builder(Material.GLOWSTONE_DUST)
            .setName(ChatColor.YELLOW + "Glow Dust")
            .addTooltip("Reveals all hunters in the game for a brief moment.")
            .onToggled(PropHuntMiniGame.GLOW_DUST_DURATION, PropHuntMiniGame.GLOW_DUST_COOLDOWN, event -> {
                int range = PropHuntMiniGame.GLOW_DUST_RANGE.get();

                for (Entity entity : event.getPlayer().getNearbyEntities(range, range, range)) {
                    if (entity instanceof Player player) {
                        if (Main.MINIGAME_ENGINE.getCurrentGame().isInTeam(player, MiniGameTeams.HUNTERS)) {
                            player.setGlowing(true);
                        }
                    }
                }
            }, event -> {
                int range = PropHuntMiniGame.GLOW_DUST_RANGE.get();

                for (Entity entity : event.getPlayer().getNearbyEntities(range, range, range)) {
                    if (entity instanceof Player player) {
                        if (Main.MINIGAME_ENGINE.getCurrentGame().isInTeam(player, MiniGameTeams.HUNTERS)) {
                            player.setGlowing(false);
                        }
                    }
                }
            })
            .build();
    public static final MiniGameItem TELEPORTER = new MiniGameItem.Builder(Material.FEATHER)
            .setName(ChatColor.DARK_PURPLE + "Teleporter")
            .addTooltip("Teleports you straight to the pointed direction.")
            .onTriggered(PropHuntMiniGame.TELEPORTER_COOLDOWN, event -> {
                Player player = event.getPlayer();
                Location eyeLoc = player.getEyeLocation();
                Vector eyeDirection = eyeLoc.getDirection();

                int range = PropHuntMiniGame.TELEPORTER_RANGE.get();
                RayTraceResult ray = Main.WORLD.rayTraceBlocks(eyeLoc, eyeDirection, range);

                if (ray != null && ray.getHitBlock() != null) {
                    Location destination = ray.getHitBlock().getLocation().add(0, 1, 0);

                    MiniGameEngine.Options options = Main.MINIGAME_ENGINE.getCurrentGame().getOptions();
                    Location spawn = options.getPlaygroundSpawn();
                    int r = options.getPlaygroundRadius() / 2 + 1;
                    Location p1 = new Location(Main.WORLD, spawn.getBlockX() - r, 0, spawn.getBlockZ() - r);
                    Location p2 = new Location(Main.WORLD, spawn.getBlockX() + r, 0, spawn.getBlockZ() + r);
                    int x = destination.getBlockX();
                    int z = destination.getBlockZ();

                    if (x > p1.getBlockX() && x < p2.getBlockX() && z > p1.getBlockZ() && z < p2.getBlockZ()) {
                        int count = 3;
                        while (player.getWorld().getBlockAt(destination).getType() != Material.AIR) {
                            if (count == 0) return;
                            destination.add(0, 1, 0);
                            count--;
                        }

                        player.teleport(destination.add(0.5, 0, 0.5).setDirection(eyeDirection));
                    }
                }
            })
            .build();
    public static final MiniGameItem COCAINE = new MiniGameItem.Builder(Material.SUGAR)
            .setName(ChatColor.BLUE + "Cocaine")
            .addTooltip("Gives you extreme speed for a brief moment.")
            .onToggled(PropHuntMiniGame.COCAINE_DURATION, PropHuntMiniGame.COCAINE_COOLDOWN, event -> {
                Player player = event.getPlayer();
                int duration = PropHuntMiniGame.COCAINE_DURATION.get() * 20;
                PotionEffect potion = new PotionEffect(PotionEffectType.SPEED, duration, 10);
                player.addPotionEffect(potion);
            })
            .build();
    public static final MiniGameItem PROP_RANDOMIZER = new MiniGameItem.Builder(Material.FLOWER_POT)
            .setName(ChatColor.LIGHT_PURPLE + "Prop Randomizer")
            .addTooltip("Changes the appears of all props with any random nearby block.")
            .onTriggered(PropHuntMiniGame.PROP_RANDOMIZER_COOLDOWN, event -> {
                for (Player player : Main.MINIGAME_ENGINE.getCurrentGame().getAllInTeam(MiniGameTeams.PROPS)) {
                    Random r = new Random();
                    Material material = Material.AIR;
                    Location loc = player.getLocation();

                    int maxCount = 32;
                    while (material == Material.AIR || material == Material.VOID_AIR) {
                        maxCount--;
                        int x = loc.getBlockX() + r.nextInt(4) - 2;
                        int y = loc.getBlockY() + r.nextInt(2);
                        int z = loc.getBlockZ() + r.nextInt(4) - 2;
                        Location pos = new Location(player.getWorld(), x, y, z);
                        material = Main.WORLD.getBlockAt(pos).getType();
                        if (maxCount == 0) return;
                    }

                    MiscDisguise disguise = new MiscDisguise(DisguiseType.FALLING_BLOCK, material);
                    DisguiseAPI.disguiseToAll(player, disguise);
                    DisguiseAPI.setActionBarShown(player, false);
                    String title = ChatColor.GOLD + "Your appearance changed!";
                    String subtitle = "A hunter has changed your appearance";
                    player.sendTitle(title, subtitle, 0, 30, 5);
                }
            })
            .build();
}
