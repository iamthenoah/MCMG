package com.than00ber.mcmg.commands;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.minigames.MiniGame;
import com.than00ber.mcmg.util.ActionResult;
import com.than00ber.mcmg.util.Console;
import com.than00ber.mcmg.util.TextUtil;
import com.than00ber.mcmg.util.config.ConfigProperty;
import com.than00ber.mcmg.util.config.ConfigUtil;
import com.than00ber.mcmg.util.config.MiniGameProperty;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ConfigsCommandExecutor extends PluginCommandExecutor {

    public ConfigsCommandExecutor(Main instance, World world) {
        super("configs", instance, world);
    }

    @Override
    public ActionResult execute(@NotNull CommandSender sender, String[] args) {
        if (!Main.MINIGAME_ENGINE.hasIdleGame()) {
            return ActionResult.warn("Cannot update config at the moment.");
        }

        if (sender instanceof Player player) {
            MiniGame game = Main.MINIGAME_ENGINE.getCurrentGame();
            String propertyName = game.getMiniGameName() + "#" + args[0];

            MiniGameProperty<?> property = (MiniGameProperty<?>) game.getProperties().stream()
                    .filter(p -> Objects.equals(p.getPath(), args[0]))
                    .findAny().orElse(null);

            if (property != null) {
                String[] options = Arrays.copyOfRange(args, 1, args.length);
                String arguments = Arrays.toString(options);

                try {
                    if (options.length > 0 && Objects.equals(options[0], "reset")) {
                        Object obj = property.reset();
                        String s = "Property '" + propertyName + "' reset to default value";
                        if (obj != null) s += " [" + obj + "]";

                        return ActionResult.success(s + ".");
                    } else {
                        Object obj = property.parse(player, options);

                        if (property.isValidValue(obj)) {
                            String s = "Property '" + propertyName + "' updated";
                            if (options.length > 0) s += " " + arguments;

                            ConfigUtil.saveConfigs(instance, game); // persist data on change

                            return ActionResult.success(s + ".");
                        } else {
                            String s = "Invalid arguments given for property '" + propertyName + "' " + arguments;
                            return ActionResult.warn(s);
                        }
                    }
                } catch (Exception e) {
                    Console.error(
                            "Failed to update '" + propertyName + "'.",
                            "- Error:     " + e.getMessage(),
                            "- Property:  " + propertyName,
                            "- Arguments: " + arguments
                    );
                    return PluginCommandExecutor.INVALID_COMMAND;
                }
            }
            return ActionResult.failure("Property '" + propertyName + "' does not exist.");
        }
        return PluginCommandExecutor.INVALID_COMMAND;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String option, String[] args) {
        if (args.length == 0 && Main.MINIGAME_ENGINE.hasIdleGame()) {
            List<? extends ConfigProperty<?>> properties = Main.MINIGAME_ENGINE.getCurrentGame().getProperties();
            return TextUtil.getMatching(new String[] {option}, properties, ConfigProperty::getPath);
        }
        return List.of();
    }
}