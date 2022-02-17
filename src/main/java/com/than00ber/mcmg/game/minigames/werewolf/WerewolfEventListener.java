package com.than00ber.mcmg.game.minigames.werewolf;

import com.than00ber.mcmg.game.EventListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class WerewolfEventListener extends EventListener<WerewolfGame> {

    protected WerewolfEventListener(WerewolfGame game) {
        super(game);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        GAME.PLAYERS_DEAD.put(player, GAME.PLAYERS_ALIVE.get(player));
    }
}
