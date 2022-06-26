package com.than00ber.mcmg.registries;

import com.than00ber.mcmg.core.WinCondition;
import com.than00ber.mcmg.minigames.HideNSeekMiniGame;
import com.than00ber.mcmg.minigames.MiniGame;
import com.than00ber.mcmg.minigames.PropHuntMiniGame;
import com.than00ber.mcmg.minigames.WerewolfMiniGame;

public class AllWinConditions {

    /**
     * Common Win Conditions
     */
    public static final WinCondition<MiniGame> EVERYONE_DEAD = new WinCondition.Builder<MiniGame>()
            .setLoseReason("Nobody managed to survive.")
            .setCondition(state -> state.getCurrentPlayerRoles().isEmpty())
            .build();

    /**
     * Werewolf Win Conditions
     */
    public static final WinCondition<WerewolfMiniGame> VAMPIRE_VICTORY = new WinCondition.Builder<WerewolfMiniGame>()
            .setWinReason("All villagers or werewolves have died.")
            .setLoseReason("The vampire has survived long enough to stealing the victory.")
            .setWinners(AllTeams.VAMPIRES)
            .setCondition(state -> {
                boolean hasVampire = state.getCurrentPlayerRoles().containsValue(AllTeams.VAMPIRES);
                boolean noVillagers = !state.getCurrentPlayerRoles().containsValue(AllTeams.VILLAGERS);
                boolean noWerewolves = !state.getCurrentPlayerRoles().containsValue(AllTeams.WEREWOLVES);
                return hasVampire && (noVillagers || noWerewolves);
            }).build();
    public static final WinCondition<WerewolfMiniGame> ALL_VILLAGERS_DEAD = new WinCondition.Builder<WerewolfMiniGame>()
            .setLoseReason("All villagers got killed.")
            .setWinReason("Werewolves have killed all villagers.")
            .setWinners(AllTeams.WEREWOLVES, AllTeams.TRAITORS)
            .setCondition(state -> !state.getCurrentPlayerRoles().containsValue(AllTeams.VILLAGERS))
            .build();
    public static final WinCondition<WerewolfMiniGame> ALL_WEREWOLVES_DEAD = new WinCondition.Builder<WerewolfMiniGame>()
            .setLoseReason("Villagers managed to kill all werewolves.")
            .setWinReason("Villagers have survived the terror of the werewolves.")
            .setWinners(AllTeams.VILLAGERS, AllTeams.POSSESSED)
            .setCondition(state -> !state.getCurrentPlayerRoles().containsValue(AllTeams.WEREWOLVES))
            .build();

    /**
     * Prophunt Win Conditions
     */
    public static final WinCondition<PropHuntMiniGame> NO_PROPS = new WinCondition.Builder<PropHuntMiniGame>()
            .setLoseReason("All props have been eliminated.")
            .setWinReason("You have spotted all props.")
            .setWinners(AllTeams.HUNTERS)
            .setCondition(state -> !state.getCurrentPlayerRoles().containsValue(AllTeams.PROPS))
            .build();
    public static final WinCondition<PropHuntMiniGame> NO_HUNTERS = new WinCondition.Builder<PropHuntMiniGame>()
            .setLoseReason("Somehow all hunters died.")
            .setWinReason("The other team had no coordination.")
            .setWinners(AllTeams.PROPS)
            .setCondition(state -> !state.getCurrentPlayerRoles().containsValue(AllTeams.HUNTERS))
            .build();
    public static final WinCondition<PropHuntMiniGame> PROPS_SURVIVED = new WinCondition.Builder<PropHuntMiniGame>()
            .setLoseReason("Some props have survived.")
            .setWinReason("You managed to stay alive.")
            .setWinners(AllTeams.PROPS)
            .build();

    /**
     * HideNSeek Win Conditions
     */
    public static final WinCondition<HideNSeekMiniGame> NO_HIDERS = new WinCondition.Builder<HideNSeekMiniGame>()
            .setLoseReason("All hiders have been eliminated.")
            .setWinReason("You have spotted all hiders.")
            .setWinners(AllTeams.SEEKERS)
            .setCondition(state -> !state.getCurrentPlayerRoles().containsValue(AllTeams.HIDERS))
            .build();
    public static final WinCondition<HideNSeekMiniGame> HIDERS_SURVIVED = new WinCondition.Builder<HideNSeekMiniGame>()
            .setLoseReason("Some hiders have survived.")
            .setWinReason("You managed to stay alive.")
            .setWinners(AllTeams.HIDERS)
            .build();
}
