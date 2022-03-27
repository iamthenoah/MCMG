package com.than00ber.mcmg.minigames;

import com.google.common.collect.ImmutableList;
import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.MiniGameEvent;
import com.than00ber.mcmg.events.PropHuntMiniGameEventListener;
import com.than00ber.mcmg.init.MiniGameTeams;
import com.than00ber.mcmg.init.WinConditions;
import com.than00ber.mcmg.objects.MiniGameTeam;
import com.than00ber.mcmg.objects.WinCondition;
import com.than00ber.mcmg.util.config.MiniGameProperty;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class PropHuntMiniGame extends MiniGame {

    public static final MiniGameProperty.BooleanProperty BLOCKS_ONLY = new MiniGameProperty.BooleanProperty("_blocks.solid", false);
    public static final MiniGameProperty.BooleanProperty NON_BLOCK_ONLY = new MiniGameProperty.BooleanProperty("_blocks.nonsolid", false);
    public static final MiniGameProperty.BooleanProperty PROPS_IN_WATER = new MiniGameProperty.BooleanProperty("blocks.inWater", false);
    public static final MiniGameProperty.IntegerProperty PROPS_MAX_HEALTH = new MiniGameProperty.IntegerProperty("health.props", 4).validate(i -> i <= 40);

    public PropHuntMiniGame(Main instance, World world) {
        super(world);
        setEventListener(new PropHuntMiniGameEventListener(instance, this));
        addProperties(BLOCKS_ONLY, NON_BLOCK_ONLY, PROPS_IN_WATER, PROPS_MAX_HEALTH);
    }

    @Override
    public String getMiniGameName() {
        return "Prophunt";
    }

    @Override
    public ImmutableList<MiniGameTeam> getMiniGameTeams() {
        return ImmutableList.of(
                MiniGameTeams.HUNTERS,
                MiniGameTeams.PROPS
        );
    }

    @Override
    public ImmutableList<WinCondition> getWinConditions() {
        return ImmutableList.of(
                WinConditions.NO_PROPS,
                WinConditions.NO_HUNTERS
        );
    }

    @Override
    public void onMinigameStarted(List<Player> participants) {
        super.onMinigameStarted(participants);
        getWorld().setDifficulty(Difficulty.PEACEFUL);
    }

    @Override
    public void onRoundStarted(MiniGameEvent event) { }

    @Override
    public void onRoundCycled(MiniGameEvent event) {
        event.setWinCondition(WinConditions.PROPS_SURVIVED);
    }
}
