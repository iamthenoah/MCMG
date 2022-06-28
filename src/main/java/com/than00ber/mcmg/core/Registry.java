package com.than00ber.mcmg.core;

import com.google.common.collect.ImmutableList;
import com.than00ber.mcmg.Console;
import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.core.configuration.Configurable;
import com.than00ber.mcmg.util.ConfigUtil;
import com.than00ber.mcmg.util.TextUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Supplier;

public final class Registry<O extends Configurable> {

    public static final HashMap<String, Registry<?>> REGISTRIES = new HashMap<>();

    private final HashMap<String, Supplier<O>> entries;
    private final Main instance;
    private final String registry;

    private Registry(Main instance, String registry) {
        entries = new HashMap<>();
        this.instance = instance;
        this.registry = registry;
    }

    public String getRegistryName() {
        return registry;
    }

    public O register(final Supplier<O> object) {
        String key = TextUtil.simplify(object.get().getName());
        if (entries.containsKey(key)) {
            Console.warn("Registry object with key '" + key + "' already registered.");
            Console.warn("  This will override the currently registered object.");
        }
        entries.put(key, object);
        return object.get();
    }

    public ImmutableList<String> getRegistryKeys() {
        return ImmutableList.copyOf(entries.keySet());
    }

    public ImmutableList<Supplier<O>> getRegistryObjects() {
        return ImmutableList.copyOf(entries.values());
    }

    @Nullable
    public O get(final String key) {
        O object = entries.get(key).get();
        if (object == null) return null;
        String path = getRegistryLocation(object);
        YamlConfiguration data = ConfigUtil.load(instance, path);
        object.setConfig(data);
        return object;
    }

    public void load(final Main instance) {
        Console.debug("Loading registry: '" + registry + "'...");
        getRegistryObjects().forEach(obj -> {
            Console.debug("| " + obj.get().getName());
            String path = getRegistryLocation(obj.get());
            YamlConfiguration data = ConfigUtil.load(instance, path);
            obj.get().setConfig(data);
        });
        Console.debug("DONE.");
    }

    public void unload(final Main instance) {
        Console.debug("Unloading registry: '" + registry + "'...");
        getRegistryObjects().forEach(obj -> {
            Console.debug("| " + obj.get().getName());
            String path = getRegistryLocation(obj.get());
            ConfigUtil.save(instance, path, obj.get().getConfig());
        });
        Console.debug("DONE.");
    }

    public void reload(final String key) {
        O object = get(key);
        if (object == null) return;
        String path = getRegistryLocation(object);
        YamlConfiguration config = ConfigUtil.load(Main.INSTANCE, path);
        object.setConfig(config);
        entries.put(key, () -> object);
    }

    public String getRegistryLocation(final Configurable object) {
        return getRegistryName() + "/" + TextUtil.simplify(object.getName());
    }

    public static <O extends Configurable> Registry<O> create(final String type) {
        Registry<O> registry = new Registry<>(Main.INSTANCE, type);
        REGISTRIES.put(registry.getRegistryName(), registry);
        return registry;
    }
}
