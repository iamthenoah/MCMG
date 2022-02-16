package com.than00ber.mcmg.game.minigames.werewolf;

import com.than00ber.mcmg.game.EventListener;
import com.than00ber.mcmg.util.ChatUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class WerewolfEventListener extends EventListener<WerewolfGame> {

    protected WerewolfEventListener(WerewolfGame game) {
        super(game);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        ChatUtil.toAll("DED " + event.getEntity().getDisplayName());
    }
}
