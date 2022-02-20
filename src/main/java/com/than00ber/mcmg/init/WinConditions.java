package com.than00ber.mcmg.init;

import com.than00ber.mcmg.game.MiniGame;
import com.than00ber.mcmg.game.minigames.propshunt.PropsHuntGame;
import com.than00ber.mcmg.game.minigames.werewolf.WerewolfGame;
import com.than00ber.mcmg.objects.WinCondition;

public class WinConditions {

    /**
     * Common Win Conditions
     */
    public static final WinCondition<MiniGame> EVERYONE_DEAD = new WinCondition.Builder<MiniGame>()
            .setLoseReason("Nobody managed to survive.")
            .setCondition(state -> state.getParticipants().isEmpty())
            .build();

    /**
     * Werewolf Win Conditions
     */
    public static final WinCondition<WerewolfGame> VAMPIRE_VICTORY = new WinCondition.Builder<WerewolfGame>()
            .setWinReason("All villagers or werewolves have died.")
            .setLoseReason("The vampire has survived long enough to stealing the victory.")
            .setWinners(GameTeams.VAMPIRE)
            .setCondition(state -> {
                boolean hasVampire = state.PLAYERS_ALIVE.containsValue(GameTeams.VAMPIRE);
                boolean noVillagers = !state.PLAYERS_ALIVE.containsValue(GameTeams.VILLAGER);
                boolean noWerewolves = !state.PLAYERS_ALIVE.containsValue(GameTeams.WEREWOLF);
                return hasVampire && (noVillagers || noWerewolves);
            }).build();
    public static final WinCondition<WerewolfGame> ALL_VILLAGERS_DEAD = new WinCondition.Builder<WerewolfGame>()
            .setLoseReason("All villagers got killed.")
            .setWinReason("Werewolves have killed all villagers.")
            .setWinners(GameTeams.WEREWOLF, GameTeams.TRAITOR)
            .setCondition(state -> !state.PLAYERS_ALIVE.containsValue(GameTeams.VILLAGER))
            .build();
    public static final WinCondition<WerewolfGame> ALL_WEREWOLVES_DEAD = new WinCondition.Builder<WerewolfGame>()
            .setLoseReason("Villagers managed to kill all werewolves.")
            .setWinReason("Villagers have survived the terror of the werewolves.")
            .setWinners(GameTeams.VILLAGER, GameTeams.POSSESSED)
            .setCondition(state -> !state.PLAYERS_ALIVE.containsValue(GameTeams.WEREWOLF))
            .build();

    /**
     * HideNSeek Win Conditions
     */
    public static final WinCondition<PropsHuntGame> NO_PROPS = new WinCondition.Builder<PropsHuntGame>()
            .setLoseReason("All props have been eliminated.")
            .setWinReason("You have spotted all props.")
            .setWinners(GameTeams.HUNTERS)
            .setCondition(state -> state.PROPS.isEmpty())
            .build();
    public static final WinCondition<PropsHuntGame> PROPS_SURVIVED = new WinCondition.Builder<PropsHuntGame>()
            .setLoseReason("Some props have survived.")
            .setWinReason("You managed to stay alive.")
            .setWinners(GameTeams.PROPS)
            .build();
}
