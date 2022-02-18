package com.than00ber.mcmg;

import com.than00ber.mcmg.commands.ConfigsCommandExecutor;
import com.than00ber.mcmg.commands.GameCommandExecutor;
import com.than00ber.mcmg.commands.ReadyCommandExecutor;
import com.than00ber.mcmg.game.GameEngine;
import com.than00ber.mcmg.game.MiniGame;
import com.than00ber.mcmg.util.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.command.Command;
import org.bukkit.plugin.java.annotation.permission.Permission;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

@Plugin(name = Main.PLUGIN_ID, version = "1.0.0")
@Author(name = "Than00ber")
@Command(
        name = "game",
        desc = "Allows to start, end and restart games",
        permission = "admin",
        usage = "/game [start | end | restart | play] <Option?>"
)
@Command(
        name = "configs",
        desc = "Allows editing of all current game properties",
        permission = "admin",
        usage = "/configs <Property> <Option?>"
)
@Command(
        name = "ready",
        desc = "Players can vote when they are ready to start a new game",
        permission = "none",
        usage = "/ready"
)
@Permission(
        name = "none",
        desc = "Lowest permission level, gives no access to commands",
        defaultValue = PermissionDefault.TRUE
)
@Permission(
        name = "admin",
        desc = "Highest permission level, gives access to all commands",
        defaultValue = PermissionDefault.OP
)
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
