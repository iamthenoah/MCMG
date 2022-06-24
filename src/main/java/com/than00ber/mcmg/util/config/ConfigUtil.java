package com.than00ber.mcmg.util.config;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.util.Console;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public final class ConfigUtil {

    public static YamlConfiguration load(Main instance, String name) {
        name += ".yml";
        YamlConfiguration configs = new YamlConfiguration();
        try {
            configs.load(new File(instance.getDataFolder(), name));
        } catch (IOException | InvalidConfigurationException e) {
            Console.warn("Config File '" + name + "' not found. Generating new one.");

            try {
                File file = new File(instance.getDataFolder(), name);
                if (!file.exists() && file.getParentFile().mkdirs() && file.createNewFile()) {
                    instance.saveResource(name, true);
                }
            } catch (IOException exception) {
                Console.error("Could not create '" + name + "' file. ");
                Console.error(exception);
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
            Console.error("Error saving " + name + " file: " + e.getMessage());
        }
    }

    public static void loadConfigs(Main instance, Configurable configurable) {
        YamlConfiguration data = load(instance, configurable.getConfigName());
        configurable.setConfig(data);
    }

    public static void saveConfigs(Main instance, Configurable configurable) {
        save(instance, configurable.getConfigName(), configurable.getConfig());
    }
}
