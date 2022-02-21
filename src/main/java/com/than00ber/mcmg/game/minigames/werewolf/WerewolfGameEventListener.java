package com.than00ber.mcmg.game.minigames.werewolf;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.game.EventListener;
import com.than00ber.mcmg.init.GameTeams;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class WerewolfGameEventListener extends EventListener<WerewolfGame> {

    protected WerewolfGameEventListener(Main instance, WerewolfGame game) {
        super(instance, game);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        game.switchTeam(event.getEntity(), GameTeams.SPECTATORS);
    }
}