package com.than00ber.mcmg;

import com.than00ber.mcmg.util.ScheduleUtil;
import com.than00ber.mcmg.util.config.ConfigProperty;
import com.than00ber.mcmg.util.config.Configurable;
import com.than00ber.mcmg.util.config.MiniGameProperty;
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
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MiniGameItem implements Configurable {

    private final MiniGameProperty.StringProperty name;
    private final MiniGameProperty.EnumProperty<Material> material;
    private final MiniGameProperty.BooleanProperty unbreakable;
    private final ItemMeta meta;
    private final List<String> tooltips;
    private final @Nullable Action action;

    private MiniGameItem(Material material, String name, List<String> tooltips, boolean unbreakable, ItemMeta meta, @Nullable Action action) {
        this.name = new MiniGameProperty.StringProperty("name", name);
        this.material = new MiniGameProperty.EnumProperty<>("material", Material.class, material);
        this.unbreakable = new MiniGameProperty.BooleanProperty("unbreakable", unbreakable);
        this.meta = meta;
        this.action = action;
        // lore has dark purple color set by default
        this.tooltips = tooltips.stream().map(s -> ChatColor.WHITE + s).toList();
    }

    public String getName() {
        return name.get();
    }

    public @Nullable Action getAction() {
        return action;
    }

    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(material.get());
        meta.setDisplayName(name.get());
        meta.setUnbreakable(unbreakable.get());
        meta.setLore(tooltips);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public List<? extends ConfigProperty<?>> getProperties() {
        List<ConfigProperty<?>> properties = new ArrayList<>();
        if (action != null) properties.addAll(action.getActionProperties());
        properties.addAll(List.of(material, name, unbreakable));
        return properties;
    }

    public static class Builder {

        private String name;
        private final Material material;
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

        public Builder onToggled(int duration, int cooldown, Consumer<PlayerInteractEvent> start) {
            action = new Action(material, duration, cooldown, start, null);
            return this;
        }

        public Builder onToggled(int duration, int cooldown, Consumer<PlayerInteractEvent> start, Consumer<PlayerInteractEvent> end) {
            action = new Action(material, duration, cooldown, start, end);
            return this;
        }

        public Builder onTriggered(int cooldown, Consumer<PlayerInteractEvent> start) {
            action = new Action(material, 0, cooldown, start, null);
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
        private final MiniGameProperty.IntegerProperty duration;
        private final MiniGameProperty.IntegerProperty cooldown;

        public Action(
                Material material,
                int duration,
                int cooldown,
                Consumer<PlayerInteractEvent> onStart,
                @Nullable Consumer<PlayerInteractEvent> onEnd
        ) {
            this.material = material;
            this.duration = new MiniGameProperty.IntegerProperty("action.duration", duration);
            this.cooldown = new MiniGameProperty.IntegerProperty("action.cooldown", cooldown);
            this.onStart = onStart;
            this.onEnd = onEnd;
        }

        public int getDuration() {
            return duration.get();
        }

        public int getCooldown() {
            return cooldown.get();
        }

        public List<ConfigProperty<?>> getActionProperties() {
            return List.of(duration, cooldown);
        }

        public void onClick(PlayerInteractEvent event) {
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
