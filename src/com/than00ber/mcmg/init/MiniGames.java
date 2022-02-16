package com.than00ber.mcmg.init;

import com.than00ber.mcmg.games.MiniGame;
import com.than00ber.mcmg.games.playables.WerewolfGame;

import java.util.HashMap;

public class MiniGames {

    public static final HashMap<String, MiniGame> MINI_GAMES = new HashMap<>();

    public static final WerewolfGame WEREWOLF_GAME = register(new WerewolfGame());

    private static <G extends MiniGame> G register(G game) {
        MINI_GAMES.put(game.getGameName().toLowerCase(), game);
        return game;
    }
}
