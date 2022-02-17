package com.than00ber.mcmg.game.minigames.hidenseek;

import com.than00ber.mcmg.game.MiniGame;
import com.than00ber.mcmg.game.MiniGameEvent;
import com.than00ber.mcmg.init.GameTeams;
import com.than00ber.mcmg.init.WinConditions;
import com.than00ber.mcmg.objects.GameTeam;
import com.than00ber.mcmg.objects.WinCondition;
import com.than00ber.mcmg.util.ChatUtil;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class HideNSeekGame extends MiniGame {

    public final HashMap<Player, GameTeam> HIDERS;
    public final HashMap<Player, GameTeam> SEEKERS;

    public HideNSeekGame(World world) {
        super(world);
        HIDERS = new HashMap<>();
        SEEKERS = new HashMap<>();
    }

    @Override
    public String getGameName() {
        return "HideNSeek";
    }

    @Override
    public List<GameTeam> getGameTeams() {
        return List.of(
                GameTeams.HIDERS,
                GameTeams.SEEKERS
        );
    }

    @Override
    public List<WinCondition> getWinConditions() {
        return List.of(
                WinConditions.NO_HIDERS
        );
    }

    @Override
    public void onGameStarted() {
        super.onGameStarted();

        assignRandomRoles();
        ChatUtil.showRoundStartScreen(getParticipants());
    }

    @Override
    public void onRoundStarted(MiniGameEvent event) {

    }

    @Override
    public void onRoundCycled(MiniGameEvent event) {

    }
}
