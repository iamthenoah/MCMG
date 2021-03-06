package com.than00ber.mcmg.util;

import com.than00ber.mcmg.Main;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public final class ConfigUtil {

    public static YamlConfiguration load(Main instance, String name) {
        name += ".yml";
        YamlConfiguration configs = new YamlConfiguration();
        try {
            configs.load(new File(instance.getDataFolder(), name));
        } catch (Exception e) {
            Main.CONSOLE.warn("Config File '" + name + "' not found. Generating new one.");

            try {
                File file = new File(instance.getDataFolder(), name);
                if (!file.exists() && file.getParentFile().mkdirs() && file.createNewFile()) {
                    instance.saveResource(name, true);
                    configs.save(file);
                }
            } catch (Exception exception) {
                Main.CONSOLE.error("Could not create '" + name + "' config file. ");
            }
        }
        return configs;
    }

    public static void save(Main instance, String name, YamlConfiguration configs) {
        name += ".yml";
        try {
            File file = new File(instance.getDataFolder(), name);
            configs.save(file);
        } catch (IOException e) {
            Main.CONSOLE.error("Error saving '" + name + "' config file: " + e.getMessage());
        }
    }
}
