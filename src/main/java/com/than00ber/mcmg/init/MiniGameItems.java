package com.than00ber.mcmg.init;

import com.than00ber.mcmg.MiniGameItem;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class MiniGameItems {

    /**
     * Werewolf Items
     */
    public static final MiniGameItem SURVIVORS_WEAPON = new MiniGameItem.Builder(Material.WOODEN_HOE)
            .setName("Survivor's weapon")
            .addTooltip(ChatColor.ITALIC + "Who said you can't trust hoes?")
            .unbreakable()
            .build();
    public static final MiniGameItem SURVIVORS_FOOD = new MiniGameItem.Builder(Material.COOKED_SALMON)
            .setName("Survivor's Fish")
            .setBuyStackSize(5)
            .setCost(1)
            .build();
    public static final MiniGameItem LIQUID_SUGAR_POTION = new MiniGameItem.Builder(Material.POTION)
            .setName("Liquid Sugar")
            .addTooltip(
                    "Gives a 2s speed boost.",
                    "Triggers a very short, but really fast sugar rush!"
            )
            .setCost(1)
            .setMeta(() -> {
                PotionMeta meta = (PotionMeta) new ItemStack(Material.POTION).getItemMeta();
                meta.setColor(Color.WHITE);
                PotionEffect effect = new PotionEffect(PotionEffectType.SPEED, 40, 10);
                meta.addCustomEffect(effect, false);
                return meta;
            }).build();
    public static final MiniGameItem RULE_BOOK = new MiniGameItem.Builder(Material.WRITTEN_BOOK)
            .setName(ChatColor.YELLOW + "WWRPG Rule Book")
            .setMeta(() -> {
                List<String> pages = new ArrayList<>();
                pages.add("This is a line");
                pages.add("This is another line");
                BookMeta meta = (BookMeta) new ItemStack(Material.WRITTEN_BOOK).getItemMeta();
                meta.setTitle(ChatColor.YELLOW + "WWRPG Rule Book");
                meta.setAuthor("The WWRPG Plugin");
                meta.setPages(pages);
                return meta;
            }).build();

    /**
     * PropHunt Items
     */
    public static final MiniGameItem HUNTERS_SWORD = new MiniGameItem.Builder(Material.IRON_SWORD)
            .setName(ChatColor.AQUA + "Hunter's Sword")
            .unbreakable()
            .build();
    public static final MiniGameItem HUNTERS_BOW = new MiniGameItem.Builder(Material.BOW)
            .setName(ChatColor.AQUA + "Hunter's Bow")
            .unbreakable()
            .build();
    public static final MiniGameItem HUNTERS_ARROWS = new MiniGameItem.Builder(Material.ARROW)
            .setName(ChatColor.AQUA + "Hunter's Arrow")
            .build();
    public static final MiniGameItem HUNTERS_COMPASS = new MiniGameItem.Builder(Material.COMPASS)
            .setName(ChatColor.AQUA + "Revelation Compass")
            .setMeta(() -> {
                CompassMeta meta = (CompassMeta) new ItemStack(Material.COMPASS).getItemMeta();
                meta.setLodestone(null);
                meta.setLodestoneTracked(false);
                return meta;
            })
            .addTooltip(
                    "Reveals the general direction of the closest",
                    "props to you for a brief moment."
            )
            .build();

    /**
     * HideNSeek Items
     */
    public static final MiniGameItem SEEKERS_AXE = new MiniGameItem.Builder(Material.GOLDEN_AXE)
            .setName(ChatColor.YELLOW + "Seeker's Axe")
            .unbreakable()
            .build();
    public static final MiniGameItem SEEKERS_BOW = new MiniGameItem.Builder(Material.BOW)
            .setName(ChatColor.YELLOW + "Seeker's Bow")
            .unbreakable()
            .build();
}
