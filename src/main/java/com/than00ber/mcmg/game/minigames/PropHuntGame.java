package com.than00ber.mcmg.game.minigames;

import com.google.common.collect.ImmutableList;
import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.game.MiniGame;
import com.than00ber.mcmg.game.MiniGameEvent;
import com.than00ber.mcmg.game.events.PropHuntGameEventListener;
import com.than00ber.mcmg.init.GameTeams;
import com.than00ber.mcmg.init.WinConditions;
import com.than00ber.mcmg.objects.GameTeam;
import com.than00ber.mcmg.objects.WinCondition;
import com.than00ber.mcmg.util.config.GameProperty;
import org.bukkit.Difficulty;
import org.bukkit.World;

public class PropHuntGame extends MiniGame {

    public static final GameProperty.BooleanProperty BLOCKS_ONLY = new GameProperty.BooleanProperty("_blocks.solid", false);
    public static final GameProperty.BooleanProperty NON_BLOCK_ONLY = new GameProperty.BooleanProperty("_blocks.nonsolid", false);
    public static final GameProperty.BooleanProperty PROPS_IN_WATER = new GameProperty.BooleanProperty("blocks.inWater", false);

    public PropHuntGame(Main instance, World world) {
        super(world);
        setEventListener(new PropHuntGameEventListener(instance, this));
        addProperties(BLOCKS_ONLY, NON_BLOCK_ONLY, PROPS_IN_WATER);
    }

    @Override
    public String getGameName() {
        return "Prophunt";
    }

    @Override
    public ImmutableList<GameTeam> getGameTeams() {
        return ImmutableList.of(
                GameTeams.HUNTERS,
                GameTeams.PROPS
        );
    }

    @Override
    public ImmutableList<WinCondition> getWinConditions() {
        return ImmutableList.of(
                WinConditions.NO_PROPS
        );
    }

    @Override
    public void onGameStarted() {
        super.onGameStarted();
        getWorld().setDifficulty(Difficulty.PEACEFUL);
    }

    @Override
    public void onRoundStarted(MiniGameEvent event) { }

    @Override
    public void onRoundCycled(MiniGameEvent event) {
        event.setWinCondition(WinConditions.PROPS_SURVIVED);
    }
}
