package com.than00ber.mcmg.game.minigames.propshunt;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.game.EventListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class PropsHuntGameEventListener extends EventListener<PropsHuntGame> {

    public PropsHuntGameEventListener(PropsHuntGame game) {
        super(game);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.INSTANCE, () -> {
            int hidingTickThreshold = 40; // make this configurable

            for (Prop prop : GAME.PROPS.keySet()) {
                prop.increaseTimer(hidingTickThreshold);
            }
        }, 0, 1);
    }

    public static Block SELECTED_BLOCK = null;

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        double xFrom = Math.round(event.getFrom().getX());
        double yFrom = Math.round(event.getFrom().getY());
        double zFrom = Math.round(event.getFrom().getZ());
        double xTo = Math.round(event.getTo().getX());
        double yTo = Math.round(event.getTo().getY());
        double zTo = Math.round(event.getTo().getZ());

        boolean hasMoved = xFrom != xTo || yFrom != yTo || zFrom != zTo;
        if (!hasMoved) return;

        if (SELECTED_BLOCK != null) {
            Player player = event.getPlayer();
            player.setInvisible(true);
            Location location = player.getLocation();
            BlockData data = SELECTED_BLOCK.getBlockData();

            FallingBlock fallingBlock = GAME.getWorld().spawnFallingBlock(location, data);
            fallingBlock.setVelocity(new Vector(0, 0, 0));
            fallingBlock.setGravity(false);
            fallingBlock.setDropItem(false);

            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.INSTANCE, fallingBlock::remove, 5);
        }

        for (Prop prop : GAME.PROPS.keySet()) {
            if (prop.getPlayer() == event.getPlayer()) {
                prop.setHidingSpot(event.getPlayer().getLocation()).resetTimer();
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
//        if (event.getHand() == EquipmentSlot.OFF_HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = event.getClickedBlock();
        BlockData data = block.getBlockData();
        Location location = block.getLocation();

        FallingBlock selector = GAME.getWorld().spawnFallingBlock(location.add(.5, -.02, .5), data);
        selector.setVelocity(new Vector(0, 0, 0));
        selector.setGravity(false);
        selector.setDropItem(false);
        selector.setGlowing(true);

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.INSTANCE, () -> {
            selector.remove();
            GAME.getWorld().setBlockData(location, data);
        }, 3);

        SELECTED_BLOCK = event.getClickedBlock();




        Player player = event.getPlayer();
        Material material = event.getClickedBlock().getBlockData().getMaterial();

        if (material == Material.AIR) return;

        for (Prop prop : GAME.PROPS.keySet()) {
            if (prop.getPlayer() == player) {
                prop.setHidingMaterial(block.getType()).resetTimer();
                return;
            }
        }
    }
}
