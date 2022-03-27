package com.than00ber.mcmg.init;

import com.than00ber.mcmg.minigames.HideNSeekMiniGame;
import com.than00ber.mcmg.minigames.MiniGame;
import com.than00ber.mcmg.minigames.PropHuntMiniGame;
import com.than00ber.mcmg.minigames.WerewolfMiniGame;
import com.than00ber.mcmg.objects.WinCondition;

public class WinConditions {

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
            .setWinners(MiniGameTeams.VAMPIRES)
            .setCondition(state -> {
                boolean hasVampire = state.getCurrentPlayerRoles().containsValue(MiniGameTeams.VAMPIRES);
                boolean noVillagers = !state.getCurrentPlayerRoles().containsValue(MiniGameTeams.VILLAGERS);
                boolean noWerewolves = !state.getCurrentPlayerRoles().containsValue(MiniGameTeams.WEREWOLVES);
                return hasVampire && (noVillagers || noWerewolves);
            }).build();
    public static final WinCondition<WerewolfMiniGame> ALL_VILLAGERS_DEAD = new WinCondition.Builder<WerewolfMiniGame>()
            .setLoseReason("All villagers got killed.")
            .setWinReason("Werewolves have killed all villagers.")
            .setWinners(MiniGameTeams.WEREWOLVES, MiniGameTeams.TRAITORS)
            .setCondition(state -> !state.getCurrentPlayerRoles().containsValue(MiniGameTeams.VILLAGERS))
            .build();
    public static final WinCondition<WerewolfMiniGame> ALL_WEREWOLVES_DEAD = new WinCondition.Builder<WerewolfMiniGame>()
            .setLoseReason("Villagers managed to kill all werewolves.")
            .setWinReason("Villagers have survived the terror of the werewolves.")
            .setWinners(MiniGameTeams.VILLAGERS, MiniGameTeams.POSSESSED)
            .setCondition(state -> !state.getCurrentPlayerRoles().containsValue(MiniGameTeams.WEREWOLVES))
            .build();

    /**
     * Prophunt Win Conditions
     */
    public static final WinCondition<PropHuntMiniGame> NO_PROPS = new WinCondition.Builder<PropHuntMiniGame>()
            .setLoseReason("All props have been eliminated.")
            .setWinReason("You have spotted all props.")
            .setWinners(MiniGameTeams.HUNTERS)
            .setCondition(state -> !state.getCurrentPlayerRoles().containsValue(MiniGameTeams.PROPS))
            .build();
    public static final WinCondition<PropHuntMiniGame> NO_HUNTERS = new WinCondition.Builder<PropHuntMiniGame>()
            .setLoseReason("Somehow all hunters died.")
            .setWinReason("The other team had no coordination.")
            .setWinners(MiniGameTeams.PROPS)
            .setCondition(state -> !state.getCurrentPlayerRoles().containsValue(MiniGameTeams.HUNTERS))
            .build();
    public static final WinCondition<PropHuntMiniGame> PROPS_SURVIVED = new WinCondition.Builder<PropHuntMiniGame>()
            .setLoseReason("Some props have survived.")
            .setWinReason("You managed to stay alive.")
            .setWinners(MiniGameTeams.PROPS)
            .build();

    /**
     * HideNSeek Win Conditions
     */
    public static final WinCondition<HideNSeekMiniGame> NO_HIDERS = new WinCondition.Builder<HideNSeekMiniGame>()
            .setLoseReason("All hiders have been eliminated.")
            .setWinReason("You have spotted all hiders.")
            .setWinners(MiniGameTeams.SEEKERS)
            .setCondition(state -> !state.getCurrentPlayerRoles().containsValue(MiniGameTeams.HIDERS))
            .build();
    public static final WinCondition<HideNSeekMiniGame> HIDERS_SURVIVED = new WinCondition.Builder<HideNSeekMiniGame>()
            .setLoseReason("Some hiders have survived.")
            .setWinReason("You managed to stay alive.")
            .setWinners(MiniGameTeams.HIDERS)
            .build();
}
