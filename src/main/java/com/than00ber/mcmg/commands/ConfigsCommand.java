package com.than00ber.mcmg.commands;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.core.ActionResult;
import com.than00ber.mcmg.core.Registry;
import com.than00ber.mcmg.core.configuration.Configurable;
import com.than00ber.mcmg.core.configuration.ConfigurableProperty;
import com.than00ber.mcmg.util.ChatUtil;
import com.than00ber.mcmg.util.ConfigUtil;
import com.than00ber.mcmg.util.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConfigsCommand extends PluginCommand {

    private static final String RESET_KEY = "RESET";

    public ConfigsCommand(Main instance, World world) {
        super("configs", instance, world);
    }

    @Override
    public ActionResult execute(@NotNull CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            String registryName = args[0];
            Registry<?> registry = Registry.REGISTRIES.get(registryName);
            if (registry == null) return ActionResult.failure("Registry '" + registryName + "' does not exist.");

            String key = args[1];
            String propertyName = args[2];
            String fullPropertyName = TextUtil.simplify(key) + "#" + propertyName;
            Configurable configurable = registry.get(key);

            if (configurable != null) {
                for (ConfigurableProperty<?> property : configurable.getProperties()) {
                    if (property.getName().equals(propertyName)) {
                        String[] options = Arrays.copyOfRange(args, 3, args.length);
                        String arguments = Arrays.toString(options);

                        if (Objects.equals(options[0], RESET_KEY)) {
                            property.reset();
                            return ActionResult.success("Property '" + fullPropertyName + "' reset [" + property.get() + "].");
                        } else {
                            if (property.setIfValid(player, options)) {
                                ConfigUtil.save(Main.INSTANCE, registry.getRegistryLocation(configurable), configurable.getConfig());
                                registry.reload(key);

                                if (Main.MINIGAME_ENGINE.hasRunningGame()) {
                                    ChatUtil.toSelf(player, ChatColor.GOLD + "Configuration will only take effect next game.");
                                }
                                return ActionResult.success("Property '" + fullPropertyName + "' updated " + arguments + ".");
                            }
                            return ActionResult.warn("Invalid arguments given for property '" + fullPropertyName + "' " + arguments + ".");
                        }
                    }
                }
                return ActionResult.failure("Property '" + fullPropertyName + "' does not exist.");
            }
            return ActionResult.failure("Configurable '" + key + "' not found in '" + registryName + "'.");
        }
        return PluginCommand.NOT_A_PLAYER;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String option, String[] args) {
        if (args.length == 0) return Registry.REGISTRIES.keySet().stream().toList();
        Registry<?> registry = Registry.REGISTRIES.get(option);
        if (args.length == 1) return registry.getRegistryKeys();
        if (args.length == 2) return registry.get(args[0]).getProperties().stream().map(ConfigurableProperty::getName).collect(Collectors.toList());
        return args.length == 3 ? List.of(RESET_KEY) : List.of();
    }
}