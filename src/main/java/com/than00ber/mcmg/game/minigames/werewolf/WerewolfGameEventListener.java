package com.than00ber.mcmg.game.minigames.werewolf;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.game.EventListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class WerewolfGameEventListener extends EventListener<WerewolfGame> {

    protected WerewolfGameEventListener(Main instance, WerewolfGame game) {
        super(instance, game);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        game.PLAYERS_DEAD.put(player, game.PLAYERS_ALIVE.get(player));
    }
}
