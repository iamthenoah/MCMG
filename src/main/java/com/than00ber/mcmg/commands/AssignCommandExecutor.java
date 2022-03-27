package com.than00ber.mcmg.commands;

import com.google.common.collect.ImmutableList;
import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.minigames.MiniGame;
import com.than00ber.mcmg.objects.MiniGameTeam;
import com.than00ber.mcmg.util.ActionResult;
import com.than00ber.mcmg.util.ChatUtil;
import com.than00ber.mcmg.util.TextUtil;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Text;

import java.util.List;
import java.util.Objects;

public class AssignCommandExecutor extends PluginCommandExecutor {

    public AssignCommandExecutor(Main instance, World world) {
        super("assign", instance, world);
    }

    @Override
    protected ActionResult execute(@NotNull CommandSender sender, @Nullable String[] args) {
        if (Main.MINIGAME_ENGINE.hasGame()) {
            MiniGame game = Main.MINIGAME_ENGINE.getCurrentGame();
            String playerName = args[0];

            Player player = game.getWorld().getPlayers().stream().filter(p -> p.getDisplayName().equals(playerName))
                    .findFirst().orElse(null);

            if (player != null) {
                String teamName = args[1];
                MiniGameTeam found = game.getMiniGameTeams().stream()
                        .filter(t -> Objects.equals(t.getDisplayName(), teamName))
                        .findFirst()
                        .orElse(null);

                if (found != null) {
                    game.switchTeam(player, found);
                    ChatUtil.toSelf(player, "You have been added to team " + TextUtil.formatGameTeam(found));
                    ChatUtil.toSelf(player, TextUtil.formatObjective(found));
                    return ActionResult.success(playerName + " is now in the " + teamName + " team.");
                }
                return ActionResult.failure("Team '" + teamName + "' does not exist in game " + TextUtil.formatMiniGame(game));
            }
            return ActionResult.failure("Could not find player " + playerName);
        }
        return ActionResult.warn("No minigame currently selected");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String option, String[] args) {
        if (Main.MINIGAME_ENGINE.hasGame() && args.length == 1) {
            ImmutableList<MiniGameTeam> teams = Main.MINIGAME_ENGINE.getCurrentGame().getMiniGameTeams();
            return teams.stream().map(MiniGameTeam::getDisplayName).toList();
        }
        return null;
    }
}
