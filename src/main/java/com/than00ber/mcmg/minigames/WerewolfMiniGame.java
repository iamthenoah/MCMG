package com.than00ber.mcmg.minigames;

import com.google.common.collect.ImmutableList;
import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.core.MiniGameEvent;
import com.than00ber.mcmg.core.MiniGameTeam;
import com.than00ber.mcmg.core.WinCondition;
import com.than00ber.mcmg.core.config.MiniGameProperty;
import com.than00ber.mcmg.events.WerewolfMiniGameEvents;
import com.than00ber.mcmg.registries.Teams;
import com.than00ber.mcmg.registries.WinConditions;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;

public class WerewolfMiniGame extends MiniGame {

    public static final MiniGameProperty.IntegerProperty AGGRO_DISTANCE = new MiniGameProperty.IntegerProperty("aggro.distance", 10).validate(d -> d > 0);
    public static final MiniGameProperty.IntegerProperty ZOMBIE_COUNT = new MiniGameProperty.IntegerProperty("zombie.count", 5).validate(d -> d > 0);
    public static final MiniGameProperty.BooleanProperty DEATH_SKULL = new MiniGameProperty.BooleanProperty("death.skull", true);

    private boolean isDaytime;

    public WerewolfMiniGame(Main instance, World world) {
        super(world);
        setEventListener(new WerewolfMiniGameEvents(instance, this));
        addProperties(AGGRO_DISTANCE, ZOMBIE_COUNT, DEATH_SKULL);
    }

    @Override
    public String getMiniGameName() {
        return "Werewolf";
    }

    @Override
    public ImmutableList<MiniGameTeam> getMiniGameTeams() {
        return ImmutableList.of(
                Teams.VILLAGERS,
                Teams.WEREWOLVES,
                Teams.TRAITORS,
                Teams.VAMPIRES,
                Teams.POSSESSED
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
        isDaytime = false;
        setNight(event.getBossBar());
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
