package com.than00ber.mcmg.games.playables;

import com.than00ber.mcmg.games.GameTeam;
import com.than00ber.mcmg.games.MiniGame;
import com.than00ber.mcmg.games.WinCondition;
import com.than00ber.mcmg.init.GameTeams;
import com.than00ber.mcmg.init.WinConditions;
import com.than00ber.mcmg.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class WerewolfGame extends MiniGame {

    public final HashMap<Player, GameTeam> PLAYERS_ALIVE;
    public final HashMap<Player, GameTeam> PLAYERS_DEAD;

    public WerewolfGame(World world) {
        super(world);
        this.PLAYERS_ALIVE = new HashMap<>();
        this.PLAYERS_DEAD = new HashMap<>();
    }

    @Override
    public String getGameName() {
        return "WerewolfGame";
    }

    @Override
    public HashMap<Player, GameTeam> getCurrentPlayers() {
        HashMap<Player, GameTeam> team = new HashMap<>();
        team.put((Player) Bukkit.getServer().getOnlinePlayers().toArray()[0], GameTeams.SPECTATOR);
        return team;
    }

    @Override
    public List<GameTeam> getGameTeams() {
        return List.of(
                GameTeams.VILLAGER,
                GameTeams.WEREWOLF,
                GameTeams.TRAITOR,
                GameTeams.VAMPIRE,
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
        ChatUtil.toAll("Werewolf#onGameStarted");
        getWorld().getWorldBorder().reset();
        getWorld().setThundering(false);
        getWorld().setStorm(false);
        getWorld().setTime(6000);
        getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        getWorld().setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        getWorld().setGameRule(GameRule.MOB_GRIEFING, false);
        getWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        getWorld().setGameRule(GameRule.DO_ENTITY_DROPS, false);
        getWorld().setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
        getWorld().setGameRule(GameRule.LOG_ADMIN_COMMANDS, false);
        getWorld().setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        getWorld().setGameRule(GameRule.KEEP_INVENTORY, true);
    }

    @Override
    public void onGameEnded() {
        ChatUtil.toAll("Werewolf#onGameEnded");
        getWorld().getWorldBorder().reset();
        getWorld().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        getWorld().setGameRule(GameRule.DO_WEATHER_CYCLE, true);
        getWorld().setGameRule(GameRule.MOB_GRIEFING, true);
        getWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, true);
        getWorld().setGameRule(GameRule.DO_ENTITY_DROPS, true);
        getWorld().setGameRule(GameRule.SHOW_DEATH_MESSAGES, true);
        getWorld().setGameRule(GameRule.LOG_ADMIN_COMMANDS, true);
        getWorld().setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, false);
        getWorld().setGameRule(GameRule.KEEP_INVENTORY, false);
    }

    @Override
    public void onRoundStarted() {
        ChatUtil.toAll("Werewolf#onRoundStarted");
    }

    @Override
    public void onRoundEnded() {
        ChatUtil.toAll("Werewolf#onRoundEnded");
    }

    @Override
    public void onRoundWon(WinCondition<?> condition) {
        ChatUtil.toAll("Werewolf#onRoundWon");
    }
}
