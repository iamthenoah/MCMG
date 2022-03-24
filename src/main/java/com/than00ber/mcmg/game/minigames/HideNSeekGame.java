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
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HideNSeekGame extends MiniGame {

    public static final GameProperty.DoubleProperty DAMAGE_PENALTY = new GameProperty.DoubleProperty("damage.penalty", .5).validate(d -> d <= 40);
    public static final GameProperty.EnumProperty<EntityType> ENTITY_TYPE = new GameProperty.EnumProperty<>("entity.type", EntityType.class, EntityType.VILLAGER);
    public static final GameProperty.BooleanProperty HIDE_DISGUISE = new GameProperty.BooleanProperty("hide.disguise", true);

    private final List<Entity> entities;

    public HideNSeekGame(Main instance, World world) {
        super(world);
        setEventListener(new HideNSeekGameEventListener(instance, this));
        addProperties(DAMAGE_PENALTY, ENTITY_TYPE, HIDE_DISGUISE);
        entities = new ArrayList<>();
    }

    @Override
    public String getGameName() {
        return "HideNSeek";
    }

    @Override
    public ImmutableList<GameTeam> getGameTeams() {
        return ImmutableList.of(
                GameTeams.HIDERS
//                GameTeams.SEEKERS
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
        disablePlayerCollisions();
        getWorld().setDifficulty(Difficulty.PEACEFUL);
        spawnRandomEntities();
    }

    @Override
    public void onGameEnded() {
        super.onGameEnded();
        enablePlayerCollisions();
        entities.forEach(Entity::remove);
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
            Location location = getRandomLocation();
            Entity entity = getWorld().spawnEntity(location, ENTITY_TYPE.get());
            entities.add(entity);
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
