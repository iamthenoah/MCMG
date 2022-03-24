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

    public static final GameProperty.IntegerProperty AGGRO_DISTANCE = new GameProperty.IntegerProperty("aggro.distance", 10).validate(d -> d > 0);
    public static final GameProperty.IntegerProperty ZOMBIE_COUNT = new GameProperty.IntegerProperty("zombie.count", 5).validate(d -> d > 0);
    public static final GameProperty.BooleanProperty DEATH_SKULL = new GameProperty.BooleanProperty("death.skull", true);

    private boolean isDaytime;

    public WerewolfGame(Main instance, World world) {
        super(world);
        setEventListener(new WerewolfGameEventListener(instance, this));
        addProperties(AGGRO_DISTANCE, ZOMBIE_COUNT, DEATH_SKULL);
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
