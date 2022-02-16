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

    @Override
    public void onEnable() {
        new GameCommandExecutor(this, WORLD);
        new ConfigsCommandExecutor(this, WORLD);
        new ReadyCommandExecutor(this, WORLD);

        GAME_ENGINE = new GameEngine<>(this);
    }

    @Override
    public void onDisable() {
        if (GAME_ENGINE.hasRunningGame()) {
            ConfigUtil.saveConfigs(this, GAME_ENGINE.getCurrentGame());
        }
    }
}
