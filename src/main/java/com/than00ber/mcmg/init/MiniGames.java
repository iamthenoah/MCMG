package com.than00ber.mcmg.init;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.minigames.HideNSeekMiniGame;
import com.than00ber.mcmg.minigames.MiniGame;
import com.than00ber.mcmg.minigames.PropHuntMiniGame;
import com.than00ber.mcmg.minigames.WerewolfMiniGame;

import java.util.HashMap;
import java.util.function.Supplier;

public class MiniGames {

    public static final HashMap<String, Supplier<? extends MiniGame>> MINI_GAMES = new HashMap<>();

    static {
        register(() -> new WerewolfMiniGame(Main.INSTANCE, Main.WORLD));
        register(() -> new PropHuntMiniGame(Main.INSTANCE, Main.WORLD));
        register(() -> new HideNSeekMiniGame(Main.INSTANCE, Main.WORLD));
    }

    private static void register(Supplier<? extends MiniGame> minigame) {
        MINI_GAMES.put(minigame.get().getMiniGameName().toLowerCase(), minigame);
    }
}
