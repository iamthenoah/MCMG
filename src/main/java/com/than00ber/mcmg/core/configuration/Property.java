package com.than00ber.mcmg.core.configuration;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings({"unchecked", "unused"})
public class Property<V> implements Supplier<V> {

    private final String name;
    protected final V defaultValue;
    protected V value;

    public Property(String name, V defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public void set(V val) {
        value = val;
    }

    @Override
    public V get() {
        return Optional.ofNullable(value).orElse(defaultValue);
    }

    public void reset() {
        value = defaultValue;
    }

    public void load(ConfigurationSection configs) {
        Object obj = configs.get(getName());
        if (obj != null) set((V) obj);
    }

    public void save(ConfigurationSection configs) {
        configs.set(getName(), get());
    }

    @Override
    public String toString() {
        return getName() + ":" + get();
    }
}
