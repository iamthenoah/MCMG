package com.than00ber.mcmg.game.events;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.game.minigames.PropHuntMiniGame;
import com.than00ber.mcmg.init.MiniGameTeams;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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

        if (minigame.isInTeam(player, MiniGameTeams.PROPS)) {
            boolean rightClicked = event.getAction().equals(Action.RIGHT_CLICK_BLOCK);

            if (rightClicked && !player.isSneaking()) {
                Material material = event.getClickedBlock().getType();
                MiscDisguise disguise = new MiscDisguise(DisguiseType.FALLING_BLOCK, material);
                DisguiseAPI.disguiseToAll(player, disguise);
                event.setCancelled(true);
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
}
