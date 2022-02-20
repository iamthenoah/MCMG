package com.than00ber.mcmg.game.minigames.propshunt;

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

public class PropsHuntGame extends MiniGame {

    public final HashMap<Prop, GameTeam> PROPS;
    public final HashMap<Player, GameTeam> HUNTERS;

    public PropsHuntGame(World world) {
        super(world);
        setEventListener(new PropsHuntGameEventListener(this));
        PROPS = new HashMap<>();
        HUNTERS = new HashMap<>();
    }

    @Override
    public String getGameName() {
        return "Propshunt";
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
    public void onGameStarted() {
        super.onGameStarted();

        assignRandomRoles();
        ChatUtil.showRoundStartScreen(getParticipants());

        getParticipants().forEach((p, r) -> {
            if (r.equals(GameTeams.PROPS)) {
                PROPS.put(new Prop(p), r);
            } else {
                HUNTERS.put(p, r);
            }
        });
    }

    @Override
    public void onRoundStarted(MiniGameEvent event) {
        event.getBossBar().setTitle("Time Remaining");
    }

    @Override
    public void onRoundCycled(MiniGameEvent event) {
        event.setWinCondition(WinConditions.PROPS_SURVIVED);
    }
}
