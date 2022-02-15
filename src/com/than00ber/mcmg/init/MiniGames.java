package com.than00ber.mcmg.init;

import com.than00ber.mcmg.games.MiniGame;
import com.than00ber.mcmg.games.example.ExampleGame;

import java.util.HashMap;

public class MiniGames {

    public static final HashMap<String, MiniGame> MINI_GAMES = new HashMap<>();

    public static final ExampleGame EXAMPLE_GAME = register(new ExampleGame());

    private static <G extends MiniGame> G register(G game) {
        MINI_GAMES.put(game.getGameName().toLowerCase(), game);
        return game;
    }
}
