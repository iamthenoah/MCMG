package com.than00ber.mcmg.registries;

import com.google.common.collect.ImmutableList;
import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.core.ActionResult;
import com.than00ber.mcmg.core.MiniGameEngine;
import com.than00ber.mcmg.core.MiniGameItem;
import com.than00ber.mcmg.core.Registry;
import com.than00ber.mcmg.util.ChatUtil;
import com.than00ber.mcmg.util.ScheduleUtil;
import com.than00ber.mcmg.util.TextUtil;
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

public class Items {

    public static final Registry<MiniGameItem> ITEMS = new Registry<>(Registry.Registries.ITEMS);

    // Werewolf Items
    public static final MiniGameItem SURVIVORS_WEAPON = Items.ITEMS.register(() -> new MiniGameItem.Builder(Material.WOODEN_HOE)
            .setName("Survivor's weapon")
            .addTooltip(ChatColor.ITALIC + "Who said you can't trust hoes?")
            .unbreakable()
            .build());
    public static final MiniGameItem SURVIVORS_FOOD = Items.ITEMS.register(() -> new MiniGameItem.Builder(Material.COOKED_SALMON)
            .setName("Survivor's Fish")
            .build());
    public static final MiniGameItem RULE_BOOK = Items.ITEMS.register(() -> new MiniGameItem.Builder(Material.WRITTEN_BOOK)
            .setName("WWRPG Rule Book")
            .setColor(ChatColor.YELLOW)
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
            .build());

    // HideNSeek Items
    public static final MiniGameItem SEEKERS_AXE = Items.ITEMS.register(() -> new MiniGameItem.Builder(Material.GOLDEN_AXE)
            .setName("Seeker's Axe")
            .setColor(ChatColor.YELLOW)
            .unbreakable()
            .build());
    public static final MiniGameItem SEEKERS_BOW = Items.ITEMS.register(() -> new MiniGameItem.Builder(Material.BOW)
            .setName("Seeker's Bow")
            .setColor(ChatColor.YELLOW)
            .unbreakable()
            .build());

