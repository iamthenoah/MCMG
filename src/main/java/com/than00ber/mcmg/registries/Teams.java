package com.than00ber.mcmg.registries;

import com.than00ber.mcmg.Main;
import com.than00ber.mcmg.core.MiniGameTeam;
import com.than00ber.mcmg.core.Registry;
import com.than00ber.mcmg.minigames.HideNSeekMiniGame;
import com.than00ber.mcmg.minigames.PropHuntMiniGame;
import com.than00ber.mcmg.util.MiniGameUtil;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.scoreboard.Team;

public class Teams {

    public static final Registry<MiniGameTeam> TEAMS = Registry.create("teams");

    // Misc Teams
    public static final MiniGameTeam SPECTATORS = TEAMS.register(() -> new MiniGameTeam.Builder("Spectator")
            .setColor(ChatColor.BLUE)
            .setSpectator()
            .prepare(player -> {
                MiniGameUtil.resetPlayer(player);
                player.setGameMode(GameMode.SPECTATOR);
            })
            .build());

    // Werewolf Teams
    public static final MiniGameTeam VILLAGERS = TEAMS.register(() -> new MiniGameTeam.Builder("Villager")
            .setWeight(0.75)
            .setThreshold(0)
            .setColor(ChatColor.GREEN)
            .setVisibility(Team.OptionStatus.NEVER)
            .setObjective("Simple, eliminate all werewolves.")
            .setCatchPhrase("Get rid of all Werewolves.")
            .setSound(Sound.ENTITY_VILLAGER_AMBIENT)
            .prepare(player -> {
                MiniGameUtil.resetPlayer(player);
                MiniGameUtil.giveMiniGameItem(player, Items.SURVIVORS_WEAPON);
                MiniGameUtil.giveMiniGameItem(player, Items.SURVIVORS_FOOD, 5);
                MiniGameUtil.giveMiniGameItemAt(player, Items.RULE_BOOK, 8);
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
                player.setHealth(40);
            })
            .build());
    public static final MiniGameTeam WEREWOLVES = TEAMS.register(() -> new MiniGameTeam.Builder("Werewolve")
            .setWeight(0.15)
            .setThreshold(0)
            .setColor(ChatColor.DARK_RED)
            .setVisibility(Team.OptionStatus.FOR_OWN_TEAM)
            .setObjective("Kill all villagers to declare victory.")
            .setCatchPhrase("Shh, they shouldn't suspect a thing...")
            .setSound(Sound.ENTITY_WOLF_GROWL)
            .isRequired()
            .prepare(player -> {
                MiniGameUtil.resetPlayer(player);
                MiniGameUtil.giveMiniGameItem(player, Items.SURVIVORS_WEAPON);
                MiniGameUtil.giveMiniGameItem(player, Items.SURVIVORS_FOOD, 5);
                MiniGameUtil.giveMiniGameItemAt(player, Items.RULE_BOOK, 8);
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
                player.setHealth(40);
            })
            .build());
    public static final MiniGameTeam TRAITORS = TEAMS.register(() -> new MiniGameTeam.Builder("Traitor")
            .setWeight(0.05)
            .setThreshold(4)
            .setColor(ChatColor.GOLD)
            .setVisibility(Team.OptionStatus.NEVER)
            .setObjective("Help werewolves by killing all villagers.")
            .setCatchPhrase("I like villagers. But I prefer werewolves.")
            .setSound(Sound.ENTITY_PILLAGER_CELEBRATE)
            .prepare(player -> {
                MiniGameUtil.resetPlayer(player);
                MiniGameUtil.giveMiniGameItem(player, Items.SURVIVORS_WEAPON);
                MiniGameUtil.giveMiniGameItem(player, Items.SURVIVORS_FOOD, 5);
                MiniGameUtil.giveMiniGameItemAt(player, Items.RULE_BOOK, 8);
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
                player.setHealth(40);
            })
            .build());
    public static final MiniGameTeam VAMPIRES = TEAMS.register(() -> new MiniGameTeam.Builder("Vampire")
            .setWeight(0.05)
            .setThreshold(9)
            .setColor(ChatColor.RED)
            .setVisibility(Team.OptionStatus.NEVER)
            .setObjective("Win if either Villagers or Werewolves win.")
            .setCatchPhrase("I'll do anything to win.")
            .setSound(Sound.ENTITY_BAT_AMBIENT)
            .prepare(player -> {
                MiniGameUtil.resetPlayer(player);
                MiniGameUtil.giveMiniGameItem(player, Items.SURVIVORS_WEAPON);
                MiniGameUtil.giveMiniGameItem(player, Items.SURVIVORS_FOOD, 5);
                MiniGameUtil.giveMiniGameItemAt(player, Items.RULE_BOOK, 8);
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
                player.setHealth(40);
            })
            .build());
    public static final MiniGameTeam POSSESSED = TEAMS.register(() -> new MiniGameTeam.Builder("Possessed")
            .setDisplayName("Villager")
            .setWeight(0.05)
            .setThreshold(9)
            .setColor(ChatColor.GREEN)
            .setVisibility(Team.OptionStatus.NEVER)
            .setObjective("Simple, eliminate all werewolves.")
            .setCatchPhrase("Get rid of all Werewolves.")
            .setSound(Sound.ENTITY_VILLAGER_AMBIENT)
            .prepare(player -> {
                MiniGameUtil.resetPlayer(player);
                MiniGameUtil.giveMiniGameItem(player, Items.SURVIVORS_WEAPON);
                MiniGameUtil.giveMiniGameItem(player, Items.SURVIVORS_FOOD, 5);
                MiniGameUtil.giveMiniGameItemAt(player, Items.RULE_BOOK, 8);
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
                player.setHealth(40);
            })
            .build());

