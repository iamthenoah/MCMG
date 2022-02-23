package com.than00ber.mcmg.game.events;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.game.minigames.HideNSeekGame;
import com.than00ber.mcmg.init.GameTeams;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class HideNSeekGameEventListener extends PluginEventListener<HideNSeekGame> {

    public HideNSeekGameEventListener(Main instance, HideNSeekGame game) {
        super(instance, game);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (game.isInTeam(player, GameTeams.SEEKERS)) {
            game.switchTeam(player, GameTeams.SPECTATORS);
        } else {
            game.switchTeam(player, GameTeams.SEEKERS);
            game.sendToGameSpawn(player);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (game.isInTeam(player, GameTeams.HIDERS)) {
            Entity entity = event.getRightClicked();
            MobDisguise disguise = new MobDisguise(DisguiseType.getType(entity));
            DisguiseAPI.disguiseToAll(player, disguise);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity assaulter = event.getDamager();

        if (assaulter instanceof Player player) {

            if (game.isInTeam(player, GameTeams.SEEKERS)) {
                Entity victim = event.getEntity();

                if (!(victim instanceof Player)) {
                    player.damage(game.getDamagePenalty());
                }
            }
        }
    }
}
