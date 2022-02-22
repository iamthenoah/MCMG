package com.than00ber.mcmg.game.events;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.game.minigames.PropHuntGame;
import com.than00ber.mcmg.init.GameTeams;
import com.than00ber.mcmg.objects.GameTeam;
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
        game.switchTeam(event.getEntity(), GameTeams.HUNTERS);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Material material = event.getClickedBlock().getType();
            MiscDisguise disguise = new MiscDisguise(DisguiseType.FALLING_BLOCK, material);
            DisguiseAPI.disguiseToAll(player, disguise);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!game.canHideInWater()) {
            Player player = event.getPlayer();
            GameTeam team = game.getParticipants().get(player);

            if (team != null && team.equals(GameTeams.PROPS) && player.isInWater()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 10, 10));
            }
        }
    }
}
