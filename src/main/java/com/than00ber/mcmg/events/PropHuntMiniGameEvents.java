package com.than00ber.mcmg.events;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.init.MiniGameItems;
import com.than00ber.mcmg.init.MiniGameTeams;
import com.than00ber.mcmg.minigames.PropHuntMiniGame;
import com.than00ber.mcmg.util.ChatUtil;
import com.than00ber.mcmg.util.ScheduleUtil;
import com.than00ber.mcmg.util.TextUtil;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class PropHuntMiniGameEvents extends MiniGameEventListener<PropHuntMiniGame> {

    public PropHuntMiniGameEvents(Main instance, PropHuntMiniGame game) {
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
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (minigame.isInTeam(player, MiniGameTeams.PROPS)) {
            if (player.isSneaking()) return;
            boolean isHoldItem = player.getInventory().getItemInMainHand().getType() != Material.AIR;

            if (action == Action.RIGHT_CLICK_BLOCK && !isHoldItem) {
                if (event.getClickedBlock() != null) {
                    event.setCancelled(true);

                    Material material = event.getClickedBlock().getType();
                    if (!PropHuntMiniGame.ALLOW_BLOCKS.get() && material.isBlock()) return;
                    if (!PropHuntMiniGame.ALLOW_SPECIALS.get() && material.isTransparent()) return;

                    MiscDisguise disguise = new MiscDisguise(DisguiseType.FALLING_BLOCK, material);
                    DisguiseAPI.disguiseToAll(player, disguise);

                    String name = material.name().replace('_', ' ');
                    String formatted = ChatColor.ITALIC + WordUtils.capitalize(name.toLowerCase());
                    String message = ChatColor.RESET + "You are disguised as a " + ChatColor.YELLOW + formatted;
                    ChatUtil.toActionBar(player, message);
                }
            } else if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                event.setCancelled(true);

                Supplier<Integer> delta = () -> new Random().nextInt(4) - 2;
                Location location = player.getLocation().add(delta.get(), delta.get(), delta.get());
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
                minigame.getCurrentPlayerRoles().keySet().forEach(p -> p.playSound(location, sound, 1, 1));
            }
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
            int cooldown = PropHuntMiniGame.ARROW_REPLENISH_COOLDOWN.get() * 20;
            player.setCooldown(event.getBow().getType(), cooldown);

            ScheduleUtil.doDelayed(cooldown, () -> {
                boolean hasArrow = player.getInventory().contains(MiniGameItems.HUNTERS_ARROWS.get());

                if (Main.MINIGAME_ENGINE.hasRunningGame() && !hasArrow) {
                    player.getInventory().setItem(8, MiniGameItems.HUNTERS_ARROWS.get());
                }
            });
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
}
