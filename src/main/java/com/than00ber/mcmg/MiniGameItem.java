package com.than00ber.mcmg;

import com.than00ber.mcmg.util.ScheduleUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MiniGameItem {

    public static final Map<String, MiniGameItem> TOGGLEABLE_ITEMS = new HashMap<>();

    private final Material material;
    private final String name;
    private final List<String> tooltips;
    private final boolean unbreakable;
    private final ItemMeta meta;

    // toggle data
    private final @Nullable Supplier<Integer> toggleDuration;
    private final @Nullable Supplier<Integer> toggleCooldown;
    private final @Nullable Consumer<PlayerInteractEvent> toggleConsumer;

    public MiniGameItem(
            Material material,
            String name,
            List<String> tooltips,
            boolean unbreakable,
            ItemMeta meta,
            @Nullable Supplier<Integer> toggleDuration,
            @Nullable Supplier<Integer> toggleCooldown,
            @Nullable Consumer<PlayerInteractEvent> toggleConsumer
    ) {
        this.material = material;
        this.name = name;
        this.unbreakable = unbreakable;
        this.meta = meta;

        this.toggleDuration = toggleDuration;
        this.toggleCooldown = toggleCooldown;
        this.toggleConsumer = toggleConsumer;

        if (toggleConsumer != null && toggleDuration != null && toggleCooldown != null) {
            TOGGLEABLE_ITEMS.put(ChatColor.stripColor(name), this);
            tooltips.add(ChatColor.GRAY + "Duration: " + ChatColor.YELLOW + toggleDuration.get() + "s");
            tooltips.add(ChatColor.GRAY + "Cooldown: " + ChatColor.YELLOW + toggleCooldown.get() + "s");
        }

        // lore has dark purple color set by default
        this.tooltips = tooltips.stream().map(s -> ChatColor.WHITE + s).toList();
    }

    public void onClick(PlayerInteractEvent event) {
        if (toggleConsumer != null && toggleDuration != null && toggleCooldown != null) {
            Player player = event.getPlayer();

            if (!player.hasCooldown(material)) {
                int delay = toggleDuration.get() * 20;
                int cooldown = toggleCooldown.get() * 20;
                toggleConsumer.accept(event);
                ScheduleUtil.doDelayed(delay, () -> player.setCooldown(material, cooldown));
            }
        }
    }

    public ItemStack get() {
        ItemStack item = new ItemStack(material);
        meta.setLore(tooltips);
        meta.setDisplayName(name);
        meta.setUnbreakable(unbreakable);
        item.setItemMeta(meta);
        return item;
    }

    public static class Builder {

        private final Material material;
        private String name;
        private final List<String> tooltips;
        private boolean unbreakable;
        private ItemMeta meta;

        // toggle data
        private @Nullable Supplier<Integer> toggleDuration;
        private @Nullable Supplier<Integer> toggleCooldown;
        private @Nullable Consumer<PlayerInteractEvent> toggleConsumer;

        public Builder(Material material) {
            this.material = material;
            name = material.name();
            tooltips = new ArrayList<>();
            unbreakable = false;
        }

        public Builder setName(String name) {
            // custom names are in italic by default
            this.name = ChatColor.RESET + name;
            return this;
        }

        public Builder unbreakable() {
            unbreakable = true;
            return this;
        }

        public Builder addTooltip(String... line) {
            tooltips.addAll(List.of(line));
            return this;
        }

        public Builder setMeta(Supplier<ItemMeta> supplier) {
            meta = supplier.get();
            return this;
        }

        public Builder onToggled(
                Supplier<Integer> duration,
                Supplier<Integer> cooldown,
                Consumer<PlayerInteractEvent> consumer
        ) {
            // providing supplier in case config changes during runtime
            toggleDuration = duration;
            toggleCooldown = cooldown;
            toggleConsumer = consumer;
            return this;
        }

        public MiniGameItem build() {
            if (meta == null) meta = new ItemStack(material).getItemMeta();
            return new MiniGameItem(
                    material,
                    name,
                    tooltips,
                    unbreakable,
                    meta,
                    toggleDuration,
                    toggleCooldown,
                    toggleConsumer
            );
        }
    }
}
