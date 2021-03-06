package com.than00ber.mcmg.minigames;

import com.google.common.collect.ImmutableList;
import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.core.MiniGameEvent;
import com.than00ber.mcmg.core.MiniGameTeam;
import com.than00ber.mcmg.core.WinCondition;
import com.than00ber.mcmg.core.configuration.ConfigurableProperty;
import com.than00ber.mcmg.events.HideNSeekMiniGameEvents;
import com.than00ber.mcmg.registries.Teams;
import com.than00ber.mcmg.registries.WinConditions;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HideNSeekMiniGame extends MiniGame {

    public static final ConfigurableProperty.DoubleProperty DAMAGE_PENALTY = new ConfigurableProperty.DoubleProperty("damage.penalty", .5).verify(ConfigurableProperty.CheckIf.POSITIVE_DOUBLE);
    public static final ConfigurableProperty.EnumProperty<EntityType> ENTITY_TYPE = new ConfigurableProperty.EnumProperty<>("entity.type", EntityType.class, EntityType.VILLAGER);
    public static final ConfigurableProperty.BooleanProperty VIEW_DISGUISE = new ConfigurableProperty.BooleanProperty("view.disguise", true);
    public static final ConfigurableProperty.IntegerProperty HIDER_MAX_HEALTH = new ConfigurableProperty.IntegerProperty("health.hiders", 10).verify(ConfigurableProperty.CheckIf.POSITIVE_INT);

    private final List<Entity> entities;

    public HideNSeekMiniGame(Main instance, World world) {
        super(world);
        setEventListener(new HideNSeekMiniGameEvents(instance, this));
        addProperties(DAMAGE_PENALTY, ENTITY_TYPE, VIEW_DISGUISE, HIDER_MAX_HEALTH);
        entities = new ArrayList<>();
    }

    @Override
    public String getName() {
        return "HideNSeek";
    }

    @Override
    public ImmutableList<MiniGameTeam> getMiniGameTeams() {
        return ImmutableList.of(
                Teams.SEEKERS,
                Teams.HIDERS
        );
    }

    @Override
    public ImmutableList<WinCondition> getWinConditions() {
        return ImmutableList.of(
                WinConditions.NO_HIDERS
        );
    }

    @Override
    public void onMinigameStarted(List<Player> participants) {
        super.onMinigameStarted(participants);
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
