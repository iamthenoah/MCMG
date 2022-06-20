package com.than00ber.mcmg.events;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.init.MiniGameItems;
import com.than00ber.mcmg.init.MiniGameTeams;
import com.than00ber.mcmg.minigames.PropHuntMiniGame;
import com.than00ber.mcmg.util.ChatUtil;
import com.than00ber.mcmg.util.TextUtil;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class PropHuntMiniGameEventListener extends MiniGameEventListener<PropHuntMiniGame> {

    public PropHuntMiniGameEventListener(Main instance, PropHuntMiniGame game) {
        super(instance, game);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        minigame.switchTeam(player, MiniGameTeams.SPECTATORS);

        AtomicInteger count = new AtomicInteger();
        minigame.getCurrentPlayerRoles().values().forEach(t -> {
            if (t == MiniGameTeams.PROPS) count.getAndIncrement();
        });

        if (count.get() > 0) {
            String remaining = ChatColor.YELLOW + String.valueOf(count) + ChatColor.RESET;
            ChatUtil.toAll(TextUtil.formatPlayer(player) + " has been eliminated.");
            ChatUtil.toAll(remaining + " props remaining.");
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (minigame.isInTeam(player, MiniGameTeams.HUNTERS)) {
            hunterInteract(player, event);
        } else if (minigame.isInTeam(player, MiniGameTeams.PROPS)) {
            propInteract(player, event);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!PropHuntMiniGame.PROPS_IN_WATER.get()) {
            Player player = event.getPlayer();

            if (minigame.isInTeam(player, MiniGameTeams.PROPS) && player.isInWater()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 10, 10));
            }
        }
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player player) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
                boolean hasArrow = player.getInventory().contains(MiniGameItems.HUNTERS_ARROWS.get());

                if (Main.MINIGAME_ENGINE.hasRunningGame() && !hasArrow) {
                    player.getInventory().setItem(8, MiniGameItems.HUNTERS_ARROWS.get());
                }
            }, 20L * PropHuntMiniGame.ARROW_REPLENISH_COOLDOWN.get());
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getHitBlock() != null) {
            event.getEntity().remove();
        } else if (event.getHitEntity() instanceof Player player) {
            if (minigame.isInTeam(player, MiniGameTeams.PROPS)) {
                player.setHealth(0);
            }
        }
    }


    private void hunterInteract(Player player, PlayerInteractEvent event) {
        ItemStack item = player.getInventory().getItemInMainHand();
        Material material = item.getType();

        if (material == Material.COMPASS) {
            CompassMeta meta = (CompassMeta) item.getItemMeta();

            if (!player.hasCooldown(material) && !meta.hasLodestone()) {
                Player prop = getNearestProp(player);
                event.setCancelled(true);

                // TODO - add schedule util class

                int chatId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.INSTANCE, () -> {
                    String title = ChatColor.BOLD + "A Hunter sees you!";
                    prop.sendTitle(ChatColor.GOLD + title, "", 0, 5, 5);
                }, 0, 10);

                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.INSTANCE, () -> {
                    Bukkit.getScheduler().cancelTask(chatId);
                }, 100);

                int compassId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.INSTANCE, () -> {
                    meta.setLodestone(prop.getLocation());
                    item.setItemMeta(meta);
                    ChatUtil.toActionBar(prop, ChatColor.GOLD + "Your position is compromised!");
                }, 0, 5);

                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.INSTANCE, () -> {
                    Bukkit.getScheduler().cancelTask(compassId);
                    player.setCooldown(material, PropHuntMiniGame.COMPASS_COOLDOWN.get() * 20);
                    meta.setLodestone(null);
                    item.setItemMeta(meta);
                    ChatUtil.toActionBar(prop, ChatColor.GREEN + "You are now hidden again...");
                }, PropHuntMiniGame.COMPASS_DURATION.get() * 20);
            }
        }
    }

    private void propInteract(Player player, PlayerInteractEvent event) {
        if (player.isSneaking()) return;
        Action action = event.getAction();

        if (action == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();

            if (clickedBlock != null) {
                Material material = clickedBlock.getType();

                if (!PropHuntMiniGame.ALLOW_BLOCKS.get() && material.isBlock()) return;
                if (!PropHuntMiniGame.ALLOW_SPECIALS.get() && material.isTransparent()) return;

                MiscDisguise disguise = new MiscDisguise(DisguiseType.FALLING_BLOCK, material);
                DisguiseAPI.disguiseToAll(player, disguise);

                String name = material.name().replace('_', ' ');
                String formatted = ChatColor.ITALIC + WordUtils.capitalize(name.toLowerCase());
                String message = ChatColor.RESET + "You are disguised as a " + ChatColor.YELLOW + formatted;
                ChatUtil.toActionBar(player, message);

                event.setCancelled(true);
            }
        } else if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
            Supplier<Integer> delta = () -> new Random().nextInt(4) - 2;
            Location loc = player.getLocation().add(delta.get(), delta.get(), delta.get());
            List<Sound> sounds = List.of(
                    Sound.ENTITY_CAT_AMBIENT,
                    Sound.ENTITY_COW_AMBIENT,
                    Sound.ENTITY_CAT_AMBIENT,
                    Sound.ENTITY_PIG_AMBIENT,
                    Sound.ENTITY_BAT_AMBIENT,
                    Sound.ENTITY_WOLF_AMBIENT,
                    Sound.ENTITY_CHICKEN_AMBIENT,
                    Sound.ENTITY_SHEEP_AMBIENT,
                    Sound.ENTITY_VILLAGER_AMBIENT
            );
            Sound sound = sounds.get(new Random().nextInt(sounds.size() - 1));
            minigame.getCurrentPlayerRoles().keySet().forEach(p -> p.playSound(loc, sound, 1, 1));
        }
    }

    private Player getNearestProp(Player player) {
        double range = (double) PropHuntMiniGame.PLAYGROUND_RADIUS.get() * 2;
        double distance = Double.POSITIVE_INFINITY;
        Player target = null;
        for (Entity entity : player.getNearbyEntities(range, range, range)) {
            if (!(entity instanceof Player)) continue;
            double to = player.getLocation().distance(entity.getLocation());
            if (to > distance) continue;
            if (!minigame.isInTeam((Player) entity, MiniGameTeams.PROPS)) continue;
            distance = to;
            target = (Player) entity;
        }
        return target;
    }
}
