package com.than00ber.mcmg;

import com.than00ber.mcmg.util.ScheduleUtil;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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
    private final @Nullable Action action;

    public MiniGameItem(Material material, String name, List<String> tooltips, boolean unbreakable, ItemMeta meta, @Nullable Action action) {
        this.material = material;
        this.name = name;
        this.unbreakable = unbreakable;
        this.meta = meta;
        this.action = action;

        if (action != null) {
            TOGGLEABLE_ITEMS.put(ChatColor.stripColor(name), this);
        }

        // lore has dark purple color set by default
        this.tooltips = tooltips.stream().map(s -> ChatColor.WHITE + s).toList();
    }

    public @Nullable Action getAction() {
        return action;
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
        private @Nullable Action action;

        public Builder(Material material) {
            this.material = material;
            meta = new ItemStack(material).getItemMeta();
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
                Consumer<PlayerInteractEvent> start) {
            action = new Action(material, duration, cooldown, start, null);
            return this;
        }

        public Builder onToggled(
                Supplier<Integer> duration,
                Supplier<Integer> cooldown,
                Consumer<PlayerInteractEvent> start,
                Consumer<PlayerInteractEvent> end) {
            action = new Action(material, duration, cooldown, start, end);
            return this;
        }

        public Builder onTrigger(
                Supplier<Integer> cooldown,
                Consumer<PlayerInteractEvent> start) {
            action = new Action(material, () -> 0, cooldown, start, null);
            return this;
        }

        public MiniGameItem build() {
            if (action != null) {
                String cooldown = ChatColor.YELLOW.toString() + action.getCooldown() + "s";
                String duration = action.getDuration() > 0
                        ? ChatColor.YELLOW.toString() + action.getDuration() + "s"
                        : ChatColor.DARK_GRAY + "instant";
                tooltips.add(ChatColor.GRAY + "  Duration: " + duration);
                tooltips.add(ChatColor.GRAY + "  Cooldown: " + cooldown);
            }
            return new MiniGameItem(material, name, tooltips, unbreakable, meta, action);
        }
    }

    public static class Action {

        private final Material material;
        private final Consumer<PlayerInteractEvent> onStart;
        private final @Nullable Consumer<PlayerInteractEvent> onEnd;
        // providing supplier in case config changes during runtime
        private final Supplier<Integer> duration;
        private final Supplier<Integer> cooldown;

        public Action(
                Material material,
                Supplier<Integer> duration,
                Supplier<Integer> cooldown,
                Consumer<PlayerInteractEvent> onStart,
                @Nullable Consumer<PlayerInteractEvent> onEnd
        ) {
            this.material = material;
            this.duration = duration;
            this.cooldown = cooldown;
            this.onStart = onStart;
            this.onEnd = onEnd;
        }

        public int getDuration() {
            return duration.get();
        }

        public int getCooldown() {
            return cooldown.get();
        }

        public void onClick(PlayerInteractEvent event) {
            if (onStart != null && duration != null && cooldown != null) {
                Player player = event.getPlayer();
                ItemStack item = event.getItem();

                if (!player.hasCooldown(material) && !item.containsEnchantment(Enchantment.LOYALTY)) {
                    event.setCancelled(true);
                    int delay = getDuration() * 20;
                    int cooldown = getCooldown() * 20;

                    onStart.accept(event);
                    item.addUnsafeEnchantment(Enchantment.LOYALTY, 1);

                    if (delay != 0) player.playEffect(player.getLocation(), Effect.CLICK1, null);

                    ScheduleUtil.doDelayed(delay, () -> {
                        if (onEnd != null) onEnd.accept(event);
                        item.removeEnchantment(Enchantment.LOYALTY);
                        player.playEffect(player.getLocation(), Effect.CLICK2, null);
                        player.setCooldown(material, cooldown);
                    });
                }
            }
        }
    }
}
