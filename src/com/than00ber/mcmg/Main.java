package com.than00ber.mcmg;

import com.than00ber.mcmg.commands.GameCommandExecutor;
import com.than00ber.mcmg.games.GameEngine;
import com.than00ber.mcmg.games.MiniGame;
import com.than00ber.mcmg.games.example.ExampleGame;
import com.than00ber.mcmg.util.ConfigUtil;
import com.than00ber.mcmg.util.config.Configurable;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static final World WORLD = Bukkit.getWorld("world");
    public static final String PLUGIN_ID = "MCMG";
    public static GameEngine<MiniGame> GAME_ENGINE;

    @Override
    public void onEnable() {
        new GameCommandExecutor(this, WORLD);
        GAME_ENGINE = new GameEngine<>(this);

        ExampleGame game = new ExampleGame();
        ConfigUtil.loadConfigs(this, game);
        GAME_ENGINE.mount(game);
    }

    @Override
    public void onDisable() {
        MiniGame game = GAME_ENGINE.getCurrentGame();
        if (game instanceof Configurable<?> configurable) {
            ConfigUtil.saveConfigs(this, configurable);
        }
    }
}
