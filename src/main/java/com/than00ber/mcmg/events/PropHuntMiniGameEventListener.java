package com.than00ber.mcmg.events;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.init.MiniGameItems;
import com.than00ber.mcmg.init.MiniGameTeams;
import com.than00ber.mcmg.minigames.PropHuntMiniGame;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
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

import java.util.Random;
import java.util.function.Supplier;

public class PropHuntMiniGameEventListener extends MiniGameEventListener<PropHuntMiniGame> {

    public PropHuntMiniGameEventListener(Main instance, PropHuntMiniGame game) {
        super(instance, game);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        minigame.switchTeam(event.getEntity(), MiniGameTeams.SPECTATORS);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (minigame.isInTeam(player, MiniGameTeams.PROPS) && !player.isSneaking()) {
            Action action = event.getAction();
            boolean rightClicked = action == Action.RIGHT_CLICK_BLOCK;

            if (rightClicked) {
                Block clickedBlock = event.getClickedBlock();

                if (clickedBlock != null) {
                    Material material = clickedBlock.getType();

                    if (!PropHuntMiniGame.ALLOW_BLOCKS.get() && material.isBlock()) return;
                    if (!PropHuntMiniGame.ALLOW_SPECIALS.get() && material.isTransparent()) return;

                    MiscDisguise disguise = new MiscDisguise(DisguiseType.FALLING_BLOCK, material);
                    DisguiseAPI.disguiseToAll(player, disguise);
                    event.setCancelled(true);
                }
            } else if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                Supplier<Integer> delta = () -> new Random().nextInt(4) - 2;
                Location loc = player.getLocation().add(delta.get(), delta.get(), delta.get());
                minigame.getCurrentPlayerRoles().keySet().forEach(p -> p.playSound(loc, Sound.ENTITY_CAT_AMBIENT, 1, 1));
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
            Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
                boolean hasArrow = player.getInventory().contains(MiniGameItems.HUNTERS_ARROWS.get());

                if (Main.MINIGAME_ENGINE.hasRunningGame() && !hasArrow) {
                    player.getInventory().setItem(8, MiniGameItems.HUNTERS_ARROWS.get());
                }
            }, 20 * 10);
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
