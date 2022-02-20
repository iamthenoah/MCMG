package com.than00ber.mcmg.game.minigames.hidenseek;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.util.ChatUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

public class Hider {

    private final Player player;
    private Location playerLocation;
    private Block hidingBlock;
    private Material material;
    private int hiddenTicks;
    private HiderStatus status;

    public Hider(Player player) {
        this(player, player.getLocation(), null, 0, HiderStatus.UNDECIDED);
    }

    public Hider(Player player, Location playerLocation, Material material, int ticks, HiderStatus status) {
        this.player = player;
        this.playerLocation = playerLocation;
        this.material = material;
        this.hiddenTicks = ticks;
        this.status = status;
        resetPlayer();
    }

    public void resetPlayer() {
        player.setExp(0);
        player.setGameMode(GameMode.ADVENTURE);
    }

    public Hider setHidingSpot(Location playerLocation) {
        this.playerLocation = playerLocation;
        return this;
    }

    public Hider setHidingMaterial(Material material) {
        if (status == HiderStatus.HIDING) {
            ChatUtil.toSelf(player, "You cannot switch block while you're hidden!");
            return this;
        }

        this.status = HiderStatus.REVEALED;
        this.material = material;
        ChatUtil.toSelf(player, "Selected " + material.name() + ".");
        return this;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Location getLocation() {
        return this.playerLocation;
    }

    public Block getHidingBlock() {
        return this.hidingBlock;
    }

    public Hider resetTimer() {
        hiddenTicks = 0;
        player.setExp(0f);
        return this;
    }

    public boolean isHidden() {
        return status == HiderStatus.HIDING;
    }

    public Hider increaseTimer(int hidingTickThreshold) {
        if (hidingTickThreshold == hiddenTicks) this.hide();
        if (hiddenTicks == 0) this.reveal();

        hiddenTicks += 1;

        double showProgressTickThreshold = hidingTickThreshold*0.6;
        if (hiddenTicks >= showProgressTickThreshold && status != HiderStatus.UNDECIDED) {
            float progress = (float) ((hiddenTicks - showProgressTickThreshold)/(hidingTickThreshold - showProgressTickThreshold));
            if (progress <= 1.0) player.setExp(progress);
        }

        return this;
    }

    public void hide() {
        if (status != HiderStatus.REVEALED) return;
        World world = Bukkit.getWorld("World");
        BlockData blockAtLocation = world.getBlockAt(player.getLocation()).getBlockData();
        BlockData blockOnGround = world.getBlockAt(player.getLocation().add(0, -1, 0)).getBlockData();

        if (blockAtLocation.getMaterial() != Material.AIR) {
            ChatUtil.toSelf(player, "You cannot hide here, not enough space!");
            resetTimer();
        } else if (blockOnGround.getMaterial() == Material.AIR || blockOnGround.getMaterial() == Material.WATER) {
            ChatUtil.toSelf(player, "You cannot hide here there's nothing below!");
            resetTimer();
        } else {
            status = HiderStatus.HIDING;
            player.setGameMode(GameMode.SPECTATOR);
            hidingBlock = disguiseFromAll(player, material);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
            Location telLoc = hidingBlock.getLocation().add(0.5, 0, 0.5);
            teleportAsIs(player, telLoc);
            ChatUtil.toSelf(player, "Shh... You are now hiding as " + material.name() + ".");
        }
    }

    public void reveal() {
        if (status != HiderStatus.HIDING) return;
        status = HiderStatus.REVEALED;
        revealToAll(player, hidingBlock.getLocation());
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, -1);
        ChatUtil.toSelf(player, "You are no longer hidden!");
        player.setGameMode(GameMode.ADVENTURE);
    }

    public enum HiderStatus {
        HIDING,
        REVEALED,
        UNDECIDED
    }

    public static void revealToAll(Player hider, Location location) {
        showToOthers(hider.getPlayer());
        location.getBlock().setType(Material.AIR);
    }

    public static Block disguiseFromAll(Player hider, Material material) {
        hideFromOthers(hider.getPlayer());
        Block blockToHide = hider.getLocation().getBlock();
        blockToHide.setType(material);
        return blockToHide;
    }

    public static void showToOthers(Player hider) {
        for (Player player : Main.INSTANCE.getServer().getOnlinePlayers()) {
            player.hidePlayer(Main.INSTANCE, hider);
        }
    }

    public static void hideFromOthers(Player hider) {
        for (Player player : Main.INSTANCE.getServer().getOnlinePlayers()) {
            player.hidePlayer(Main.INSTANCE, hider);
        }
    }

    public static void teleportAsIs(Player player, Location location) {
        location.setPitch(player.getLocation().getPitch());
        location.setYaw(player.getLocation().getYaw());
        player.teleport(location);
    }
}