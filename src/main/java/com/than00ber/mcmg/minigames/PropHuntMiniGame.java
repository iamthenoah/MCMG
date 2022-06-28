package com.than00ber.mcmg.minigames;

import com.google.common.collect.ImmutableList;
import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.core.MiniGameEvent;
import com.than00ber.mcmg.core.MiniGameTeam;
import com.than00ber.mcmg.core.WinCondition;
import com.than00ber.mcmg.core.configuration.ConfigurableProperty;
import com.than00ber.mcmg.events.PropHuntMiniGameEvents;
import com.than00ber.mcmg.registries.Teams;
import com.than00ber.mcmg.registries.WinConditions;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class PropHuntMiniGame extends MiniGame {

    public static final ConfigurableProperty.BooleanProperty PROPS_IN_WATER = new ConfigurableProperty.BooleanProperty(
            "blocks.inWater", false);
    public static final ConfigurableProperty.IntegerProperty PROPS_MAX_HEALTH = new ConfigurableProperty.IntegerProperty(
            "health.props", 4).verify(ConfigurableProperty.CheckIf.POSITIVE_INT);
    public static final ConfigurableProperty.IntegerProperty ARROW_REPLENISH_COOLDOWN = new ConfigurableProperty.IntegerProperty(
            "replenish.seconds", 10).verify(ConfigurableProperty.CheckIf.POSITIVE_INT);
    public static final ConfigurableProperty.BooleanProperty ALLOW_BLOCKS = new ConfigurableProperty.BooleanProperty(
            "allow.solids", true);
    public static final ConfigurableProperty.BooleanProperty ALLOW_SPECIALS = new ConfigurableProperty.BooleanProperty(
            "allow.specials", true);

    // public final List<Integer> DROP_EVENTS = new ArrayList<>();

    public PropHuntMiniGame(Main instance, World world) {
        super(world);
        setEventListener(new PropHuntMiniGameEvents(instance, this));
        addProperties(
                PROPS_IN_WATER,
                PROPS_MAX_HEALTH,
                ARROW_REPLENISH_COOLDOWN,
                ALLOW_BLOCKS,
                ALLOW_SPECIALS);
    }

    @Override
    public String getName() {
        return "PropHunt";
    }

    @Override
    public ImmutableList<MiniGameTeam> getMiniGameTeams() {
        return ImmutableList.of(
                Teams.HUNTERS,
                Teams.PROPS);
    }

    @Override
    public ImmutableList<WinCondition> getWinConditions() {
        return ImmutableList.of(
                WinConditions.NO_PROPS,
                WinConditions.NO_HUNTERS);
    }

    @Override
    public void onMinigameStarted(List<Player> participants) {
        super.onMinigameStarted(participants);
        getWorld().setDifficulty(Difficulty.PEACEFUL);

        // DROP_EVENTS.clear();
        // Random random = new Random();
        // for (int i = 0; i < 3; i++) {
        // DROP_EVENTS.add(random.nextInt(getOptions().getDurationRound()));
        // }
        // Console.debug(DROP_EVENTS);
        // Console.debug(getOptions().getDurationRound());
    }

    @Override
    public void onRoundCycled(MiniGameEvent event) {
        event.setWinCondition(WinConditions.PROPS_SURVIVED);
    }

    @Override
    public void onMiniGameTick(MiniGameEvent event) {
        // ImmutableList<Player> players = getAllInTeam(MiniGameTeams.PROPS);
        // Player prop = players.get(new Random().nextInt(players.size()));
        // Console.warn("data + " + getWorld().getBlockData(prop.getLocation().add(0,
        // -1, 0)));
        //
        // if (DROP_EVENTS.contains(event.getCurrentTick())) {
        // FallingBlock entity = getWorld().spawnFallingBlock(prop.getLocation().add(0,
        // 1, 0), Material.ACACIA_BOAT, (byte) 0);
        // ScheduleUtil.doDelayed(60, entity::remove);
        // entity.addPassenger(prop);
        // entity.setVelocity(new Vector(0, 0, 0));
        // entity.setGlowing(true);
        // }
    }
}
