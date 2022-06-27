package com.than00ber.mcmg.commands;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.core.ActionResult;
import com.than00ber.mcmg.core.Registry;
import com.than00ber.mcmg.core.config.ConfigProperty;
import com.than00ber.mcmg.core.config.Configurable;
import com.than00ber.mcmg.core.config.MiniGameProperty;
import com.than00ber.mcmg.registries.Items;
import com.than00ber.mcmg.registries.MiniGames;
import com.than00ber.mcmg.registries.Teams;
import com.than00ber.mcmg.util.ChatUtil;
import com.than00ber.mcmg.util.ConfigUtil;
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
            Registry.Registries registryName = MiniGameProperty.safeValueOf(Registry.Registries.class, args[0]);
            if (registryName == null) return ActionResult.failure("Registry '" + args[0] + "' does not exist.");

            Registry<?> registry = switch (registryName) {
                case ITEMS -> Items.ITEMS;
                case TEAMS -> Teams.TEAMS;
                case MINIGAMES -> MiniGames.MINIGAMES;
            };

            String key = args[1];
            String propertyName = args[2];
            String fullPropertyName = key + "#" + propertyName;
            Configurable configurable = registry.get(key);
            MiniGameProperty<?> property = (MiniGameProperty<?>) configurable.getProperties().stream()
                    .filter(p -> Objects.equals(p.getPath(), propertyName))
                    .findAny().orElse(null);

            if (property != null) {
                String[] options = Arrays.copyOfRange(args, 3, args.length);
                String arguments = Arrays.toString(options);

                if (Objects.equals(options[0], RESET_KEY)) {
                    property.reset();
                    return ActionResult.success("Property '" + fullPropertyName + "' reset [" + property.get() + "].");
                } else {
                    if (property.isValidValue(player, options)) {
                        property.parseAndSet(player, options);
                        String path = registry.getRegistryLocation(key);
                        ConfigUtil.save(Main.INSTANCE, path, configurable.getConfig());

                        if (Main.MINIGAME_ENGINE.hasRunningGame()) {
                            String warning = "Configuration will only take effect next game.";
                            ChatUtil.toSelf(player, ChatColor.GOLD + warning);
                        }
                        return ActionResult.success("Property '" + fullPropertyName + "' updated " + arguments + ".");
                    }
                    return ActionResult.warn("Invalid arguments given for property '" + fullPropertyName + "' " + arguments + ".");
                }
            }
            return ActionResult.failure("Property '" + fullPropertyName + "' does not exist.");
        }
        return PluginCommand.NOT_A_PLAYER;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String option, String[] args) {
        if (args.length == 0) return Arrays.stream(Registry.Registries.values()).map(Object::toString).collect(Collectors.toList());
        Registry.Registries name = MiniGameProperty.safeValueOf(Registry.Registries.class, option);
        if (name == null) return List.of();

        Registry<?> registry = switch (name) {
            case ITEMS -> Items.ITEMS;
            case TEAMS -> Teams.TEAMS;
            case MINIGAMES -> MiniGames.MINIGAMES;
        };

        if (args.length == 1) {
            return registry.getRegistryKeys();
        } else if (args.length == 2) {
            Configurable configurable = registry.get(args[0]);
            return configurable.getProperties().stream()
                    .map(ConfigProperty::getPath)
                    .collect(Collectors.toList());
        } else {
            return List.of(RESET_KEY);
        }
    }
}