package com.than00ber.mcmg;

import com.than00ber.mcmg.commands.AssignCommandExecutor;
import com.than00ber.mcmg.commands.ConfigsCommandExecutor;
import com.than00ber.mcmg.commands.GameCommandExecutor;
import com.than00ber.mcmg.commands.ReadyCommandExecutor;
import com.than00ber.mcmg.events.GlobalEventListener;
import com.than00ber.mcmg.game.GameEngine;
import com.than00ber.mcmg.game.MiniGame;
import com.than00ber.mcmg.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static final String PLUGIN_ID = "MCMG";
    public static GameEngine<MiniGame> GAME_ENGINE;
    public static Main INSTANCE;
    public static World WORLD;

    // 6033393598488652660 -2000 ~ 1000

    @Override
    public void onEnable() {
        INSTANCE = this;
        WORLD = Bukkit.getWorlds().get(0);
        GAME_ENGINE = new GameEngine<>(this);

        Bukkit.getPluginManager().registerEvents(new GlobalEventListener(), this);

        new GameCommandExecutor(this, WORLD);
        new ConfigsCommandExecutor(this, WORLD);
        new ReadyCommandExecutor(this, WORLD);
        new AssignCommandExecutor(this, WORLD);
    }

    @Override
    public void onDisable() {
        if (GAME_ENGINE != null && GAME_ENGINE.hasGame()) {
            ConfigUtil.saveConfigs(this, GAME_ENGINE.getCurrentGame());

            if (GAME_ENGINE.hasRunningGame()) {
                GAME_ENGINE.endGame("Game ending caused by plugin disabling.");
            }
        }
    }
}
