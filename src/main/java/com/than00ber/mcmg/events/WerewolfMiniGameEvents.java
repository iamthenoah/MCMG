package com.than00ber.mcmg.events;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.init.MiniGameTeams;
import com.than00ber.mcmg.minigames.WerewolfMiniGame;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class WerewolfMiniGameEvents extends MiniGameEvents<WerewolfMiniGame> {

    public WerewolfMiniGameEvents(Main instance, WerewolfMiniGame game) {
        super(instance, game);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        minigame.switchTeam(event.getEntity(), MiniGameTeams.SPECTATORS);

        if (WerewolfMiniGame.DEATH_SKULL.get()) {
            Location location = player.getLocation();
            BlockData blockData = Material.SKELETON_SKULL.createBlockData();
            minigame.getWorld().setBlockData(location, blockData);
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();

        if (entity.getNearbyEntities(30, 30, 30).size() > WerewolfMiniGame.ZOMBIE_COUNT.get()) {
            event.setCancelled(true);
            return;
        }

        if (entity instanceof Monster && !(entity instanceof ZombieVillager)) {
            event.setCancelled(true);

            Location location = event.getLocation();
            EntityType type = EntityType.ZOMBIE_VILLAGER;
            ZombieVillager zombieVillager = (ZombieVillager) minigame.getWorld().spawnEntity(location, type);

            zombieVillager.setAdult();
            zombieVillager.setHealth(10.0F);

            Random random = new Random();
            // give 10% change for zombie to hold tool
            if (random.nextFloat() > .90F) {
                zombieVillager.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(4.0F);
                List<Material> tools = List.of(
                        Material.WOODEN_SWORD,
                        Material.WOODEN_SHOVEL,
                        Material.WOODEN_PICKAXE,
                        Material.WOODEN_AXE,
                        Material.WOODEN_HOE
                );
                Material tool = tools.get(random.nextInt(tools.size() - 1));
                zombieVillager.getEquipment().setItemInMainHand(new ItemStack(tool));
            }
            // give 5% chance for zombie to be holding torch
            if (random.nextFloat() > .95F) {
                ItemStack torch = new ItemStack(Material.TORCH);
                zombieVillager.getEquipment().setItemInOffHand(torch);
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        event.setDroppedExp(0);

        if (event.getEntity() instanceof ZombieVillager zombieVillager) {
            Player player = zombieVillager.getKiller();

            if (zombieVillager.getEquipment() != null || new Random().nextBoolean()) {
                if (player != null) player.getInventory().addItem(new ItemStack(Material.EMERALD));
            }
        }
    }

    @EventHandler
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
        if (event.getEntity() instanceof ZombieVillager zombieVillager) {
            LivingEntity target = event.getTarget();
            if (target == null) return;
            Location to = target.getEyeLocation();
            Location from = zombieVillager.getEyeLocation();

            if (getDistance(to, from) > WerewolfMiniGame.AGGRO_DISTANCE.get()) {
                event.setTarget(null);
                event.setCancelled(true);
                return;
            }

            // give 10% chance for zombie to talk
            if (new Random().nextFloat() > .9F) {
                zombieVillager.setCustomName("I've been waiting for you, " + event.getTarget().getName());
                zombieVillager.setCustomNameVisible(true);

                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.INSTANCE, () -> {
                    zombieVillager.setCustomName(null);
                    zombieVillager.setCustomNameVisible(false);
                }, 100);
            }
        }
    }

    private static double getDistance(Location l1, Location l2) {
        double x = Math.pow(l1.getX() - l2.getX(), 2);
        double y = Math.pow(l1.getY() - l2.getY(), 2);
        double z = Math.pow(l1.getZ() - l2.getZ(), 2);
        return Math.abs(Math.sqrt(x + y + z));
    }
}
