package com.than00ber.mcmg.core;

import com.google.common.collect.ImmutableList;
import com.than00ber.mcmg.Console;
import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.core.config.Configurable;
import com.than00ber.mcmg.util.ConfigUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Supplier;

public final class Registry<E extends Registry.Object> {

    private final Registries registry;
    private final HashMap<String, Supplier<E>> ENTRIES = new HashMap<>();

    public Registry(Registries registry) {
        this.registry = registry;
    }

    public E register(final Supplier<E> supplier) {
        String key = supplier.get().getName();
        if (ENTRIES.containsKey(key)) {
            Console.warn("Registry object with key '" + key + "' already registered.");
            Console.warn("  This will override the currently registered object.");
        }
        ENTRIES.put(key, supplier);
        return supplier.get();
    }

    @Nullable
    public E get(final String key) {
        return ENTRIES.getOrDefault(key, null).get();
    }

    public ImmutableList<String> getRegistryKeys() {
        return ImmutableList.copyOf(ENTRIES.keySet());
    }

    public void load(final Main instance) {
        Console.debug("Loading " + registry + " registry.");
        ENTRIES.forEach((key, obj) -> {
            String path = getRegistryLocation(key);
            YamlConfiguration data = ConfigUtil.load(instance, path);
            obj.get().setConfig(data);
        });
    }

    public void unload(final Main instance) {
        Console.debug("Unloading " + registry + " registry.");
        ENTRIES.forEach((key, obj) -> {
            String path = getRegistryLocation(key);
            ConfigUtil.save(instance, path, obj.get().getConfig());
        });
    }

    private String getRegistryLocation(String key) {
        return registry.name().toLowerCase() + "/" + key;
    }

    public enum Registries {
        MINIGAMES, ITEMS, TEAMS
    }

    public interface Object extends Configurable {
        /**
         * Get Registry object name to be used as config key.
         * @return Registry name.
         */
        String getName();
    }
}
