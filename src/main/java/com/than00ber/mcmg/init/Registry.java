package com.than00ber.mcmg.init;

import com.google.common.collect.ImmutableList;
import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.util.Console;
import com.than00ber.mcmg.util.config.ConfigProperty;
import com.than00ber.mcmg.util.config.ConfigUtil;
import com.than00ber.mcmg.util.config.Configurable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public final class Registry<E> implements Configurable {

    private final Registries registry;
    private final HashMap<String, Supplier<E>> ENTRIES = new HashMap<>();

    public Registry(Registries registry) {
        this.registry = registry;
    }

    public E register(String key, Supplier<E> supplier) {
        if (ENTRIES.containsKey(key)) {
            Console.warn("Registry object with key '" + key + "' already registered. Skipping...");
            return null;
        } else {
            ENTRIES.put(key, supplier);
            return supplier.get();
        }
    }

    @Nullable
    public E get(String key) {
        return ENTRIES.getOrDefault(key, null).get();
    }

    public ImmutableList<String> getRegistryKeys() {
        return ImmutableList.copyOf(ENTRIES.keySet());
    }

    public void load(Main instance) {
        Console.debug("Loading '" + registry + "' registry...");
        ConfigUtil.loadConfigs(instance, this);
        Console.debug("Done.");
    }

    public void unload(Main instance) {
        Console.debug("Unloading '" + registry + "' registry...");
        ConfigUtil.saveConfigs(instance, this);
        Console.debug("Done.");
    }

    @Override
    public String getConfigName() {
        return registry.name().toLowerCase() + "-registry";
    }

    @Override
    public List<? extends ConfigProperty<?>> getProperties() {
        List<ConfigProperty<?>> configs = new ArrayList<>();
        for (Supplier<?> obj : ENTRIES.values()) {
            if (obj.get() instanceof Configurable configurable) {
                configs.addAll(configurable.getProperties());
            }
        }
        return configs;
    }

    public enum Registries {
        MINIGAMES, ITEMS, TEAMS
    }
}
