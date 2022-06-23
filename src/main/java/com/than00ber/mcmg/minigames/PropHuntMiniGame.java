package com.than00ber.mcmg.minigames;

import com.google.common.collect.ImmutableList;
import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.MiniGameEvent;
import com.than00ber.mcmg.MiniGameTeam;
import com.than00ber.mcmg.WinCondition;
import com.than00ber.mcmg.events.PropHuntMiniGameEvents;
import com.than00ber.mcmg.init.MiniGameTeams;
import com.than00ber.mcmg.init.WinConditions;
import com.than00ber.mcmg.util.config.MiniGameProperty;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class PropHuntMiniGame extends MiniGame {

    public static final MiniGameProperty.BooleanProperty PROPS_IN_WATER = new MiniGameProperty.BooleanProperty("blocks.inWater", false);
    public static final MiniGameProperty.IntegerProperty PROPS_MAX_HEALTH = new MiniGameProperty.IntegerProperty("health.props", 4).validate(i -> i <= 40);
    public static final MiniGameProperty.IntegerProperty ARROW_REPLENISH_COOLDOWN = new MiniGameProperty.IntegerProperty("replenish.seconds", 10).validate(i -> i <= Main.MINIGAME_ENGINE.getCurrentGame().getOptions().getDurationRound());
    public static final MiniGameProperty.BooleanProperty ALLOW_BLOCKS = new MiniGameProperty.BooleanProperty("allow.solids", true);
    public static final MiniGameProperty.BooleanProperty ALLOW_SPECIALS = new MiniGameProperty.BooleanProperty("allow.specials", true);
    // PROP_COMPASS
    public static final MiniGameProperty.IntegerProperty PROP_COMPASS_COOLDOWN_START = new MiniGameProperty.IntegerProperty("propCompass.startingCooldown", 30).validate(i -> i >= Main.MINIGAME_ENGINE.getCurrentGame().getOptions().getDurationGrace());
    public static final MiniGameProperty.IntegerProperty PROP_COMPASS_COOLDOWN = new MiniGameProperty.IntegerProperty("propCompass.cooldown", 10).validate(MiniGameProperty.IntegerProperty.POSITIVE);
    public static final MiniGameProperty.IntegerProperty PROP_COMPASS_DURATION = new MiniGameProperty.IntegerProperty("propCompass.duration", 10).validate(MiniGameProperty.IntegerProperty.POSITIVE);
    // STUN_INK
    public static final MiniGameProperty.IntegerProperty STUN_JUICE_COOLDOWN = new MiniGameProperty.IntegerProperty("stunJuice.cooldown", 30).validate(MiniGameProperty.IntegerProperty.POSITIVE);
    public static final MiniGameProperty.IntegerProperty STUN_JUICE_DURATION = new MiniGameProperty.IntegerProperty("stunJuice.duration", 2).validate(MiniGameProperty.IntegerProperty.POSITIVE);
    public static final MiniGameProperty.IntegerProperty STUN_JUICE_RANGE = new MiniGameProperty.IntegerProperty("stunJuice.range", 5).validate(MiniGameProperty.IntegerProperty.POSITIVE);
    // GLOW_DUST
    public static final MiniGameProperty.IntegerProperty GLOW_DUST_COOLDOWN = new MiniGameProperty.IntegerProperty("glowDust.cooldown", 30).validate(MiniGameProperty.IntegerProperty.POSITIVE);
    public static final MiniGameProperty.IntegerProperty GLOW_DUST_DURATION = new MiniGameProperty.IntegerProperty("glowDust.duration", 2).validate(MiniGameProperty.IntegerProperty.POSITIVE);
    public static final MiniGameProperty.IntegerProperty GLOW_DUST_RANGE = new MiniGameProperty.IntegerProperty("glowDust.range", 30).validate(MiniGameProperty.IntegerProperty.POSITIVE);
    // TELEPORTER
    public static final MiniGameProperty.IntegerProperty TELEPORTER_COOLDOWN = new MiniGameProperty.IntegerProperty("teleporter.cooldown", 30).validate(MiniGameProperty.IntegerProperty.POSITIVE);
    public static final MiniGameProperty.IntegerProperty TELEPORTER_RANGE = new MiniGameProperty.IntegerProperty("teleporter.range", 100).validate(MiniGameProperty.IntegerProperty.POSITIVE);
    // COCAINE
    public static final MiniGameProperty.IntegerProperty COCAINE_COOLDOWN = new MiniGameProperty.IntegerProperty("cocaine.cooldown", 30).validate(MiniGameProperty.IntegerProperty.POSITIVE);
    public static final MiniGameProperty.IntegerProperty COCAINE_DURATION = new MiniGameProperty.IntegerProperty("cocaine.duration", 2).validate(MiniGameProperty.IntegerProperty.POSITIVE);
    // PROP_RANDOMIZER
    public static final MiniGameProperty.IntegerProperty PROP_RANDOMIZER_COOLDOWN = new MiniGameProperty.IntegerProperty("propRandomizer.cooldown", 30).validate(MiniGameProperty.IntegerProperty.POSITIVE);

    public PropHuntMiniGame(Main instance, World world) {
        super(world);
        setEventListener(new PropHuntMiniGameEvents(instance, this));
        addProperties(
                PROPS_IN_WATER,
                PROPS_MAX_HEALTH,
                ARROW_REPLENISH_COOLDOWN,
                ALLOW_BLOCKS,
                ALLOW_SPECIALS,
                PROP_COMPASS_COOLDOWN_START,
                PROP_COMPASS_COOLDOWN,
                PROP_COMPASS_DURATION,
                STUN_JUICE_COOLDOWN,
                STUN_JUICE_DURATION,
                STUN_JUICE_RANGE,
                GLOW_DUST_COOLDOWN,
                GLOW_DUST_DURATION,
                GLOW_DUST_RANGE,
                TELEPORTER_COOLDOWN,
                TELEPORTER_RANGE,
                COCAINE_COOLDOWN,
                COCAINE_DURATION,
                PROP_RANDOMIZER_COOLDOWN
        );
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
