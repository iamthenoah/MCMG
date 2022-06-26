package com.than00ber.mcmg.registries;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.core.MiniGameTeam;
import com.than00ber.mcmg.core.Registry;
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

public class Teams {

    public static final Registry<MiniGameTeam> TEAMS = new Registry<>(Registry.Registries.TEAMS);

    // Misc Teams
    public static final MiniGameTeam SPECTATORS = register(new MiniGameTeam.Builder("Spectator")
            .setColor(ChatColor.BLUE)
            .setSpectator()
            .prepare(player -> {
                resetPlayer(player);
                player.setGameMode(GameMode.SPECTATOR);
            })
            .build());

    // Werewolf Teams
    public static final MiniGameTeam VILLAGERS = register(new MiniGameTeam.Builder("Villager")
            .setWeight(0.75)
            .setThreshold(0)
            .setColor(ChatColor.GREEN)
            .setVisibility(Team.OptionStatus.NEVER)
            .setObjective("Simple, eliminate all werewolves.")
            .setCatchPhrase("Get rid of all Werewolves.")
            .setSound(Sound.ENTITY_VILLAGER_AMBIENT)
            .prepare(player -> {
                resetPlayer(player);
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
                player.setHealth(40);
                ItemStack hoe = Items.SURVIVORS_WEAPON.toItemStack();
                ItemStack food = Items.SURVIVORS_FOOD.toItemStack();
                ItemStack rules = Items.RULE_BOOK.toItemStack();
                food.setAmount(5);
                player.getInventory().addItem(hoe, food);
                player.getInventory().setItem(8, rules);
            })
            .build());
    public static final MiniGameTeam WEREWOLVES = register(new MiniGameTeam.Builder("Werewolve")
            .setWeight(0.15)
            .setThreshold(0)
            .setColor(ChatColor.DARK_RED)
            .setVisibility(Team.OptionStatus.FOR_OWN_TEAM)
            .setObjective("Kill all villagers to declare victory.")
            .setCatchPhrase("Shh, they shouldn't suspect a thing...")
            .setSound(Sound.ENTITY_WOLF_GROWL)
            .isRequired()
            .prepare(VILLAGERS::prepare)
            .build());
    public static final MiniGameTeam TRAITORS = register(new MiniGameTeam.Builder("Traitor")
            .setWeight(0.05)
            .setThreshold(4)
            .setColor(ChatColor.GOLD)
            .setVisibility(Team.OptionStatus.NEVER)
            .setObjective("Help werewolves by killing all villagers.")
            .setCatchPhrase("I like villagers. But I prefer werewolves.")
            .setSound(Sound.ENTITY_PILLAGER_CELEBRATE)
            .prepare(VILLAGERS::prepare)
            .build());
    public static final MiniGameTeam VAMPIRES = register(new MiniGameTeam.Builder("Vampire")
            .setWeight(0.05)
            .setThreshold(9)
            .setColor(ChatColor.RED)
            .setVisibility(Team.OptionStatus.NEVER)
            .setObjective("Win if either Villagers or Werewolves win.")
            .setCatchPhrase("I'll do anything to win.")
            .setSound(Sound.ENTITY_BAT_AMBIENT)
            .prepare(VILLAGERS::prepare)
            .build());
    public static final MiniGameTeam POSSESSED = register(new MiniGameTeam.Builder("Possessed")
            .setDisplayName("Villager")
            .setWeight(0.05)
            .setThreshold(9)
            .setColor(ChatColor.GREEN)
            .setVisibility(Team.OptionStatus.NEVER)
            .setObjective("Simple, eliminate all werewolves.")
            .setCatchPhrase("Get rid of all Werewolves.")
            .setSound(Sound.ENTITY_VILLAGER_AMBIENT)
            .prepare(VILLAGERS::prepare)
            .build());

