package com.than00ber.mcmg.game.minigames.werewolf;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.game.MiniGame;
import com.than00ber.mcmg.game.MiniGameEvent;
import com.than00ber.mcmg.init.GameTeams;
import com.than00ber.mcmg.init.WinConditions;
import com.than00ber.mcmg.objects.GameTeam;
import com.than00ber.mcmg.objects.WinCondition;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class WerewolfGame extends MiniGame {

    public final HashMap<Player, GameTeam> PLAYERS_ALIVE;
    public final HashMap<Player, GameTeam> PLAYERS_DEAD;
    private boolean isDaytime;

    public WerewolfGame(Main instance, World world) {
        super(world);
        setEventListener(new WerewolfGameEventListener(instance, this));
        PLAYERS_ALIVE = new HashMap<>();
        PLAYERS_DEAD = new HashMap<>();
    }

    @Override
    public String getGameName() {
        return "Werewolf";
    }

    @Override
    public List<GameTeam> getGameTeams() {
        return List.of(
                GameTeams.SPECTATORS,
                GameTeams.VILLAGERS,
                GameTeams.WEREWOLVES,
                GameTeams.TRAITORS,
                GameTeams.VAMPIRES,
                GameTeams.POSSESSED
        );
    }

    @Override
    public List<WinCondition> getWinConditions() {
        return List.of(
                WinConditions.VAMPIRE_VICTORY,
                WinConditions.ALL_VILLAGERS_DEAD,
                WinConditions.ALL_WEREWOLVES_DEAD,
                WinConditions.EVERYONE_DEAD
        );
    }

    @Override
    public void onGameStarted() {
        super.onGameStarted();
        PLAYERS_ALIVE.putAll(getParticipants());
        PLAYERS_DEAD.clear();
    }

    private void setDay(BossBar bar) {
        bar.setTitle(ChatColor.YELLOW + "It's midday in the village");
        bar.setColor(BarColor.YELLOW);
        getWorld().setTime(6000);
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
