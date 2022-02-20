package com.than00ber.mcmg.game.minigames.hidenseek;

import com.than00ber.mcmg.game.MiniGame;
import com.than00ber.mcmg.game.MiniGameEvent;
import com.than00ber.mcmg.game.minigames.propshunt.Prop;
import com.than00ber.mcmg.init.GameTeams;
import com.than00ber.mcmg.init.WinConditions;
import com.than00ber.mcmg.objects.GameTeam;
import com.than00ber.mcmg.objects.WinCondition;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class HideNSeek extends MiniGame {

    public final HashMap<Prop, GameTeam> HIDERS;
    public final HashMap<Player, GameTeam> SEEKERS;

    public HideNSeek(World world) {
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
                WinConditions.NO_HIDERS,
                WinConditions.HIDERS_SURVIVED
        );
    }

    @Override
    public void onRoundStarted(MiniGameEvent event) {

    }

    @Override
    public void onRoundCycled(MiniGameEvent event) {

    }
}
