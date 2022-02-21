package com.than00ber.mcmg;

import com.than00ber.mcmg.commands.ConfigsCommandExecutor;
import com.than00ber.mcmg.commands.GameCommandExecutor;
import com.than00ber.mcmg.commands.ReadyCommandExecutor;
import com.than00ber.mcmg.game.GameEngine;
import com.than00ber.mcmg.game.MiniGame;
import com.than00ber.mcmg.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static final String PLUGIN_ID = "MCMG";
    public static final World WORLD = Bukkit.getWorld("world");
    public static GameEngine<MiniGame> GAME_ENGINE;
    public static Main INSTANCE;

    // username: Gaojinglu80!

    @Override
    public void onEnable() {
        INSTANCE = this;
        GAME_ENGINE = new GameEngine<>(this);

        new GameCommandExecutor(this, WORLD);
        new ConfigsCommandExecutor(this, WORLD);
        new ReadyCommandExecutor(this, WORLD);
    }

    @Override
    public void onDisable() {
        if (GAME_ENGINE != null && GAME_ENGINE.hasGame()) {
            ConfigUtil.saveConfigs(this, GAME_ENGINE.getCurrentGame());
        }
    }
}
