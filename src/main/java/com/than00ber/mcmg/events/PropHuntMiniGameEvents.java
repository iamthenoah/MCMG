package com.than00ber.mcmg.events;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.minigames.PropHuntMiniGame;
import com.than00ber.mcmg.registries.Items;
import com.than00ber.mcmg.registries.Teams;
import com.than00ber.mcmg.util.ChatUtil;
import com.than00ber.mcmg.util.MiniGameUtil;
import com.than00ber.mcmg.util.ScheduleUtil;
import com.than00ber.mcmg.util.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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

public class PropHuntMiniGameEvents extends MiniGameEvents<PropHuntMiniGame> {

    public PropHuntMiniGameEvents(Main instance, PropHuntMiniGame game) {
        super(instance, game);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        minigame.switchTeam(player, Teams.SPECTATORS);
        int count = minigame.getAllInTeam(Teams.PROPS).size();

        if (count > 0) {
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

        if (minigame.isInTeam(player, Teams.PROPS)) {
            if (player.isSneaking()) return;
            boolean isHoldItem = player.getInventory().getItemInMainHand().getType() != Material.AIR;

            if (action == Action.RIGHT_CLICK_BLOCK && !isHoldItem) {
                if (event.getClickedBlock() != null) {
                    event.setCancelled(true);

                    Material material = event.getClickedBlock().getType();
                    if (!PropHuntMiniGame.ALLOW_BLOCKS.get() && material.isBlock()) return;
                    if (!PropHuntMiniGame.ALLOW_SPECIALS.get() && material.isTransparent()) return;

                    MiniGameUtil.disguiseAsBlock(player, event.getClickedBlock());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!PropHuntMiniGame.PROPS_IN_WATER.get()) {
            Player player = event.getPlayer();

            if (minigame.isInTeam(player, Teams.PROPS) && player.isInWater()) {
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
                boolean hasArrow = player.getInventory().contains(Items.HUNTERS_ARROW.toItemStack());

                if (Main.MINIGAME_ENGINE.hasRunningGame() && !hasArrow) {
                    player.getInventory().setItem(8, Items.HUNTERS_ARROW.toItemStack());
                }
            });
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getHitBlock() != null) {
            event.getEntity().remove();
        } else if (event.getHitEntity() instanceof Player player) {
            if (minigame.isInTeam(player, Teams.PROPS)) {
                player.setHealth(0);
            }
        }
    }
}
