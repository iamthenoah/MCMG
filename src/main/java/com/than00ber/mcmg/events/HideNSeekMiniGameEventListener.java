package com.than00ber.mcmg.events;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.init.MiniGameTeams;
import com.than00ber.mcmg.minigames.HideNSeekMiniGame;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class HideNSeekMiniGameEventListener extends MiniGameEventListener<HideNSeekMiniGame> {

    public HideNSeekMiniGameEventListener(Main instance, HideNSeekMiniGame game) {
        super(instance, game);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (minigame.isInTeam(player, MiniGameTeams.SEEKERS)) {
            minigame.switchTeam(player, MiniGameTeams.SPECTATORS);
        } else {
            minigame.switchTeam(player, MiniGameTeams.SEEKERS);
            minigame.sendToGameSpawn(player);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();

        if (minigame.isInTeam(player, MiniGameTeams.HIDERS)) {
            Entity entity = event.getRightClicked();
            if (entity instanceof Player) return;

            MobDisguise disguise = new MobDisguise(DisguiseType.getType(entity));
            disguise.setViewSelfDisguise(false);
            DisguiseAPI.disguiseToAll(player, disguise);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity assaulter = event.getDamager();

        if (assaulter instanceof Player player) {

            if (minigame.isInTeam(player, MiniGameTeams.SEEKERS)) {
                Entity victim = event.getEntity();

                if (!(victim instanceof Player)) {
                    player.damage(HideNSeekMiniGame.DAMAGE_PENALTY.get());
                }
            }
        }
    }

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player player) {

            if (minigame.isInTeam(player, MiniGameTeams.SEEKERS)) {
                event.setCancelled(true);
            }
        }
    }
}
