package com.than00ber.mcmg.game.minigames;

import com.google.common.collect.ImmutableList;
import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.game.MiniGame;
import com.than00ber.mcmg.game.MiniGameEvent;
import com.than00ber.mcmg.game.events.WerewolfGameEventListener;
import com.than00ber.mcmg.init.GameTeams;
import com.than00ber.mcmg.init.WinConditions;
import com.than00ber.mcmg.objects.GameTeam;
import com.than00ber.mcmg.objects.WinCondition;
import com.than00ber.mcmg.util.config.GameProperty;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;

public class WerewolfGame extends MiniGame {

    private final GameProperty.IntegerProperty aggroDistance = new GameProperty.IntegerProperty("aggro.distance", 10).validate(d -> d > 0);
    private final GameProperty.IntegerProperty zombieSpawnCount = new GameProperty.IntegerProperty("spawn.count", 5).validate(d -> d > 0);

    private boolean isDaytime;

    public WerewolfGame(Main instance, World world) {
        super(world);
        setEventListener(new WerewolfGameEventListener(instance, this));
        addProperties(aggroDistance);
    }

    public int getAggroDistance() {
        return aggroDistance.get();
    }

    public int getZombieSpawnCount() {
        return zombieSpawnCount.get() + getParticipants().size();
    }

    @Override
    public String getGameName() {
        return "Werewolf";
    }

    @Override
    public ImmutableList<GameTeam> getGameTeams() {
        return ImmutableList.of(
                GameTeams.VILLAGERS,
                GameTeams.WEREWOLVES,
                GameTeams.TRAITORS,
                GameTeams.VAMPIRES,
                GameTeams.POSSESSED
        );
    }

    @Override
    public ImmutableList<WinCondition> getWinConditions() {
        return ImmutableList.of(
                WinConditions.VAMPIRE_VICTORY,
                WinConditions.ALL_VILLAGERS_DEAD,
                WinConditions.ALL_WEREWOLVES_DEAD,
                WinConditions.EVERYONE_DEAD
        );
    }

    private void setDay(BossBar bar) {
        bar.setTitle(ChatColor.YELLOW + "It's midday in the village");
        bar.setColor(BarColor.YELLOW);
        getWorld().setTime(6000);
        clearMonsters();
    }

    private void setNight(BossBar bar) {
        bar.setTitle(ChatColor.DARK_PURPLE + "It's midnight in the village");
        bar.setColor(BarColor.PURPLE);
        getWorld().setTime(18000);
    }

    @Override
    public void onRoundStarted(MiniGameEvent event) {
        isDaytime = true;
        setDay(event.getBossBar());
    }

    @Override
    public void onRoundCycled(MiniGameEvent event) {
        if (isDaytime) {
            setNight(event.getBossBar());
        } else {
            setDay(event.getBossBar());
        }
        isDaytime = !isDaytime;
    }
}
