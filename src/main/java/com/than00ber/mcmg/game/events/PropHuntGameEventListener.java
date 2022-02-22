package com.than00ber.mcmg.game.events;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.game.minigames.PropHuntGame;
import com.than00ber.mcmg.init.GameTeams;
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

public class PropHuntGameEventListener extends PluginEventListener<PropHuntGame> {

    public PropHuntGameEventListener(Main instance, PropHuntGame game) {
        super(instance, game);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (game.isInTeam(player, GameTeams.HUNTERS)) {
            game.switchTeam(player, GameTeams.SPECTATORS);
        } else {
            game.switchTeam(player, GameTeams.HUNTERS);
            game.sendToGameSpawn(player);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        boolean rightClicked = event.getAction().equals(Action.RIGHT_CLICK_BLOCK);

        if (rightClicked && game.isInTeam(player, GameTeams.PROPS)) {
            Material material = event.getClickedBlock().getType();
            MiscDisguise disguise = new MiscDisguise(DisguiseType.FALLING_BLOCK, material);
            DisguiseAPI.disguiseToAll(player, disguise);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!game.canHideInWater()) {
            Player player = event.getPlayer();

            if (game.isInTeam(player, GameTeams.PROPS) && player.isInWater()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 10, 10));
            }
        }
    }
}
