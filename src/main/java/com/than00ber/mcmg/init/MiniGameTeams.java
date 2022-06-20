package com.than00ber.mcmg.init;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.MiniGameTeam;
import com.than00ber.mcmg.minigames.HideNSeekMiniGame;
import com.than00ber.mcmg.minigames.PropHuntMiniGame;
import com.than00ber.mcmg.util.ChatUtil;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Team;

public class MiniGameTeams {

    /**
     * Common Teams
     */
    public static final MiniGameTeam SPECTATORS = new MiniGameTeam.Builder("spectators")
            .setDisplayName("Spectator")
            .setColor(ChatColor.BLUE)
            .setSpectator()
            .prepare(MiniGameTeams::setSpectatorPlayer)
            .build();

    /**
     * Werewolf Teams
     */
    public static final MiniGameTeam VILLAGERS = new MiniGameTeam.Builder("villagers")
            .setDisplayName("Villager")
            .setWeight(0.75)
            .setThreshold(0)
            .setColor(ChatColor.GREEN)
            .setVisibility(Team.OptionStatus.NEVER)
            .setObjective("Simple, eliminate all werewolves.")
            .setCatchPhrase("Get rid of all Werewolves.")
            .setSound(Sound.ENTITY_VILLAGER_AMBIENT)
            .prepare(MiniGameTeams::setWerewolfPlayer)
            .build();
    public static final MiniGameTeam WEREWOLVES = new MiniGameTeam.Builder("werewolves")
            .setDisplayName("Werewolf")
            .setWeight(0.15)
            .setThreshold(0)
            .setColor(ChatColor.DARK_RED)
            .setVisibility(Team.OptionStatus.FOR_OWN_TEAM)
            .setObjective("Kill all villagers to declare victory.")
            .setCatchPhrase("Shh, they shouldn't suspect a thing...")
            .setSound(Sound.ENTITY_WOLF_GROWL)
            .prepare(MiniGameTeams::setWerewolfPlayer)
            .isRequired()
            .build();
    public static final MiniGameTeam TRAITORS = new MiniGameTeam.Builder("traitors")
            .setDisplayName("Traitor")
            .setWeight(0.05)
            .setThreshold(4)
            .setColor(ChatColor.GOLD)
            .setVisibility(Team.OptionStatus.NEVER)
            .setObjective("Help werewolves by killing all villagers.")
            .setCatchPhrase("I like villagers. But I prefer werewolves.")
            .setSound(Sound.ENTITY_PILLAGER_CELEBRATE)
            .prepare(MiniGameTeams::setWerewolfPlayer)
            .build();
    public static final MiniGameTeam VAMPIRES = new MiniGameTeam.Builder("vampires")
            .setDisplayName("Vampire")
            .setWeight(0.05)
            .setThreshold(9)
            .setColor(ChatColor.RED)
            .setVisibility(Team.OptionStatus.NEVER)
            .setObjective("Win if either Villagers or Werewolves win.")
            .setCatchPhrase("I'll do anything to win.")
            .setSound(Sound.ENTITY_BAT_AMBIENT)
            .prepare(MiniGameTeams::setWerewolfPlayer)
            .build();
    public static final MiniGameTeam POSSESSED = new MiniGameTeam.Builder("possessed")
            .setDisplayName("Villager")
            .setWeight(0.05)
            .setThreshold(9)
            .setColor(ChatColor.GREEN)
            .setVisibility(Team.OptionStatus.NEVER)
            .setObjective("Simple, eliminate all werewolves.")
            .setCatchPhrase("Get rid of all Werewolves.")
            .setSound(Sound.ENTITY_VILLAGER_AMBIENT)
            .prepare(MiniGameTeams::setWerewolfPlayer)
            .build();

    /**
     * Prophunt Teams
     */
    public static final MiniGameTeam PROPS = new MiniGameTeam.Builder("props")
            .setDisplayName("Prop")
            .setWeight(0.75)
            .setColor(ChatColor.YELLOW)
            .setVisibility(Team.OptionStatus.FOR_OWN_TEAM)
            .setObjective("Hide and survive for as long as possible.")
            .setCatchPhrase("I've always wanted to be a pot.")
            .setSound(Sound.ENTITY_ARMOR_STAND_PLACE)
            .prepare(MiniGameTeams::setPropPlayer)
            .build();
    public static final MiniGameTeam HUNTERS = new MiniGameTeam.Builder("hunters")
            .setDisplayName("Hunter")
            .setWeight(0.25)
            .setColor(ChatColor.BLUE)
            .setVisibility(Team.OptionStatus.FOR_OWN_TEAM)
            .setObjective("Find and eliminate all props.")
            .setCatchPhrase("Something's not right. I can feel it.")
            .setSound(Sound.BLOCK_ANVIL_LAND)
            .prepare(MiniGameTeams::setHunterPlayer)
            .isRequired()
            .disableWhileInGrace()
            .build();

