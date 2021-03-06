package com.than00ber.mcmg;

import com.than00ber.mcmg.commands.AssignCommand;
import com.than00ber.mcmg.commands.ConfigsCommand;
import com.than00ber.mcmg.commands.MiniGameCommand;
import com.than00ber.mcmg.core.MiniGameEngine;
import com.than00ber.mcmg.events.GlobalEvents;
import com.than00ber.mcmg.minigames.MiniGame;
import com.than00ber.mcmg.registries.Items;
import com.than00ber.mcmg.registries.MiniGames;
import com.than00ber.mcmg.registries.Teams;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static final String PLUGIN_ID = "MCMG";
    public static MiniGameEngine<MiniGame> MINIGAME_ENGINE;
    public static Main INSTANCE;
    public static World WORLD;
    public static Console CONSOLE;

    @Override
    public void onEnable() {
        INSTANCE = this;
        WORLD = Bukkit.getWorlds().get(0);
        MINIGAME_ENGINE = new MiniGameEngine<>(this);
        CONSOLE = new Console(true);

        Bukkit.getPluginManager().registerEvents(new GlobalEvents(), this);

        new MiniGameCommand(this, WORLD);
        new ConfigsCommand(this, WORLD);
        new AssignCommand(this, WORLD);

        MiniGames.MINIGAMES.load(this);
        Items.ITEMS.load(this);
        Teams.TEAMS.load(this);
    }

    @Override
    public void onDisable() {
        if (MINIGAME_ENGINE != null && MINIGAME_ENGINE.hasGame()) {
            if (MINIGAME_ENGINE.hasRunningGame()) {
                MINIGAME_ENGINE.endMiniGame("Minigame ending caused by plugin disabling.");
            }
        }

        MiniGames.MINIGAMES.unload(this);
        Items.ITEMS.unload(this);
        Teams.TEAMS.unload(this);
    }
}
