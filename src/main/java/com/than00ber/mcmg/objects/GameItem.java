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
        return canBuy(player);
    }

    public ItemStack get() {
        ItemStack item = new ItemStack(this.material);
        meta.setDisplayName(this.name);
        meta.setUnbreakable(this.unbreakable);
        meta.setLore(tooltips);
        if (glowing) {
            item.addUnsafeEnchantment(Enchantment.LOYALTY, 1);
        }
        item.setItemMeta(meta);
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
            unbreakable = true;
            return this;
        }

        public Builder glowing() {
            glowing = true;
            return this;
        }

        public Builder addTooltip(String... line) {
            tooltips.addAll(List.of(line));
            return this;
        }

        public Builder addBlackListed(GameTeam team) {
            blacklisted.add(team);
            return this;
        }

        public Builder setMeta(Supplier<ItemMeta> supplier) {
            meta = supplier.get();
            return this;
        }

        public Builder canBuy(Predicate<Player> canBuy) {
            this.canBuy = canBuy;
            return this;
        }

        public GameItem build() {
            if (meta == null) {
                meta = new ItemStack(material).getItemMeta();
            }
            return new GameItem(
                    material,
                    name,
                    tooltips,
                    blacklisted,
                    cost,
                    unbreakable,
                    glowing,
                    count,
                    meta,
                    canBuy
            );
        }
    }
}
