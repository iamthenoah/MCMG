package com.than00ber.mcmg.core.config;

import com.google.common.collect.ImmutableList;
import org.bukkit.configuration.file.YamlConfiguration;

public interface Configurable {
    /**
     * Retrieves all configurable properties of the object.
     * @return All object properties.
     */
    ImmutableList<? extends ConfigProperty<?>> getProperties();

    /**
     * Retrieves the configured object.
     * @return Configured data.
     * @default Returns a configuration of all properties.
     */
    default YamlConfiguration getConfig() {
        YamlConfiguration configs = new YamlConfiguration();
        for (ConfigProperty<?> property : getProperties()) {
            property.save(configs);
        }
        return configs;
    }

    /**
     * Sets the configuration.
     * @param configs Configuration object.
     * @default Configures of properties.
     */
    default void setConfig(YamlConfiguration configs) {
        for (ConfigProperty<?> property : getProperties()) {
            property.load(configs);
        }
    }
}
