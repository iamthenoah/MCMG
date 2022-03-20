package com.than00ber.mcmg.init;

import com.than00ber.mcmg.objects.GameItem;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class GameItems {

    /**
     * Werewolf Items
     */
    public static final GameItem SURVIVORS_WEAPON = new GameItem.Builder(Material.WOODEN_HOE)
            .setName("Survivor's weapon")
            .addTooltip(ChatColor.ITALIC + "Who said you can't trust hoes?")
            .unbreakable()
            .build();
    public static final GameItem SURVIVORS_FOOD = new GameItem.Builder(Material.COOKED_SALMON)
            .setName("Survivor's Fish")
            .setBuyStackSize(5)
            .setCost(1)
            .build();
    public static final GameItem LIQUID_SUGAR_POTION = new GameItem.Builder(Material.POTION)
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
    public static final GameItem RULE_BOOK = new GameItem.Builder(Material.WRITTEN_BOOK)
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
    public static final GameItem HUNTERS_SWORD = new GameItem.Builder(Material.IRON_SWORD)
            .setName(ChatColor.AQUA + "Hunter's Sword")
            .unbreakable()
            .build();
    public static final GameItem HUNTERS_BOW = new GameItem.Builder(Material.BOW)
            .setName(ChatColor.AQUA + "Hunter's Bow")
            .setMeta(() -> {
                ItemMeta meta = new ItemStack(Material.BOW).getItemMeta();
                meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                return meta;
            })
            .unbreakable()
            .build();
    public static final GameItem HUNTERS_ARROWS = new GameItem.Builder(Material.TIPPED_ARROW)
            .setName(ChatColor.AQUA + "Hunter's Bow")
            .build();
    public static final GameItem HUNTERS_COMPASS = new GameItem.Builder(Material.COMPASS)
            .setName(ChatColor.AQUA + "Revelation Compass")
            .setMeta(() -> new ItemStack(Material.COMPASS).getItemMeta()) // TODO - Hunter's Compass
            .addTooltip("Reveals the general direction of the closest props to you for a brief moment.")
            .build();

    /**
     * HideNSeek Items
     */
    public static final GameItem SEEKERS_AXE = new GameItem.Builder(Material.GOLDEN_AXE)
            .setName(ChatColor.YELLOW + "Seeker's Axe")
            .unbreakable()
            .build();
    public static final GameItem SEEKERS_BOW = new GameItem.Builder(Material.BOW)
            .setName(ChatColor.YELLOW + "Seeker's Bow")
            .unbreakable()
            .build();
}
