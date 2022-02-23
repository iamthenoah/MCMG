package com.than00ber.mcmg.game.minigames;

import com.google.common.collect.ImmutableList;
import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.game.MiniGame;
import com.than00ber.mcmg.game.MiniGameEvent;
import com.than00ber.mcmg.game.events.HideNSeekGameEventListener;
import com.than00ber.mcmg.init.GameTeams;
import com.than00ber.mcmg.init.WinConditions;
import com.than00ber.mcmg.objects.GameTeam;
import com.than00ber.mcmg.objects.WinCondition;
import com.than00ber.mcmg.util.config.GameProperty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HideNSeekGame extends MiniGame {

    private final GameProperty.DoubleProperty damagePenalty = new GameProperty.DoubleProperty("damage.penalty", .5).validate(d -> d <= 40);

    private final List<Villager> villagers;

    public HideNSeekGame(Main instance, World world) {
        super(world);
        setEventListener(new HideNSeekGameEventListener(instance, this));
        addProperties(damagePenalty);
        villagers = new ArrayList<>();
    }

    public double getDamagePenalty() {
        return damagePenalty.get();
    }

    @Override
    public String getGameName() {
        return "HideNSeek";
    }

    @Override
    public ImmutableList<GameTeam> getGameTeams() {
        return ImmutableList.of(
                GameTeams.HIDERS,
                GameTeams.SEEKERS
        );
    }

    @Override
    public ImmutableList<WinCondition> getWinConditions() {
        return ImmutableList.of(
                WinConditions.NO_HIDERS
        );
    }

    @Override
    public void onGameStarted() {
        super.onGameStarted();
        spawnRandomVillagers();
        players.forEach((p, r) -> {
            if (r.equals(GameTeams.SEEKERS)) {
                int duration = durationGrace.get() * 20;
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration, 100));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, 10));
                p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, duration, 250));
            }
        });
    }

    @Override
    public void onGameEnded() {
        super.onGameEnded();
        villagers.forEach(Entity::remove);
    }

    @Override
    public void onRoundStarted(MiniGameEvent event) { }

    @Override
    public void onRoundCycled(MiniGameEvent event) {
        event.setWinCondition(WinConditions.HIDERS_SURVIVED);
    }

    private void spawnRandomVillagers() {
        int count = Math.min(100, (int) Math.round(Math.pow((float) playgroundRadius.get() / 2, 2)));
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            Location location = getRandomLocation();
            Villager villager = (Villager) getWorld().spawnEntity(location, EntityType.VILLAGER);

            int c = random.nextInt(Villager.Profession.values().length);
            Villager.Profession profession = Villager.Profession.values()[c];
            villager.setProfession(profession);

            villagers.add(villager);
        }
    }

    private Location getRandomLocation() {
        Location center = playgroundSpawn.get();
        Random random = new Random();
        int r = playgroundRadius.get() / 2;
        int x = random.nextInt(center.getBlockX() - r, center.getBlockX() + r);
        int z = random.nextInt(center.getBlockZ() - r, center.getBlockZ() + r);
        int y = getWorld().getHighestBlockYAt(x, z) + 1;
        return new Location(getWorld(), x, y, z);
    }
}
