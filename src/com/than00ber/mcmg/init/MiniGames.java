package com.than00ber.mcmg.init;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.game.MiniGame;
import com.than00ber.mcmg.game.minigames.WerewolfGame;

import java.util.HashMap;
import java.util.function.Supplier;

public class MiniGames {

    public static final HashMap<String, Supplier<? extends MiniGame>> MINI_GAMES = new HashMap<>();

    static {
        register(() -> new WerewolfGame(Main.WORLD));
    }

    private static void register(Supplier<? extends MiniGame> game) {
        MINI_GAMES.put(game.get().getGameName().toLowerCase(), game);
    }
}