    // PropHunt Items
    public static final MiniGameItem HUNTERS_SWORD = Items.ITEMS.register(() -> new MiniGameItem.Builder(Material.IRON_SWORD)
            .setName("Hunter's Sword")
            .setColor(ChatColor.AQUA)
            .unbreakable().build());
    public static final MiniGameItem HUNTERS_BOW = Items.ITEMS.register(() -> new MiniGameItem.Builder(Material.BOW)
            .setName("Hunter's Bow")
            .setColor(ChatColor.AQUA)
            .unbreakable()
            .build());
    public static final MiniGameItem HUNTERS_ARROW = Items.ITEMS.register(() -> new MiniGameItem.Builder(Material.ARROW)
            .setName("Hunter's Arrow")
            .setColor(ChatColor.AQUA)
            .build());
    public static final MiniGameItem PROP_COMPASS = Items.ITEMS.register(() -> new MiniGameItem.Builder(Material.COMPASS)
            .setName("Prop Compass")
            .setColor(ChatColor.DARK_AQUA)
            .setStartingCooldown(30)
            .addTooltip("Reveals the location of the closest props.")
            .setMeta(() -> {
                CompassMeta meta = (CompassMeta) new ItemStack(Material.COMPASS).getItemMeta();
                meta.setLodestone(null);
                meta.setLodestoneTracked(false);
                return meta;
            })
            .onToggle(30, 30, 50, action -> {
                Player player = action.getEvent().getPlayer();
                double distance = Double.POSITIVE_INFINITY;
                Player target = null;

                double range = action.getRange();
                for (Entity entity : player.getNearbyEntities(range, range, range)) {
                    if (!(entity instanceof Player)) continue;
                    double to = player.getLocation().distance(entity.getLocation());
                    if (to > distance) continue;
                    if (!Main.MINIGAME_ENGINE.getCurrentGame().isInTeam((Player) entity, Teams.PROPS)) continue;
                    distance = to;
                    target = (Player) entity;
                }

                if (target != null) {
                    Player prop = target;
                    ItemStack item = player.getInventory().getItemInMainHand();
                    CompassMeta meta = (CompassMeta) item.getItemMeta();
                    prop.playSound(prop.getLocation(), Sound.ENTITY_ENDERMAN_STARE, 1, 1);

                    ScheduleUtil.doWhile(action.getDuration() , 10, () -> {
                        String title = ChatColor.BOLD + "A Hunter sees you!";
                        prop.sendTitle(ChatColor.GOLD + title, "", 0, 5, 5);
                    });

                    ScheduleUtil.doWhile(action.getDuration() , 5, () -> {
                        ChatUtil.toActionBar(prop, ChatColor.GOLD + "Your position is compromised!");
                        meta.setLodestone(prop.getLocation());
                        item.setItemMeta(meta);
                    }, () -> {
                        ChatUtil.toActionBar(prop, ChatColor.GREEN + "You are now hidden again...");
                        meta.setLodestone(null);
                        item.setItemMeta(meta);
                    });
                    return ActionResult.info("Compass pointing at " + TextUtil.formatPlayer(target));
                }
                return ActionResult.warn("No props found...");
            })
            .build());
    public static final MiniGameItem STUN_INK = Items.ITEMS.register(() -> new MiniGameItem.Builder(Material.INK_SAC)
            .setName("Stun Juice")
            .setColor(ChatColor.DARK_AQUA)
            .addTooltip("Blinds any nearby hunter for a brief moment.")
            .onToggle(3, 30, 5, action -> {
                Player player = action.getEvent().getPlayer();
                int range = action.getRange();
                List<Entity> entities = player.getNearbyEntities(range, range, range);

                for (Entity entity : entities) {
                    if (entity instanceof Player victim) {
                        if (Main.MINIGAME_ENGINE.getCurrentGame().isInTeam(victim, Teams.HUNTERS)) {
                            victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, action.getDuration() + 20, 5));
                            victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, action.getDuration() * 20, 5));
                        }
                    }
                }

                return ActionResult.info("Stunned " + entities.size() + " hunters.");

            })
            .build());
    public static final MiniGameItem GLOW_DUST = Items.ITEMS.register(() -> new MiniGameItem.Builder(Material.GLOWSTONE_DUST)
            .setName("Glow Dust")
            .setColor(ChatColor.YELLOW)
            .addTooltip("Reveals all hunters in the game for a brief moment.")
            .onToggle(30, 30, 30, action -> {
                Player player = action.getEvent().getPlayer();
                int range = action.getRange();
                List<Entity> entities = player.getNearbyEntities(range, range, range);

                 for (Entity entity : entities) {
                    if (entity instanceof Player target) {
                        if (Main.MINIGAME_ENGINE.getCurrentGame().isInTeam(target, Teams.HUNTERS)) {
                            target.setGlowing(true);
                        }
                    }
                }

                return ActionResult.info("Revealed " + entities.size() + " hunters.");
            }, action -> {
                int range = action.getRange();
                for (Entity entity : action.getEvent().getPlayer().getNearbyEntities(range, range, range)) {
                    if (entity instanceof Player player) {
                        if (Main.MINIGAME_ENGINE.getCurrentGame().isInTeam(player, Teams.HUNTERS)) {
                            player.setGlowing(false);
                        }
                    }
                }
            })
            .build());
    public static final MiniGameItem TELEPORTER = Items.ITEMS.register(() -> new MiniGameItem.Builder(Material.FEATHER)
            .setName("Teleporter")
            .setColor(ChatColor.DARK_PURPLE)
            .setStartingCooldown(30)
            .addTooltip("Teleports you straight to the pointed direction.")
            .onTrigger(30, 100, action -> {
                Player player = action.getEvent().getPlayer();
                Location eyeLoc = player.getEyeLocation();
                Vector eyeDirection = eyeLoc.getDirection();

                RayTraceResult ray = Main.WORLD.rayTraceBlocks(eyeLoc, eyeDirection, action.getRange());

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
                            if (count == 0) return ActionResult.warn("Can't teleport here.");
                            destination.add(0, 1, 0);
                            count--;
                        }

                        player.teleport(destination.add(0.5, 0, 0.5).setDirection(eyeDirection));
                    }
                    return ActionResult.success();
                }
                return ActionResult.warn("Too far away...");
            })
            .build());
    public static final MiniGameItem COCAINE = Items.ITEMS.register(() -> new MiniGameItem.Builder(Material.SUGAR)
            .setName("Cocaine")
            .setColor(ChatColor.BLUE)
            .addTooltip("Gives you extreme speed for a brief moment.")
            .onToggle(30, 30, action -> {
                Player player = action.getEvent().getPlayer();
                PotionEffect potion = new PotionEffect(PotionEffectType.SPEED, action.getDuration() * 20, 10);
                player.addPotionEffect(potion);
                return ActionResult.info("Speed will last for " + action.getDuration() + " seconds");
            })
            .build());
    public static final MiniGameItem PROP_RANDOMIZER = Items.ITEMS.register(() -> new MiniGameItem.Builder(Material.FLOWER_POT)
            .setName("Prop Randomizer")
            .setColor(ChatColor.LIGHT_PURPLE)
            .setStartingCooldown(30)
            .addTooltip("Changes the appearance of props with any nearby block.")
            .onTrigger(30, action -> {
                ImmutableList<Player> players = Main.MINIGAME_ENGINE.getCurrentGame().getAllInTeam(Teams.PROPS);
                for (Player player : players) {
                    Random r = new Random();
                    Material material = Material.AIR;
                    Location loc = player.getLocation();

                    int maxCount = 32;
                    while (material == Material.AIR || material == Material.VOID_AIR) {
                        if (maxCount == 0) continue;
                        maxCount--;
                        int x = loc.getBlockX() + r.nextInt(4) - 2;
                        int y = loc.getBlockY() + r.nextInt(2);
                        int z = loc.getBlockZ() + r.nextInt(4) - 2;
                        Location pos = new Location(player.getWorld(), x, y, z);
                        material = Main.WORLD.getBlockAt(pos).getType();
                    }

                    MiscDisguise disguise = new MiscDisguise(DisguiseType.FALLING_BLOCK, material);
                    DisguiseAPI.disguiseToAll(player, disguise);
                    DisguiseAPI.setActionBarShown(player, false);
                    String title = ChatColor.GOLD + "Your appearance changed!";
                    String subtitle = "A hunter has changed your appearance";
                    player.sendTitle(title, subtitle, 0, 30, 5);
                }
                return ActionResult.info(players.size() + " props have changed appearance.");
            })
            .build());
}
