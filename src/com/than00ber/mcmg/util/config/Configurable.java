package com.than00ber.mcmg.util.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public interface Configurable<T> {
    /**
     * Get default file name to be used to save the configs on.
     * @return Default file name.
     */
    String getConfigName();

    /**
     * Retrieves all configurable properties of the object.
     * @return All object properties.
     */
    List<? extends ConfigProperty<?>> getProperties();

    /**
     * Retrieves the configured object.
     * @return Configured data.
     * @default Returns a configuration of all properties.
     */
    default YamlConfiguration getConfig() {
        YamlConfiguration configs = new YamlConfiguration();
        for (ConfigProperty<?> property : this.getProperties()) {
            property.save(configs);
        }
        return configs;
    }

    /**
     * Sets the configuration.
     * @param configs Configuration object.
     * @return Instance.
     * @default Configures of properties.
     */
    default T setConfig(YamlConfiguration configs) {
        for (ConfigProperty<?> property : this.getProperties()) {
            property.load(configs);
        }
        return (T) this;
    }
}
