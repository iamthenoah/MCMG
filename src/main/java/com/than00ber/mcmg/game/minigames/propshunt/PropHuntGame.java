package com.than00ber.mcmg.game.minigames.propshunt;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.game.MiniGame;
import com.than00ber.mcmg.game.MiniGameEvent;
import com.than00ber.mcmg.init.GameTeams;
import com.than00ber.mcmg.init.WinConditions;
import com.than00ber.mcmg.objects.GameTeam;
import com.than00ber.mcmg.objects.WinCondition;
import com.than00ber.mcmg.util.config.GameProperty;
import org.bukkit.World;

import java.util.List;

public class PropHuntGame extends MiniGame {

    private final GameProperty.BooleanProperty blocksOnly = new GameProperty.BooleanProperty("blocks.solid", false);
    private final GameProperty.BooleanProperty nonBlocksOnly = new GameProperty.BooleanProperty("blocks.nonsolid", false);

    public PropHuntGame(Main instance, World world) {
        super(world);
        setEventListener(new PropHuntGameEventListener(instance, this));
        addProperties(blocksOnly, nonBlocksOnly);
    }

    @Override
    public String getGameName() {
        return "Prophunt";
    }

    @Override
    public List<GameTeam> getGameTeams() {
        return List.of(
                GameTeams.PROPS,
                GameTeams.HUNTERS
        );
    }

    @Override
    public List<WinCondition> getWinConditions() {
        return List.of(
                WinConditions.NO_PROPS
        );
    }

    @Override
    public void onRoundStarted(MiniGameEvent event) { }

    @Override
    public void onRoundCycled(MiniGameEvent event) {
        event.setWinCondition(WinConditions.PROPS_SURVIVED);
    }
}
