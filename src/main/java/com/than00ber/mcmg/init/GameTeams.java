package com.than00ber.mcmg.init;

import com.than00ber.mcmg.objects.GameTeam;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.scoreboard.Team;

public class GameTeams {

    /**
     * Common Teams
     */
    public static final GameTeam SPECTATOR = new GameTeam.Builder("spectators")
            .setDisplayName("Spectator")
            .setColor(ChatColor.BLUE)
            .setSpectator()
            .build();

    /**
     * Werewolf Teams
     */
    public static final GameTeam VILLAGER = new GameTeam.Builder("villagers")
            .setDisplayName("Villager")
            .setWeight(0.75)
            .setThreshold(0)
            .setColor(ChatColor.GREEN)
            .setVisibility(Team.OptionStatus.NEVER)
            .setObjective("Simple, eliminate all werewolves.")
            .setCatchPhrase("Get rid of all Werewolves.")
            .setSound(Sound.ENTITY_VILLAGER_AMBIENT)
            .build();
    public static final GameTeam WEREWOLF = new GameTeam.Builder("werewolves")
            .setDisplayName("Werewolf")
            .setWeight(0.15)
            .setThreshold(0)
            .setColor(ChatColor.DARK_RED)
            .setVisibility(Team.OptionStatus.FOR_OWN_TEAM)
            .setObjective("Kill all villagers to declare victory.")
            .setCatchPhrase("Shh, they shouldn't suspect a thing...")
            .setSound(Sound.ENTITY_WOLF_GROWL)
            .build();
    public static final GameTeam TRAITOR = new GameTeam.Builder("traitors")
            .setDisplayName("Traitor")
            .setWeight(0.05)
            .setThreshold(4)
            .setColor(ChatColor.GOLD)
            .setVisibility(Team.OptionStatus.NEVER)
            .setObjective("Help werewolves by killing all villagers.")
            .setCatchPhrase("I like villagers. But I prefer werewolves.")
            .setSound(Sound.ENTITY_PILLAGER_CELEBRATE)
            .build();
    public static final GameTeam VAMPIRE = new GameTeam.Builder("vampires")
            .setDisplayName("Vampire")
            .setWeight(0.05)
            .setThreshold(9)
            .setColor(ChatColor.RED)
            .setVisibility(Team.OptionStatus.NEVER)
            .setObjective("Win if either Villagers or Werewolves win.")
            .setCatchPhrase("I'll do anything to win.")
            .setSound(Sound.ENTITY_BAT_AMBIENT)
            .build();
    public static final GameTeam POSSESSED = new GameTeam.Builder("possessed")
            .setDisplayName("Villager")
            .setWeight(0.05)
            .setThreshold(9)
            .setColor(ChatColor.GREEN)
            .setVisibility(Team.OptionStatus.NEVER)
            .setObjective("Simple, eliminate all werewolves.")
            .setCatchPhrase("Get rid of all Werewolves.")
            .setSound(Sound.ENTITY_VILLAGER_AMBIENT)
            .build();

    /**
     * Prophunt Teams
     */
    public static final GameTeam PROPS = new GameTeam.Builder("props")
            .setDisplayName("Prop")
            .setWeight(0.75)
            .setColor(ChatColor.YELLOW)
            .setVisibility(Team.OptionStatus.FOR_OWN_TEAM)
            .setObjective("Hide and survive for as long as possible.")
            .setCatchPhrase("I've always wanted to be a pot.")
            .setSound(Sound.ENTITY_ARMOR_STAND_PLACE)
            .build();
    public static final GameTeam HUNTERS = new GameTeam.Builder("hunters")
            .setDisplayName("Hunter")
            .setWeight(0.25)
            .setColor(ChatColor.BLUE)
            .setVisibility(Team.OptionStatus.FOR_OWN_TEAM)
            .setObjective("Find and eliminate all props.")
            .setCatchPhrase("Something's not right. I can feel it.")
            .setSound(Sound.BLOCK_ANVIL_LAND)
            .build();

    /**
     * HideNSeek Teams
     */
    public static final GameTeam HIDERS = new GameTeam.Builder("hiders")
            .setDisplayName("Hider")
            .setWeight(0.75)
            .setColor(ChatColor.DARK_GREEN)
            .setVisibility(Team.OptionStatus.FOR_OWN_TEAM)
            .setObjective("Hide and survive for as long as possible.")
            .setCatchPhrase("I've always wanted to be a pot.")
            .setSound(Sound.ENTITY_ARMOR_STAND_PLACE)
            .build();
    public static final GameTeam SEEKERS = new GameTeam.Builder("seekers")
            .setDisplayName("Seeker")
            .setWeight(0.25)
            .setColor(ChatColor.DARK_RED)
            .setVisibility(Team.OptionStatus.FOR_OWN_TEAM)
            .setObjective("Find and eliminate all hiders.")
            .setCatchPhrase("Something's not right. I can feel it.")
            .setSound(Sound.BLOCK_ANVIL_LAND)
            .build();
}
