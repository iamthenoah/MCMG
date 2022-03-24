package com.than00ber.mcmg.minigames;

import com.google.common.collect.ImmutableList;
import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.MiniGame;
import com.than00ber.mcmg.MiniGameEvent;
import com.than00ber.mcmg.events.HideNSeekMiniGameEventListener;
import com.than00ber.mcmg.init.MiniGameTeams;
import com.than00ber.mcmg.init.WinConditions;
import com.than00ber.mcmg.objects.MiniGameTeam;
import com.than00ber.mcmg.objects.WinCondition;
import com.than00ber.mcmg.util.config.MiniGameProperty;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HideNSeekMiniGame extends MiniGame {

    public static final MiniGameProperty.DoubleProperty DAMAGE_PENALTY = new MiniGameProperty.DoubleProperty("damage.penalty", .5).validate(d -> d <= 40);
    public static final MiniGameProperty.EnumProperty<EntityType> ENTITY_TYPE = new MiniGameProperty.EnumProperty<>("entity.type", EntityType.class, EntityType.VILLAGER);
    public static final MiniGameProperty.BooleanProperty HIDE_DISGUISE = new MiniGameProperty.BooleanProperty("hide.disguise", true);

    private final List<Entity> entities;

    public HideNSeekMiniGame(Main instance, World world) {
        super(world);
        setEventListener(new HideNSeekMiniGameEventListener(instance, this));
        addProperties(DAMAGE_PENALTY, ENTITY_TYPE, HIDE_DISGUISE);
        entities = new ArrayList<>();
    }

    @Override
    public String getGameName() {
        return "HideNSeek";
    }

    @Override
    public ImmutableList<MiniGameTeam> getGameTeams() {
        return ImmutableList.of(
                MiniGameTeams.SEEKERS,
                MiniGameTeams.HIDERS
        );
    }

    @Override
    public ImmutableList<WinCondition> getWinConditions() {
        return ImmutableList.of(
                WinConditions.NO_HIDERS
        );
    }

    @Override
    public void onMinigameStarted() {
        super.onMinigameStarted();
        disablePlayerCollisions();
        getWorld().setDifficulty(Difficulty.PEACEFUL);
        spawnRandomEntities();
    }

    @Override
    public void onMinigameEnded() {
        super.onMinigameEnded();
        enablePlayerCollisions();
        if (ENTITY_TYPE.get().equals(EntityType.VILLAGER)) {
            /* START - ugly code */
            /**/ Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/kill @e[type=minecraft:villager]");
            /* END - ugly code */
        } else {
            entities.forEach(Entity::remove);
        }
    }

    @Override
    public void onRoundStarted(MiniGameEvent event) { }

    @Override
    public void onRoundCycled(MiniGameEvent event) {
        event.setWinCondition(WinConditions.HIDERS_SURVIVED);
    }

    private void spawnRandomEntities() {
        int count = Math.min(100, (int) Math.round(Math.pow((float) PLAYGROUND_RADIUS.get() / 2, 2)));

        for (int i = 0; i < count; i++) {
            Location loc = getRandomLocation();

            if (ENTITY_TYPE.get().equals(EntityType.VILLAGER)) {
                /* START - ugly code */
                /**/ int professionIndex = new Random().nextInt(4);
                /**/ String l = loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();
                /**/ String cmd = "/summon minecraft:villager " + l + " {Profession:-" + professionIndex + "}";
                System.out.println(cmd);
                /**/ Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                /* END - ugly code */
            } else {
                Entity entity = getWorld().spawnEntity(loc, ENTITY_TYPE.get());
                entities.add(entity);
            }
        }
    }

    private Location getRandomLocation() {
        Location center = PLAYGROUND_SPAWN.get();
        Random random = new Random();
        int r = PLAYGROUND_RADIUS.get() / 2;
        int x = random.nextInt(center.getBlockX() - r, center.getBlockX() + r);
        int z = random.nextInt(center.getBlockZ() - r, center.getBlockZ() + r);
        int y = getWorld().getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING_NO_LEAVES) + 1;
        return new Location(getWorld(), x, y, z);
    }

    private void disablePlayerCollisions() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;

        Scoreboard scoreboard = manager.getMainScoreboard();
        for (Team team : scoreboard.getTeams()) {
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        }
    }

    private void enablePlayerCollisions() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;

        Scoreboard scoreboard = manager.getMainScoreboard();
        for (Team team : scoreboard.getTeams()) {
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.ALWAYS);
        }
    }
}
