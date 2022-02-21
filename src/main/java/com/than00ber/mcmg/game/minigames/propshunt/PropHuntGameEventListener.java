package com.than00ber.mcmg.game.minigames.propshunt;

import com.than00ber.mcmg.game.EventListener;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PropHuntGameEventListener extends EventListener<PropHuntGame> {

    public PropHuntGameEventListener(PropHuntGame game) {
        super(game);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (GAME.PROPS.containsKey(player)) {
            DisguiseAPI.undisguiseToAll(player);
            GAME.HUNTERS.put(player, GAME.PROPS.remove(player));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Material material = event.getClickedBlock().getType();
            if (!material.isBlock() && GAME.solidsOnly()) return;

            MiscDisguise disguise = new MiscDisguise(DisguiseType.FALLING_BLOCK, material);
            DisguiseAPI.disguiseToAll(player, disguise);
        }
    }
}
