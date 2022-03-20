package com.than00ber.mcmg.init;

import com.than00ber.mcmg.objects.GameTeam;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Team;

import java.util.Random;

public class GameTeams {

    /**
     * Common Teams
     */
    public static final GameTeam SPECTATORS = new GameTeam.Builder("spectators")
            .setDisplayName("Spectator")
            .setColor(ChatColor.BLUE)
            .setSpectator()
            .prepare(player -> {
                resetPlayer(player);
                player.setGameMode(GameMode.SPECTATOR);
            })
            .build();

    /**
     * Werewolf Teams
     */
    public static final GameTeam VILLAGERS = new GameTeam.Builder("villagers")
            .setDisplayName("Villager")
            .setWeight(0.75)
            .setThreshold(0)
            .setColor(ChatColor.GREEN)
            .setVisibility(Team.OptionStatus.NEVER)
            .setObjective("Simple, eliminate all werewolves.")
            .setCatchPhrase("Get rid of all Werewolves.")
            .setSound(Sound.ENTITY_VILLAGER_AMBIENT)
            .prepare(GameTeams::setWerewolfPlayer)
            .build();
    public static final GameTeam WEREWOLVES = new GameTeam.Builder("werewolves")
            .setDisplayName("Werewolf")
            .setWeight(0.15)
            .setThreshold(0)
            .setColor(ChatColor.DARK_RED)
            .setVisibility(Team.OptionStatus.FOR_OWN_TEAM)
            .setObjective("Kill all villagers to declare victory.")
            .setCatchPhrase("Shh, they shouldn't suspect a thing...")
            .setSound(Sound.ENTITY_WOLF_GROWL)
            .prepare(GameTeams::setWerewolfPlayer)
            .isRequired()
            .build();
    public static final GameTeam TRAITORS = new GameTeam.Builder("traitors")
            .setDisplayName("Traitor")
            .setWeight(0.05)
            .setThreshold(4)
            .setColor(ChatColor.GOLD)
            .setVisibility(Team.OptionStatus.NEVER)
            .setObjective("Help werewolves by killing all villagers.")
            .setCatchPhrase("I like villagers. But I prefer werewolves.")
            .setSound(Sound.ENTITY_PILLAGER_CELEBRATE)
            .prepare(GameTeams::setWerewolfPlayer)
            .build();
    public static final GameTeam VAMPIRES = new GameTeam.Builder("vampires")
            .setDisplayName("Vampire")
            .setWeight(0.05)
            .setThreshold(9)
            .setColor(ChatColor.RED)
            .setVisibility(Team.OptionStatus.NEVER)
            .setObjective("Win if either Villagers or Werewolves win.")
            .setCatchPhrase("I'll do anything to win.")
            .setSound(Sound.ENTITY_BAT_AMBIENT)
            .prepare(GameTeams::setWerewolfPlayer)
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
            .prepare(GameTeams::setWerewolfPlayer)
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
            .prepare(GameTeams::setPropPlayer)
            .build();
    public static final GameTeam HUNTERS = new GameTeam.Builder("hunters")
            .setDisplayName("Hunter")
            .setWeight(0.25)
            .setColor(ChatColor.BLUE)
            .setVisibility(Team.OptionStatus.FOR_OWN_TEAM)
            .setObjective("Find and eliminate all props.")
            .setCatchPhrase("Something's not right. I can feel it.")
            .setSound(Sound.BLOCK_ANVIL_LAND)
            .prepare(GameTeams::setHunterPlayer)
            .isRequired()
            .disableWhileInGrace()
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
            .prepare(GameTeams::setHiderPlayer)
            .build();
    public static final GameTeam SEEKERS = new GameTeam.Builder("seekers")
            .setDisplayName("Seeker")
            .setWeight(0.25)
            .setColor(ChatColor.DARK_RED)
            .setVisibility(Team.OptionStatus.FOR_OWN_TEAM)
            .setObjective("Find and eliminate all hiders.")
            .setCatchPhrase("Something's not right. I can feel it.")
            .setSound(Sound.BLOCK_ANVIL_LAND)
            .prepare(GameTeams::setSeekerPlayer)
            .isRequired()
            .disableWhileInGrace()
            .build();

    /**
     * GameTeam Player Preparation Helper Methods
     */
    public static void resetPlayer(Player player) {
        player.setCollidable(true);
        player.setInvisible(false);
        player.setGlowing(false);
        player.setLevel(0);
        player.setExp(0);
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.setFoodLevel(20);
        player.getInventory().clear();
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        player.setHealth(20);
        DisguiseAPI.undisguiseToAll(player);
        for (PotionEffect potion : player.getActivePotionEffects()) {
            player.removePotionEffect(potion.getType());
        }
    }

    private static void setWerewolfPlayer(Player player) {
        resetPlayer(player);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
        player.setHealth(40);
        ItemStack hoe = GameItems.SURVIVORS_WEAPON.get();
        ItemStack food = GameItems.SURVIVORS_FOOD.get();
        food.setAmount(5);
        player.getInventory().addItem(hoe, food);
    }

    private static void setPropPlayer(Player player) {
        resetPlayer(player);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(10);
        player.setInvisible(true);
        int i = new Random().nextInt(Material.values().length - 1);
        Material randomMaterial = Material.values()[i];
        MiscDisguise disguise = new MiscDisguise(DisguiseType.FALLING_BLOCK, randomMaterial);
        DisguiseAPI.disguiseToAll(player, disguise);
    }

    private static void setHunterPlayer(Player player) {
        resetPlayer(player);
        ItemStack axe = GameItems.HUNTERS_SWORD.get();
        ItemStack bow = GameItems.HUNTERS_BOW.get();
        ItemStack arrow = GameItems.HUNTERS_ARROWS.get();
        player.getInventory().addItem(axe, bow);
        player.getInventory().setItem(8, arrow);
    }

    private static void setHiderPlayer(Player player) {
        resetPlayer(player);
        MobDisguise disguise = new MobDisguise(DisguiseType.VILLAGER);
        disguise.setViewSelfDisguise(false);
        DisguiseAPI.disguiseToAll(player, disguise);
    }

    private static void setSeekerPlayer(Player player) {
        resetPlayer(player);
        ItemStack axe = GameItems.SEEKERS_AXE.get();
        ItemStack bow = GameItems.SEEKERS_BOW.get();
        player.getInventory().addItem(axe, bow);
    }
}
