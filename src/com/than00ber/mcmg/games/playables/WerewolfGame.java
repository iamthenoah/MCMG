package com.than00ber.mcmg.games.playables;

import com.than00ber.mcmg.games.GameTeam;
import com.than00ber.mcmg.games.MiniGame;
import com.than00ber.mcmg.games.WinCondition;
import com.than00ber.mcmg.init.GameTeams;
import com.than00ber.mcmg.init.WinConditions;
import com.than00ber.mcmg.util.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class WerewolfGame extends MiniGame {

    public final HashMap<Player, GameTeam> PLAYERS_ALIVE;
    public final HashMap<Player, GameTeam> PLAYERS_DEAD;

    public WerewolfGame() {
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
        ChatUtil.toAll("ExampleGame#onGameStarted");
    }

    @Override
    public void onGameEnded() {
        ChatUtil.toAll("ExampleGame#onGameEnded");
    }

    @Override
    public void onRoundStarted() {
        ChatUtil.toAll("ExampleGame#onRoundStarted");
    }

    @Override
    public void onRoundEnded() {
        ChatUtil.toAll("ExampleGame#onRoundEnded");
    }

    @Override
    public void onRoundWon(WinCondition<?> condition) {
        ChatUtil.toAll("ExampleGame#onRoundWon");
        ChatUtil.toAll(condition.getSubTitleFor(GameTeams.WEREWOLF));
    }
}
