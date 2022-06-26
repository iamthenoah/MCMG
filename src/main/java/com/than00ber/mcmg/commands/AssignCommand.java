package com.than00ber.mcmg.commands;

import com.google.common.collect.ImmutableList;
import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.MiniGameTeam;
import com.than00ber.mcmg.minigames.MiniGame;
import com.than00ber.mcmg.util.ActionResult;
import com.than00ber.mcmg.util.ChatUtil;
import com.than00ber.mcmg.util.TextUtil;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class AssignCommand extends PluginCommand {

    public AssignCommand(Main instance, World world) {
        super("assign", instance, world);
    }

    @Override
    protected ActionResult execute(@NotNull CommandSender sender, @Nullable String[] args) {
        if (Main.MINIGAME_ENGINE.hasRunningGame()) {
            MiniGame game = Main.MINIGAME_ENGINE.getCurrentGame();
            String playerName = args[0];

            Player player = game.getWorld().getPlayers().stream().filter(p -> p.getDisplayName().equals(playerName))
                    .findFirst().orElse(null);

            if (player != null) {
                String teamName = args[1];
                MiniGameTeam found = game.getMiniGameTeams().stream()
                        .filter(t -> Objects.equals(t.getVisibleName(), teamName))
                        .findFirst()
                        .orElse(null);

                if (found != null) {
                    game.addPlayer(player, found);
                    ChatUtil.toAll(TextUtil.formatPlayer(player) + " is now in the " + TextUtil.formatGameTeam(found) + " team.");
                    ChatUtil.toSelf(player, TextUtil.formatObjective(found));
                    return ActionResult.success();
                }
                return ActionResult.failure("Team '" + teamName + "' does not exist in game " + TextUtil.formatMiniGame(game));
            }
            return ActionResult.failure("Could not find player " + playerName);
        }
        return ActionResult.warn("No game currently running.");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String option, String[] args) {
        if (Main.MINIGAME_ENGINE.hasRunningGame() && args.length == 1) {
            ImmutableList<MiniGameTeam> teams = Main.MINIGAME_ENGINE.getCurrentGame().getMiniGameTeams();
            return teams.stream().map(MiniGameTeam::getVisibleName).toList();
        }
        return null;
    }
}
