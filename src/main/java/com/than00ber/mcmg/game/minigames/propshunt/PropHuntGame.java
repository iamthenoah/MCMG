package com.than00ber.mcmg.game.minigames.propshunt;

import com.than00ber.mcmg.game.MiniGame;
import com.than00ber.mcmg.game.MiniGameEvent;
import com.than00ber.mcmg.init.GameTeams;
import com.than00ber.mcmg.init.WinConditions;
import com.than00ber.mcmg.objects.GameTeam;
import com.than00ber.mcmg.objects.WinCondition;
import com.than00ber.mcmg.util.ChatUtil;
import com.than00ber.mcmg.util.config.GameProperty;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class PropHuntGame extends MiniGame {

    private final GameProperty.BooleanProperty blocksOnly = new GameProperty.BooleanProperty("blocks.solid", false);

    public final HashMap<Player, GameTeam> PROPS;
    public final HashMap<Player, GameTeam> HUNTERS;

    public PropHuntGame(World world) {
        super(world);
        setEventListener(new PropHuntGameEventListener(this));
        addProperties(blocksOnly);
        PROPS = new HashMap<>();
        HUNTERS = new HashMap<>();
    }

    public boolean solidsOnly() {
        return blocksOnly.get();
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
    public void onGameStarted() {
        super.onGameStarted();

        assignRandomRoles();
        ChatUtil.showRoundStartScreen(getParticipants());

        Random random = new Random();
        getParticipants().forEach((p, r) -> {
            if (r.equals(GameTeams.PROPS)) {
                PROPS.put(p, r);
                p.setInvisible(true);

                Material randomMaterial;
                do {
                    int i = random.nextInt(Material.values().length - 1);
                    randomMaterial = Material.values()[i];
                } while (!randomMaterial.isBlock() && solidsOnly());

                MiscDisguise disguise = new MiscDisguise(DisguiseType.FALLING_BLOCK, randomMaterial);
                DisguiseAPI.disguiseToAll(p, disguise);
            } else {
                HUNTERS.put(p, r);
            }
        });
    }

    @Override
    public void onGameEnded() {
        super.onGameEnded();
        PROPS.forEach((p, r) -> DisguiseAPI.undisguiseToAll(p));
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
