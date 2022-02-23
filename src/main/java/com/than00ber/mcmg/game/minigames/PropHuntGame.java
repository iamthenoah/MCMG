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
import org.bukkit.World;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PropHuntGame extends MiniGame {

    private final GameProperty.BooleanProperty blocksOnly = new GameProperty.BooleanProperty("_blocks.solid", false);
    private final GameProperty.BooleanProperty nonBlocksOnly = new GameProperty.BooleanProperty("_blocks.nonsolid", false);
    private final GameProperty.BooleanProperty propsInWater = new GameProperty.BooleanProperty("blocks.inWater", false);

    public PropHuntGame(Main instance, World world) {
        super(world);
        setEventListener(new PropHuntGameEventListener(instance, this));
        addProperties(blocksOnly, nonBlocksOnly, propsInWater);
    }

    public boolean canHideInWater() {
        return propsInWater.get();
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
        players.forEach((p, r) -> {
            if (r.equals(GameTeams.HUNTERS)) {
                int duration = durationGrace.get() * 20;
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration, 100));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, 10));
                p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, duration, 250));
            }
        });
    }

    @Override
    public void onRoundStarted(MiniGameEvent event) { }

    @Override
    public void onRoundCycled(MiniGameEvent event) {
        event.setWinCondition(WinConditions.PROPS_SURVIVED);
    }
}