    // Prophunt Teams
    public static final MiniGameTeam PROPS = register(new MiniGameTeam.Builder("Prop")
            .setWeight(0.75)
            .setColor(ChatColor.YELLOW)
            .setVisibility(Team.OptionStatus.NEVER)
            .setObjective("Hide and survive for as long as possible.")
            .setCatchPhrase("I've always wanted to be a pot.")
            .setSound(Sound.ENTITY_ARMOR_STAND_PLACE)
            .isRequired()
            .prepare(player -> {
                resetPlayer(player);
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(PropHuntMiniGame.PROPS_MAX_HEALTH.get());
                player.setInvisible(true);
                ItemStack stunInk = Items.STUN_INK.toItemStack();
                ItemStack glowDust = Items.GLOW_DUST.toItemStack();
                ItemStack propCocaine = Items.COCAINE.toItemStack();
                player.getInventory().addItem(stunInk, glowDust, propCocaine);
                Material material = Main.WORLD.getBlockAt(player.getLocation().add(0, -1, 0)).getType();
                MiscDisguise disguise = new MiscDisguise(DisguiseType.FALLING_BLOCK, material);
                DisguiseAPI.disguiseToAll(player, disguise);
                DisguiseAPI.setActionBarShown(player, false);
                String name = material.name().replace('_', ' ');
                String formatted = ChatColor.ITALIC + WordUtils.capitalize(name.toLowerCase());
                String message = ChatColor.RESET + "You are disguised as a " + ChatColor.YELLOW + formatted;
                ChatUtil.toActionBar(player, message);
            })
            .build());
    public static final MiniGameTeam HUNTERS = register(new MiniGameTeam.Builder("Hunter")
            .setWeight(0.25)
            .setColor(ChatColor.BLUE)
            .setVisibility(Team.OptionStatus.FOR_OTHER_TEAMS)
            .setObjective("Find and eliminate all props.")
            .setCatchPhrase("Something's not right. I can feel it.")
            .setSound(Sound.BLOCK_ANVIL_LAND)
            .isRequired()
            .disableWhileInGrace()
            .prepare(player -> {
                resetPlayer(player);
                ItemStack axe = Items.HUNTERS_SWORD.toItemStack();
                ItemStack bow = Items.HUNTERS_BOW.toItemStack();
                ItemStack arrow = Items.HUNTERS_ARROW.toItemStack();
                ItemStack compass = Items.PROP_COMPASS.toItemStack();
                ItemStack teleporter = Items.TELEPORTER.toItemStack();
                ItemStack propRandomizer = Items.PROP_RANDOMIZER.toItemStack();
                player.setCooldown(compass.getType(), PropHuntMiniGame.PROP_COMPASS_COOLDOWN_START.get() * 20);
                player.setCooldown(teleporter.getType(), PropHuntMiniGame.TELEPORTER_COOLDOWN_START.get() * 20);
                player.setCooldown(propRandomizer.getType(), PropHuntMiniGame.PROP_RANDOMIZER_COOLDOWN_START.get() * 20);
                player.getInventory().addItem(axe, bow, compass, teleporter, propRandomizer);
                player.getInventory().setItem(8, arrow);
            })
            .build());

    // HideNSeek Teams
    public static final MiniGameTeam HIDERS = register(new MiniGameTeam.Builder("Hider")
            .setWeight(0.75)
            .setColor(ChatColor.DARK_GREEN)
            .setVisibility(Team.OptionStatus.FOR_OWN_TEAM)
            .setObjective("Hide and survive for as long as possible.")
            .setCatchPhrase("I've always wanted to be a pot.")
            .setSound(Sound.ENTITY_ARMOR_STAND_PLACE)
            .prepare(player -> {
                resetPlayer(player);
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(HideNSeekMiniGame.HIDER_MAX_HEALTH.get());
                DisguiseType disguiseType = DisguiseType.getType(HideNSeekMiniGame.ENTITY_TYPE.get());
                Disguise disguise = disguiseType.isMob()
                        ? new MobDisguise(disguiseType)
                        : new MiscDisguise(disguiseType);
                DisguiseAPI.disguiseToAll(player, disguise.setViewSelfDisguise(HideNSeekMiniGame.VIEW_DISGUISE.get()));
                DisguiseAPI.setActionBarShown(player, false);
            })
            .build());
    public static final MiniGameTeam SEEKERS = register(new MiniGameTeam.Builder("Seeker")
            .setWeight(0.25)
            .setColor(ChatColor.DARK_RED)
            .setVisibility(Team.OptionStatus.FOR_OWN_TEAM)
            .setObjective("Find and eliminate all hiders.")
            .setCatchPhrase("Something's not right. I can feel it.")
            .setSound(Sound.BLOCK_ANVIL_LAND)
            .prepare(player -> {
                resetPlayer(player);
                ItemStack axe = Items.SEEKERS_AXE.toItemStack();
                ItemStack bow = Items.SEEKERS_BOW.toItemStack();
                player.getInventory().addItem(axe, bow);
            })
            .isRequired()
            .disableWhileInGrace()
            .build());

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

    private static MiniGameTeam register(MiniGameTeam team) {
        return TEAMS.register(team.getName(), () -> team);
    }
}