    /**
     * HideNSeek Teams
     */
    public static final MiniGameTeam HIDERS = new MiniGameTeam.Builder("hiders")
            .setDisplayName("Hider")
            .setWeight(0.75)
            .setColor(ChatColor.DARK_GREEN)
            .setVisibility(Team.OptionStatus.FOR_OWN_TEAM)
            .setObjective("Hide and survive for as long as possible.")
            .setCatchPhrase("I've always wanted to be a pot.")
            .setSound(Sound.ENTITY_ARMOR_STAND_PLACE)
            .prepare(MiniGameTeams::setHiderPlayer)
            .build();
    public static final MiniGameTeam SEEKERS = new MiniGameTeam.Builder("seekers")
            .setDisplayName("Seeker")
            .setWeight(0.25)
            .setColor(ChatColor.DARK_RED)
            .setVisibility(Team.OptionStatus.FOR_OWN_TEAM)
            .setObjective("Find and eliminate all hiders.")
            .setCatchPhrase("Something's not right. I can feel it.")
            .setSound(Sound.BLOCK_ANVIL_LAND)
            .prepare(MiniGameTeams::setSeekerPlayer)
            .isRequired()
            .disableWhileInGrace()
            .build();

    /**
     * MiniGameTeam Player Preparation Helper Methods
     */
    public static void resetPlayer(Player player) {
        DisguiseAPI.undisguiseToAll(player);
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
        for (PotionEffect potion : player.getActivePotionEffects()) {
            player.removePotionEffect(potion.getType());
        }
    }

    private static void setSpectatorPlayer(Player player) {
        resetPlayer(player);
        player.setGameMode(GameMode.SPECTATOR);
    }

    private static void setWerewolfPlayer(Player player) {
        resetPlayer(player);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
        player.setHealth(40);
        ItemStack hoe = MiniGameItems.SURVIVORS_WEAPON.get();
        ItemStack food = MiniGameItems.SURVIVORS_FOOD.get();
        food.setAmount(5);
        player.getInventory().addItem(hoe, food);
    }

    private static void setPropPlayer(Player player) {
        resetPlayer(player);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(PropHuntMiniGame.PROPS_MAX_HEALTH.get());
        player.setInvisible(true);
        player.getInventory().addItem(MiniGameItems.STUN_INK.get());
        Material material = Main.WORLD.getBlockAt(player.getLocation().add(0, -1, 0)).getType();
        MiscDisguise disguise = new MiscDisguise(DisguiseType.FALLING_BLOCK, material);
        DisguiseAPI.disguiseToAll(player, disguise);
        DisguiseAPI.setActionBarShown(player, false);
        String name = material.name().replace('_', ' ');
        String formatted = ChatColor.ITALIC + WordUtils.capitalize(name.toLowerCase());
        String message = ChatColor.RESET + "You are disguised as a " + ChatColor.YELLOW + formatted;
        ChatUtil.toActionBar(player, message);
    }

    private static void setHunterPlayer(Player player) {
        resetPlayer(player);
        ItemStack axe = MiniGameItems.HUNTERS_SWORD.get();
        ItemStack bow = MiniGameItems.HUNTERS_BOW.get();
        ItemStack arrow = MiniGameItems.HUNTERS_ARROWS.get();
        ItemStack compass = MiniGameItems.HUNTERS_COMPASS.get();
        int cooldown = PropHuntMiniGame.COMPASS_COOLDOWN_START.get() * 20;
        player.setCooldown(compass.getType(), cooldown);
        player.getInventory().addItem(axe, bow, compass);
        player.getInventory().setItem(8, arrow);
    }

    private static void setHiderPlayer(Player player) {
        resetPlayer(player);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(HideNSeekMiniGame.HIDER_MAX_HEALTH.get());
        DisguiseType disguiseType = DisguiseType.getType(HideNSeekMiniGame.ENTITY_TYPE.get());
        Disguise disguise = disguiseType.isMob()
                ? new MobDisguise(disguiseType)
                : new MiscDisguise(disguiseType);
        DisguiseAPI.disguiseToAll(player, disguise.setViewSelfDisguise(HideNSeekMiniGame.VIEW_DISGUISE.get()));
        DisguiseAPI.setActionBarShown(player, false);
    }

    private static void setSeekerPlayer(Player player) {
        resetPlayer(player);
        ItemStack axe = MiniGameItems.SEEKERS_AXE.get();
        ItemStack bow = MiniGameItems.SEEKERS_BOW.get();
        player.getInventory().addItem(axe, bow);
    }
}
