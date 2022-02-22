package com.than00ber.mcmg.game.events;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.game.minigames.WerewolfGame;
import com.than00ber.mcmg.init.GameTeams;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class WerewolfGameEventListener extends PluginEventListener<WerewolfGame> {

    public WerewolfGameEventListener(Main instance, WerewolfGame game) {
        super(instance, game);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        game.switchTeam(event.getEntity(), GameTeams.SPECTATORS);
    }
}
