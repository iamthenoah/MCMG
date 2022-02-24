package com.than00ber.mcmg.objects;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class GameItem {

    private final Material material;
    private final String name;
    private final List<String> tooltips;
    private final boolean unbreakable;
    private final boolean glowing;
    private final ItemMeta meta;

    // trading data
    private final Predicate<Player> canBuy;
    private final List<GameTeam> blacklisted;
    private final int count;
    private final int cost;

    public GameItem(
            Material material,
            String name,
            List<String> tooltips,
            List<GameTeam> blacklisted,
            int cost,
            boolean unbreakable,
            boolean glowing,
            int count,
            ItemMeta meta,
            Predicate<Player> canBuy
    ) {
        this.material = material;
        this.name = name;
        this.tooltips = tooltips;
        this.blacklisted = blacklisted;
        this.cost = cost;
        this.unbreakable = unbreakable;
        this.glowing = glowing;
        this.count = count;
        this.meta = meta;
        this.canBuy = canBuy;
    }

    public boolean canBuy(Player player) {
        return this.canBuy(player);
    }

    public ItemStack get() {
        ItemStack item = new ItemStack(this.material);
        this.meta.setDisplayName(this.name);
        this.meta.setUnbreakable(this.unbreakable);
        this.meta.setLore(this.tooltips);
        if (this.glowing) {
            item.addUnsafeEnchantment(Enchantment.LOYALTY, 1);
        }
        item.setItemMeta(this.meta);
        return item;
    }

    public static class Builder {

        private final Material material;
        private final List<String> tooltips;
        private final List<GameTeam> blacklisted;
        private String name;
        private int cost;
        private boolean unbreakable;
        private boolean glowing;
        private int count;
        private @Nullable ItemMeta meta;
        private Predicate<Player> canBuy;

        public Builder(Material material) {
            this.material = material;
            this.name = material.name();
            this.tooltips = new ArrayList<>();
            this.blacklisted = new ArrayList<>();
            this.unbreakable = false;
            this.glowing = false;
            this.count = 1;
            this.cost = 1;
        }

        public Builder setName(String name) {
            // custom names are in italic by default
            this.name = ChatColor.RESET + name;
            return this;
        }

        public Builder setCost(int cost) {
            this.cost = cost;
            return this;
        }

        public Builder setBuyStackSize(int count) {
            this.count = count;
            return this;
        }

        public Builder unbreakable() {
            this.unbreakable = true;
            return this;
        }

        public Builder glowing() {
            this.glowing = true;
            return this;
        }

        public Builder addTooltipLine(String line) {
            this.tooltips.add(line);
            return this;
        }

        public Builder addBlackListed(GameTeam team) {
            this.blacklisted.add(team);
            return this;
        }

        public Builder setMeta(Supplier<ItemMeta> supplier) {
            this.meta = supplier.get();
            return this;
        }

        public Builder canBuy(Predicate<Player> canBuy) {
            this.canBuy = canBuy;
            return this;
        }

        public GameItem build() {
            if (this.meta == null) {
                this.meta = new ItemStack(this.material).getItemMeta();
            }
            return new GameItem(
                    this.material,
                    this.name,
                    this.tooltips,
                    this.blacklisted,
                    this.cost,
                    this.unbreakable,
                    this.glowing,
                    this.count,
                    this.meta,
                    this.canBuy
            );
        }
    }
}
