package com.than00ber.mcmg.init;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.minigames.HideNSeekMiniGame;
import com.than00ber.mcmg.minigames.MiniGame;
import com.than00ber.mcmg.minigames.PropHuntMiniGame;
import com.than00ber.mcmg.minigames.WerewolfMiniGame;
import com.than00ber.mcmg.util.Registry;

public class MiniGames {

    public static final Registry<MiniGame> MINIGAMES = new Registry<>();

    static {
        MINIGAMES.register("prophunt", () -> new PropHuntMiniGame(Main.INSTANCE, Main.WORLD));
        MINIGAMES.register("werewolf", () -> new WerewolfMiniGame(Main.INSTANCE, Main.WORLD));
        MINIGAMES.register("hidenseek", () -> new HideNSeekMiniGame(Main.INSTANCE, Main.WORLD));
    }
}
