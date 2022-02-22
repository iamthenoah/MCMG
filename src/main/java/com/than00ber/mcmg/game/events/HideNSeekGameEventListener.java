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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class HideNSeekGameEventListener extends PluginEventListener<HideNSeekGame> {

    public HideNSeekGameEventListener(Main instance, HideNSeekGame game) {
        super(instance, game);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        game.switchTeam(event.getEntity(), GameTeams.SEEKERS);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        MobDisguise disguise = new MobDisguise(DisguiseType.getType(entity));
        DisguiseAPI.disguiseToAll(player, disguise);
    }
}
