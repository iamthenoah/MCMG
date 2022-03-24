package com.than00ber.mcmg;

import com.than00ber.mcmg.commands.AssignCommandExecutor;
import com.than00ber.mcmg.commands.ConfigsCommandExecutor;
import com.than00ber.mcmg.commands.MiniGameCommandExecutor;
import com.than00ber.mcmg.commands.ReadyCommandExecutor;
import com.than00ber.mcmg.events.GlobalEventListener;
import com.than00ber.mcmg.game.MiniGameEngine;
import com.than00ber.mcmg.game.MiniGame;
import com.than00ber.mcmg.util.config.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static final String PLUGIN_ID = "MCMG";
    public static MiniGameEngine<MiniGame> MINIGAME_ENGINE;
    public static Main INSTANCE;
    public static World WORLD;

    // 6033393598488652660 -2000 ~ 1000

    @Override
    public void onEnable() {
        INSTANCE = this;
        WORLD = Bukkit.getWorlds().get(0);
        MINIGAME_ENGINE = new MiniGameEngine<>(this);

        Bukkit.getPluginManager().registerEvents(new GlobalEventListener(), this);

        new MiniGameCommandExecutor(this, WORLD);
        new ConfigsCommandExecutor(this, WORLD);
        new ReadyCommandExecutor(this, WORLD);
        new AssignCommandExecutor(this, WORLD);
    }

    @Override
    public void onDisable() {
        if (MINIGAME_ENGINE != null && MINIGAME_ENGINE.hasGame()) {
            ConfigUtil.saveConfigs(this, MINIGAME_ENGINE.getCurrentGame());

            if (MINIGAME_ENGINE.hasRunningGame()) {
                MINIGAME_ENGINE.endGame("Minigame ending caused by plugin disabling.");
            }
        }
    }
}
