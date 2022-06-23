package com.than00ber.mcmg.util.config;

import org.bukkit.configuration.file.YamlConfiguration;

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

    public boolean isSet() {
        return value != null;
    }

    public V set(V val) {
        value = val;
        return val;
    }

    public V reset() {
        value = defaultValue;
        return value;
    }

    @Override
    public V get() {
        return Optional.ofNullable(value).orElse(defaultValue);
    }

    public void load(YamlConfiguration configs) {
        Object obj = configs.get(getPath());
        if (obj != null) {
            set((V) obj);
        }
    }

    public void save(YamlConfiguration configs) {
        configs.set(path, get());
    }
}
