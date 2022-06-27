package com.than00ber.mcmg.events;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.minigames.HideNSeekMiniGame;
import com.than00ber.mcmg.registries.Teams;
import com.than00ber.mcmg.util.MiniGameUtil;
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

public class HideNSeekMiniGameEvents extends MiniGameEvents<HideNSeekMiniGame> {

    public HideNSeekMiniGameEvents(Main instance, HideNSeekMiniGame game) {
        super(instance, game);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (minigame.isInTeam(player, Teams.SEEKERS)) {
            minigame.switchTeam(player, Teams.SPECTATORS);
        } else {
            minigame.switchTeam(player, Teams.SEEKERS);
            MiniGameUtil.sendToGameSpawn(player);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();

        if (minigame.isInTeam(player, Teams.HIDERS)) {
            Entity entity = event.getRightClicked();
            if ((entity.getType() != HideNSeekMiniGame.ENTITY_TYPE.get())) return;

            MobDisguise disguise = new MobDisguise(DisguiseType.getType(entity));
            disguise.setViewSelfDisguise(HideNSeekMiniGame.VIEW_DISGUISE.get());
            DisguiseAPI.disguiseToAll(player, disguise);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity assaulter = event.getDamager();

        if (assaulter instanceof Player player) {

            if (minigame.isInTeam(player, Teams.SEEKERS)) {
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

            if (minigame.isInTeam(player, Teams.SEEKERS)) {
                event.setCancelled(true);
            }
        }
    }
}
