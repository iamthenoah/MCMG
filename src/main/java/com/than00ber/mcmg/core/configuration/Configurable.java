package com.than00ber.mcmg.core.configuration;

import com.google.common.collect.ImmutableList;
import org.bukkit.configuration.file.YamlConfiguration;

public interface Configurable {

    String getName();

    default ImmutableList<ConfigurableProperty<?>> getProperties() {
        return ImmutableList.of();
    }

    default YamlConfiguration getConfig() {
        YamlConfiguration configs = new YamlConfiguration();
        for (ConfigurableProperty<?> property : getProperties()) {
            property.save(configs);
        }
        return configs;
    }

    default void setConfig(final YamlConfiguration configs) {
        for (ConfigurableProperty<?> property : getProperties()) {
            property.load(configs);
        }
    }
}
