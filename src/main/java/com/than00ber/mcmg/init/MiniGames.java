package com.than00ber.mcmg.init;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.game.MiniGame;
import com.than00ber.mcmg.game.minigames.HideNSeekGame;
import com.than00ber.mcmg.game.minigames.PropHuntGame;
import com.than00ber.mcmg.game.minigames.WerewolfGame;

import java.util.HashMap;
import java.util.Locale;
import java.util.function.Supplier;

public class MiniGames {

    public static final HashMap<String, Supplier<? extends MiniGame>> MINI_GAMES = new HashMap<>();

    static {
        register(() -> new WerewolfGame(Main.INSTANCE, Main.WORLD));
        register(() -> new PropHuntGame(Main.INSTANCE, Main.WORLD));
        register(() -> new HideNSeekGame(Main.INSTANCE, Main.WORLD));
    }

    private static void register(Supplier<? extends MiniGame> game) {
        MINI_GAMES.put(game.get().getGameName().toLowerCase(Locale.ENGLISH), game);
    }
}
