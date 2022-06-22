package com.than00ber.mcmg;

import com.than00ber.mcmg.util.ChatUtil;
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
    private final @Nullable String message;
    private final List<String> tooltips;
    private final boolean unbreakable;
    private final ItemMeta meta;

    // toggle data
    private final @Nullable Consumer<PlayerInteractEvent> onActionStart;
    private final @Nullable Consumer<PlayerInteractEvent> onActionEnd;
    // providing supplier in case config changes during runtime
    private final @Nullable Supplier<Integer> actionDuration;
    private final @Nullable Supplier<Integer> actionCooldown;

    public MiniGameItem(
            Material material,
            String name,
            @Nullable String message,
            List<String> tooltips,
            boolean unbreakable,
            ItemMeta meta,
            @Nullable Supplier<Integer> actionDuration,
            @Nullable Supplier<Integer> actionCooldown,
            @Nullable Consumer<PlayerInteractEvent> onActionStart,
            @Nullable Consumer<PlayerInteractEvent> onActionEnd)
    {
        this.material = material;
        this.name = name;
        this.message = message;
        this.unbreakable = unbreakable;
        this.meta = meta;
        this.actionDuration = actionDuration;
        this.actionCooldown = actionCooldown;
        this.onActionStart = onActionStart;
        this.onActionEnd = onActionEnd;

        if (onActionStart != null) {
            TOGGLEABLE_ITEMS.put(ChatColor.stripColor(name), this);
        }
        if (actionDuration != null) {
            tooltips.add(ChatColor.GRAY + "Duration: " + ChatColor.YELLOW + actionDuration.get() + "s");
        }
        if (actionCooldown != null) {
            tooltips.add(ChatColor.GRAY + "Cooldown: " + ChatColor.YELLOW + actionCooldown.get() + "s");
        }

        // lore has dark purple color set by default
        this.tooltips = tooltips.stream().map(s -> ChatColor.WHITE + s).toList();
    }

    public void onClick(PlayerInteractEvent event) {
        if (onActionStart != null && actionDuration != null && actionCooldown != null) {
            Player player = event.getPlayer();
            ItemStack item = event.getItem();

            if (!player.hasCooldown(material) && !item.containsEnchantment(Enchantment.LOYALTY)) {
                event.setCancelled(true);
                int delay = actionDuration.get() * 20;
                int cooldown = actionCooldown.get() * 20;

                onActionStart.accept(event);
                item.addUnsafeEnchantment(Enchantment.LOYALTY, 1);

                if (delay != 0) player.playEffect(player.getLocation(), Effect.CLICK1, null);
                if (message != null) ScheduleUtil.doWhile(delay, 5, () -> ChatUtil.toActionBar(player, message));

                ScheduleUtil.doDelayed(delay, () -> {
                    item.removeEnchantment(Enchantment.LOYALTY);
                    player.playEffect(player.getLocation(), Effect.CLICK2, null);
                    player.setCooldown(material, cooldown);

                    if (onActionEnd != null) {
                        onActionEnd.accept(event);
                    }
                });
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
        private String message;
        private final List<String> tooltips;
        private boolean unbreakable;
        private ItemMeta meta;

        // toggle data
        private @Nullable Supplier<Integer> actionDuration;
        private @Nullable Supplier<Integer> actionCooldown;
        private @Nullable Consumer<PlayerInteractEvent> onActionStart;
        private @Nullable Consumer<PlayerInteractEvent> onActionEnd;

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

        public Builder addMessage(String message) {
            this.message = message;
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
            actionDuration = duration;
            actionCooldown = cooldown;
            onActionStart = start;
            return this;
        }

        public Builder onToggled(
                Supplier<Integer> duration,
                Supplier<Integer> cooldown,
                Consumer<PlayerInteractEvent> start,
                Consumer<PlayerInteractEvent> end) {
            actionDuration = duration;
            actionCooldown = cooldown;
            onActionStart = start;
            onActionEnd = end;
            return this;
        }

        public Builder onTrigger(
                Supplier<Integer> cooldown,
                Consumer<PlayerInteractEvent> start) {
            actionDuration = () -> 0;
            actionCooldown = cooldown;
            onActionStart = start;
            return this;
        }

        public MiniGameItem build() {
            return new MiniGameItem(
                    material,
                    name,
                    message,
                    tooltips,
                    unbreakable,
                    meta,
                    actionDuration,
                    actionCooldown,
                    onActionStart,
                    onActionEnd);
        }
    }
}
