package com.than00ber.mcmg.core;

import com.google.common.collect.ImmutableList;
import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.core.config.Configurable;
import com.than00ber.mcmg.util.ConfigUtil;
import org.bukkit.ChatColor;
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

    public E register(final String key, final Supplier<E> supplier) {
        if (ENTRIES.containsKey(key)) {
            Console.warn("Registry object with key '" + key + "' already registered.");
            Console.warn("  This will override the currently registered object.");
        }
        String name = ChatColor.stripColor(key
                .replaceAll("[-+.^:,']","")
                .replaceAll(" ", "_")
        ).toLowerCase(); // TODO - review this
        ENTRIES.put(name, supplier);
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
            if (obj.get() instanceof Configurable configurable) {
                String path = registry.name().toLowerCase() + "/" + key;
                YamlConfiguration data = ConfigUtil.load(instance, path);
                configurable.setConfig(data);
            }
        });
    }

    public void unload(final Main instance) {
        Console.debug("Unloading " + registry + " registry.");
        ENTRIES.forEach((key, obj) -> {
            if (obj.get() instanceof Configurable configurable) {
                String path = registry.name().toLowerCase() + "/" + key;
                ConfigUtil.save(instance, path, configurable.getConfig());
            }
        });
    }

    public enum Registries {
        MINIGAMES, ITEMS, TEAMS
    }
}
