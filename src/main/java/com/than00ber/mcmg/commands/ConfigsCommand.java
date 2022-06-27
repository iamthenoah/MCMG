package com.than00ber.mcmg.commands;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.core.ActionResult;
import com.than00ber.mcmg.core.Registry;
import com.than00ber.mcmg.core.config.Configurable;
import com.than00ber.mcmg.core.config.MiniGameProperty;
import com.than00ber.mcmg.registries.Items;
import com.than00ber.mcmg.registries.MiniGames;
import com.than00ber.mcmg.registries.Teams;
import com.than00ber.mcmg.util.ConfigUtil;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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

            String configurableName = args[1];
            String propertyName = args[2];
            String fullPropertyName = configurableName + "#" + propertyName;
            Configurable configurable = (Configurable) registry.get(configurableName);
            MiniGameProperty<?> property = (MiniGameProperty<?>) configurable.getProperties().stream()
                    .filter(p -> Objects.equals(p.getPath(), propertyName))
                    .findAny().orElse(null);

            if (property != null) {
                String option = args[3];

                if (Objects.equals(option, RESET_KEY)) {
                    Object obj = property.reset();
                    String s = "Property '" + fullPropertyName + "' reset to default value";
                    if (obj != null) s += " [" + obj + "]";
                    return ActionResult.success(s + ".");
                } else {
                    Object obj = property.parse(player, new String[] {option});

                    if (property.isValidValue(obj)) {
                        String s = "Property '" + fullPropertyName + "' updated [" + option + "]";
                        String path = registryName.name().toLowerCase() + "/" + configurableName;
                        ConfigUtil.save(Main.INSTANCE, path, configurable.getConfig());
                        return ActionResult.success(s + ".");
                    } else {
                        String s = "Invalid arguments given for property '" + fullPropertyName + "' [" + option + "].";
                        return ActionResult.warn(s);
                    }
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
            Configurable configurable = (Configurable) registry.get(args[0]);
            List<String> properties = new ArrayList<>();
            configurable.getProperties().forEach(p -> properties.add(p.getPath()));
            return properties;
        } else {
            return List.of("RESET");
        }
    }
}