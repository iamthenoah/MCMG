package com.than00ber.mcmg.init;

import com.google.common.collect.ImmutableList;
import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.util.Console;
import com.than00ber.mcmg.util.config.ConfigUtil;
import com.than00ber.mcmg.util.config.Configurable;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Supplier;

public final class Registry<E> {

    private final Registries registry;
    private final HashMap<String, Supplier<E>> ENTRIES = new HashMap<>();

    public Registry(Registries registry) {
        this.registry = registry;
    }

    public E register(String key, Supplier<E> supplier) {
        if (ENTRIES.containsKey(key)) {
            Console.warn("Registry object with key '" + key + "' already registered.");
            Console.warn("  This is override the currently registered object with '" + key + "'.");
        }
        ENTRIES.put(key, supplier);
        return supplier.get();
    }

    @Nullable
    public E get(String key) {
        return ENTRIES.getOrDefault(key, null).get();
    }

    public ImmutableList<String> getRegistryKeys() {
        return ImmutableList.copyOf(ENTRIES.keySet());
    }

    public void load(Main instance) {
        Console.debug("Loading " + registry + " registry.");
        ENTRIES.forEach((key, obj) -> {
            if (obj.get() instanceof Configurable configurable) {
                String path = registry.name().toLowerCase() + "/" + configurable.getConfigName();
                YamlConfiguration data = ConfigUtil.load(instance, path);
                configurable.setConfig(data);
            }
        });
    }

    public void unload(Main instance) {
        Console.debug("Unloading " + registry + " registry.");
        ENTRIES.forEach((key, obj) -> {
            if (obj.get() instanceof Configurable configurable) {
                String path = registry.name().toLowerCase() + "/" + configurable.getConfigName();
                ConfigUtil.save(instance, path, configurable.getConfig());
            }
        });
    }

    public enum Registries {
        MINIGAMES, ITEMS, TEAMS
    }

//    public String getRegistryName() {
//        return registry.name().toLowerCase() + "-registry";
//    }
//
//    public void load(Main instance) {
//        Console.debug("Loading '" + registry + "' registry from '" + getRegistryName() + "' file.");
//        YamlConfiguration configs =  ConfigUtil.load(instance, getRegistryName());
//        ENTRIES.forEach((key, obj) -> {
//            if (obj.get() instanceof Configurable configurable) {
//                for (ConfigProperty<?> property : configurable.getProperties()) {
//                    ConfigurationSection section = configs.getConfigurationSection(key);
//                    if (section == null) section = configs.createSection(key);
//                    property.load(section);
//                }
//            }
//        });
//    }
//
//    public void unload(Main instance) {
//        Console.debug("Unloading '" + registry + "' registry to '" + getRegistryName() + "' file.");
//        YamlConfiguration configs = new YamlConfiguration();
//        ENTRIES.forEach((key, obj) -> {
//            if (obj.get() instanceof Configurable configurable) {
//                for (ConfigProperty<?> property : configurable.getProperties()) {
//                    ConfigurationSection section = configs.getConfigurationSection(key);
//                    if (section == null) section = configs.createSection(key);
//                    property.save(section);
//                }
//            }
//        });
//        ConfigUtil.save(instance, getRegistryName(), configs);
//    }
}
