package com.than00ber.mcmg.core.config;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;
import java.util.function.Supplier;

public class ConfigProperty<V> implements Supplier<V> {

    private final String path;
    protected final V defaultValue;
    protected V value;

    public ConfigProperty(String path, V defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }

    public String getPath() {
        return path;
    }

    public void set(V val) {
        value = val;
    }

    public void reset() {
        value = defaultValue;
    }

    @Override
    public V get() {
        return Optional.ofNullable(value).orElse(defaultValue);
    }

    public void load(ConfigurationSection configs) {
        Object obj = configs.get(getPath());
        if (obj != null) set((V) obj);
    }

    public void save(ConfigurationSection configs) {
        configs.set(path, get());
    }

    @Override
    public String toString() {
        return getPath() + "#" + get();
    }
}
