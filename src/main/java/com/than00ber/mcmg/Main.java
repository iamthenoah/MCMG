package com.than00ber.mcmg;

import com.than00ber.mcmg.commands.AssignCommand;
import com.than00ber.mcmg.commands.ConfigsCommand;
import com.than00ber.mcmg.commands.MiniGameCommand;
import com.than00ber.mcmg.events.GlobalEvents;
import com.than00ber.mcmg.init.MiniGameItems;
import com.than00ber.mcmg.init.MiniGameTeams;
import com.than00ber.mcmg.init.MiniGames;
import com.than00ber.mcmg.minigames.MiniGame;
import com.than00ber.mcmg.util.config.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static final String PLUGIN_ID = "MCMG";
    public static MiniGameEngine<MiniGame> MINIGAME_ENGINE;
    public static Main INSTANCE;
    public static World WORLD;

    @Override
    public void onEnable() {
        INSTANCE = this;
        WORLD = Bukkit.getWorlds().get(0);
        MINIGAME_ENGINE = new MiniGameEngine<>(this);

        Bukkit.getPluginManager().registerEvents(new GlobalEvents(), this);

        new MiniGameCommand(this, WORLD);
        new ConfigsCommand(this, WORLD);
        new AssignCommand(this, WORLD);

        MiniGameItems.ITEMS.load(this);
        MiniGameTeams.TEAMS.load(this);
        MiniGames.MINIGAMES.load(this);
    }

    @Override
    public void onDisable() {
        if (MINIGAME_ENGINE != null && MINIGAME_ENGINE.hasGame()) {
            ConfigUtil.saveConfigs(this, MINIGAME_ENGINE.getCurrentGame());

            if (MINIGAME_ENGINE.hasRunningGame()) {
                MINIGAME_ENGINE.endMiniGame("Minigame ending caused by plugin disabling.");
            }
        }

        MiniGameItems.ITEMS.unload(this);
        MiniGameTeams.TEAMS.unload(this);
        MiniGames.MINIGAMES.unload(this);
    }
}
