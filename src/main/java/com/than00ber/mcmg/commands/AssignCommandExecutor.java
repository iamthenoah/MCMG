package com.than00ber.mcmg.commands;

import com.google.common.collect.ImmutableList;
import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.game.MiniGame;
import com.than00ber.mcmg.objects.GameTeam;
import com.than00ber.mcmg.util.ActionResult;
import com.than00ber.mcmg.util.Console;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class AssignCommandExecutor extends PluginCommandExecutor {

    public AssignCommandExecutor(Main instance, World world) {
        super("assign", instance, world);
    }

    @Override
    protected ActionResult execute(@NotNull CommandSender sender, @Nullable String[] args) {
        if (Main.GAME_ENGINE.hasGame()) {
            MiniGame game = Main.GAME_ENGINE.getCurrentGame();
            String playerName = args[0];
            Player player = game.getWorld().getPlayers().stream().filter(p -> p.getDisplayName().equals(playerName))
                    .findFirst().orElse(null);

            if (player != null) {
                String teamName = args[1];
                game.getGameTeams().stream().filter(t -> {
                            Console.debug("'" + t.getDisplayName() + "'");
                            Console.debug("'" + teamName + "'");
                            return Objects.equals(t.getDisplayName(), teamName);
                        })
                        .findFirst().ifPresent(team -> game.switchTeam(player, team));

                return ActionResult.success(playerName + " is now in the " + teamName + " team.");
            }
            return ActionResult.failure("Could not find player " + playerName);
        }
        return ActionResult.warn("No game currently selected");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String option, String[] args) {
        if (Main.GAME_ENGINE.hasGame() && args.length == 1) {
            ImmutableList<GameTeam> teams = Main.GAME_ENGINE.getCurrentGame().getGameTeams();
            return teams.stream().map(GameTeam::getDisplayName).toList();
        }
        return null;
    }
}
