package com.than00ber.mcmg.init;

import com.than00ber.mcmg.game.MiniGame;
import com.than00ber.mcmg.game.minigames.hidenseek.HideNSeekGame;
import com.than00ber.mcmg.game.minigames.propshunt.PropHuntGame;
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
            .setWinners(GameTeams.VAMPIRES)
            .setCondition(state -> {
                boolean hasVampire = state.PLAYERS_ALIVE.containsValue(GameTeams.VAMPIRES);
                boolean noVillagers = !state.PLAYERS_ALIVE.containsValue(GameTeams.VILLAGERS);
                boolean noWerewolves = !state.PLAYERS_ALIVE.containsValue(GameTeams.WEREWOLVES);
                return hasVampire && (noVillagers || noWerewolves);
            }).build();
    public static final WinCondition<WerewolfGame> ALL_VILLAGERS_DEAD = new WinCondition.Builder<WerewolfGame>()
            .setLoseReason("All villagers got killed.")
            .setWinReason("Werewolves have killed all villagers.")
            .setWinners(GameTeams.WEREWOLVES, GameTeams.TRAITORS)
            .setCondition(state -> !state.PLAYERS_ALIVE.containsValue(GameTeams.VILLAGERS))
            .build();
    public static final WinCondition<WerewolfGame> ALL_WEREWOLVES_DEAD = new WinCondition.Builder<WerewolfGame>()
            .setLoseReason("Villagers managed to kill all werewolves.")
            .setWinReason("Villagers have survived the terror of the werewolves.")
            .setWinners(GameTeams.VILLAGERS, GameTeams.POSSESSED)
            .setCondition(state -> !state.PLAYERS_ALIVE.containsValue(GameTeams.WEREWOLVES))
            .build();

    /**
     * Prophunt Win Conditions
     */
    public static final WinCondition<PropHuntGame> NO_PROPS = new WinCondition.Builder<PropHuntGame>()
            .setLoseReason("All props have been eliminated.")
            .setWinReason("You have spotted all props.")
            .setWinners(GameTeams.HUNTERS)
            .setCondition(state -> !state.getParticipants().containsValue(GameTeams.PROPS))
            .build();
    public static final WinCondition<PropHuntGame> PROPS_SURVIVED = new WinCondition.Builder<PropHuntGame>()
            .setLoseReason("Some props have survived.")
            .setWinReason("You managed to stay alive.")
            .setWinners(GameTeams.PROPS)
            .build();

    /**
     * HideNSeek Win Conditions
     */
    public static final WinCondition<HideNSeekGame> NO_HIDERS = new WinCondition.Builder<HideNSeekGame>()
            .setLoseReason("All hiders have been eliminated.")
            .setWinReason("You have spotted all hiders.")
            .setWinners(GameTeams.SEEKERS)
            .setCondition(state -> state.HIDERS.isEmpty())
            .build();
    public static final WinCondition<HideNSeekGame> HIDERS_SURVIVED = new WinCondition.Builder<HideNSeekGame>()
            .setLoseReason("Some hiders have survived.")
            .setWinReason("You managed to stay alive.")
            .setWinners(GameTeams.HIDERS)
            .build();
}