    // Prophunt Teams
    public static final MiniGameTeam PROPS = TEAMS.register(() -> new MiniGameTeam.Builder("Prop")
            .setWeight(0.75)
            .setColor(ChatColor.YELLOW)
            .setVisibility(Team.OptionStatus.NEVER)
            .setObjective("Hide and survive for as long as possible.")
            .setCatchPhrase("I've always wanted to be a pot.")
            .setSound(Sound.ENTITY_ARMOR_STAND_PLACE)
            .isRequired()
            .prepare(player -> {
                MiniGameUtil.resetPlayer(player);
                MiniGameUtil.giveMiniGameItem(player, Items.STUN_INK);
                MiniGameUtil.giveMiniGameItem(player, Items.GLOW_DUST);
                MiniGameUtil.giveMiniGameItem(player, Items.COCAINE);
                Block block = Main.WORLD.getBlockAt(player.getLocation().add(0, -1, 0));
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(PropHuntMiniGame.PROPS_MAX_HEALTH.get());
                MiniGameUtil.disguiseAsBlock(player, block);
            })
            .build());
    public static final MiniGameTeam HUNTERS = TEAMS.register(() -> new MiniGameTeam.Builder("Hunter")
            .setWeight(0.25)
            .setColor(ChatColor.BLUE)
            .setVisibility(Team.OptionStatus.FOR_OTHER_TEAMS)
            .setObjective("Find and eliminate all props.")
            .setCatchPhrase("Something's not right. I can feel it.")
            .setSound(Sound.BLOCK_ANVIL_LAND)
            .isRequired()
            .disableWhileInGrace()
            .prepare(player -> {
                MiniGameUtil.resetPlayer(player);
                MiniGameUtil.giveMiniGameItem(player, Items.HUNTERS_SWORD);
                MiniGameUtil.giveMiniGameItem(player, Items.HUNTERS_BOW);
                MiniGameUtil.giveMiniGameItem(player, Items.PROP_COMPASS);
                MiniGameUtil.giveMiniGameItem(player, Items.TELEPORTER);
                MiniGameUtil.giveMiniGameItem(player, Items.PROP_RANDOMIZER);
                MiniGameUtil.giveMiniGameItemAt(player, Items.HUNTERS_ARROW, 8);
            })
            .build());

    // HideNSeek Teams
    public static final MiniGameTeam HIDERS = TEAMS.register(() -> new MiniGameTeam.Builder("Hider")
            .setWeight(0.75)
            .setColor(ChatColor.DARK_GREEN)
            .setVisibility(Team.OptionStatus.FOR_OWN_TEAM)
            .setObjective("Hide and survive for as long as possible.")
            .setCatchPhrase("I've always wanted to be a pot.")
            .setSound(Sound.ENTITY_ARMOR_STAND_PLACE)
            .prepare(player -> {
                MiniGameUtil.resetPlayer(player);
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH)
                        .setBaseValue(HideNSeekMiniGame.HIDER_MAX_HEALTH.get());
                DisguiseType disguiseType = DisguiseType.getType(HideNSeekMiniGame.ENTITY_TYPE.get());
                Disguise disguise = disguiseType.isMob()
                        ? new MobDisguise(disguiseType)
                        : new MiscDisguise(disguiseType);
                DisguiseAPI.disguiseToAll(player, disguise.setViewSelfDisguise(HideNSeekMiniGame.VIEW_DISGUISE.get()));
                DisguiseAPI.setActionBarShown(player, false);
            })
            .build());
    public static final MiniGameTeam SEEKERS = TEAMS.register(() -> new MiniGameTeam.Builder("Seeker")
            .setWeight(0.25)
            .setColor(ChatColor.DARK_RED)
            .setVisibility(Team.OptionStatus.FOR_OWN_TEAM)
            .setObjective("Find and eliminate all hiders.")
            .setCatchPhrase("Something's not right. I can feel it.")
            .setSound(Sound.BLOCK_ANVIL_LAND)
            .isRequired()
            .disableWhileInGrace()
            .prepare(player -> {
                MiniGameUtil.resetPlayer(player);
                MiniGameUtil.giveMiniGameItem(player, Items.SEEKERS_AXE);
                MiniGameUtil.giveMiniGameItem(player, Items.SEEKERS_BOW);
            })
            .build());
}
