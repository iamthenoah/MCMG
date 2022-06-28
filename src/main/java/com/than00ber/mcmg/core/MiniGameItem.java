package com.than00ber.mcmg.core;

import com.google.common.collect.ImmutableList;
import com.than00ber.mcmg.core.configuration.Configurable;
import com.than00ber.mcmg.core.configuration.ConfigurableProperty;
import com.than00ber.mcmg.util.ScheduleUtil;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MiniGameItem implements Configurable {

    private final ConfigurableProperty.StringProperty name;
    private final ConfigurableProperty.ChatColorProperty color;
    private final ConfigurableProperty.EnumProperty<Material> material;
    private final ConfigurableProperty.BooleanProperty unbreakable;
    private final ConfigurableProperty.IntegerProperty startingCooldown;
    private final ConfigurableProperty.IntegerProperty duration;
    private final ConfigurableProperty.IntegerProperty cooldown;
    private final ConfigurableProperty.IntegerProperty range;
    private final ItemMeta meta;
    private final List<String> tooltips;
    private final Function<Action, ActionResult> onStart;
    private final Consumer<Action> onFinish;

    private MiniGameItem(
            Material material,
            String name,
            ChatColor color,
            List<String> tooltips,
            boolean unbreakable,
            int startingCooldown,
            ItemMeta meta,
            int duration,
            int cooldown,
            int range,
            Function<Action, ActionResult> onStart,
            Consumer<Action> onFinish
    ) {
        this.name = new ConfigurableProperty.StringProperty("name", name);
        this.material = new ConfigurableProperty.EnumProperty<>("material", Material.class, material);
        this.color = new ConfigurableProperty.ChatColorProperty("color", color);
        this.unbreakable = new ConfigurableProperty.BooleanProperty("unbreakable", unbreakable);
        this.startingCooldown = new ConfigurableProperty.IntegerProperty("starting.cooldown", startingCooldown);
        this.duration = new ConfigurableProperty.IntegerProperty("action.duration", duration);
        this.cooldown = new ConfigurableProperty.IntegerProperty("action.cooldown", cooldown);
        this.range = new ConfigurableProperty.IntegerProperty("action.range", range);
        this.meta = meta;
        this.onStart = onStart;
        this.onFinish = onFinish;

        if (range > 0) tooltips.add(ChatColor.GRAY + "  Range: " + ChatColor.YELLOW + range + "b");
        if (duration > 0) tooltips.add(ChatColor.GRAY + "  Duration: " + ChatColor.YELLOW + duration + "s");
        if (cooldown > 0) tooltips.add(ChatColor.GRAY + "  Cooldown: " + ChatColor.YELLOW + cooldown + "s");

        // lore has dark purple color set by default
        this.tooltips = tooltips.stream().map(s -> ChatColor.WHITE + s).toList();
    }

    @Override
    public String getName() {
        return name.get();
    }

    public int getStartingCooldown() {
        return startingCooldown.get() * 20;
    }

    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(material.get());
        meta.setDisplayName(ChatColor.RESET + color.get().toString() + name.get());
        meta.setUnbreakable(unbreakable.get());
        meta.setLore(tooltips);
        item.setItemMeta(meta);
        return item;
    }

    public ActionResult onClick(PlayerInteractEvent event) {
        Action action = new Action(event, duration.get(), cooldown.get(), range.get());
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (!player.hasCooldown(material.get()) && !item.containsEnchantment(Enchantment.LOYALTY)) {
            event.setCancelled(true);
            ActionResult result = onStart.apply(action);

            if (result.isSuccessful()) {
                if (duration.get() != 0) player.playEffect(player.getLocation(), Effect.CLICK1, null);
                item.addUnsafeEnchantment(Enchantment.LOYALTY, 1);
                ScheduleUtil.doDelayed(duration.get() * 20, () -> {
                    onFinish.accept(action);
                    item.removeEnchantment(Enchantment.LOYALTY);
                    player.playEffect(player.getLocation(), Effect.CLICK2, null);
                    player.setCooldown(material.get(), cooldown.get() * 20);
                });
            }
            return result;
        }
        return ActionResult.success();
    }

    @Override
    public ImmutableList<ConfigurableProperty<?>> getProperties() {
        List<ConfigurableProperty<?>> properties = new ArrayList<>();
        properties.add(name);
        properties.add(color);
        properties.add(material);
        properties.add(unbreakable);
        properties.add(startingCooldown);
        if (duration.get() > 0) properties.add(duration);
        if (cooldown.get() > 0) properties.add(cooldown);
        if (range.get() > 0) properties.add(range);
        return ImmutableList.copyOf(properties);
    }

    public static class Builder {

        private String name;
        private ChatColor color;
        private final Material material;
        private List<String> tooltips;
        private boolean unbreakable;
        private int startingCooldown;
        private ItemMeta meta;
        private int duration;
        private int cooldown;
        private int range;
        private Function<Action, ActionResult> onStart;
        private Consumer<Action> onFinish;

        public Builder(Material material) {
            this.material = material;
            name = material.name();
            color = ChatColor.WHITE;
            tooltips = new ArrayList<>();
            unbreakable = false;
            startingCooldown = 0;
            meta = new ItemStack(material).getItemMeta();
            duration = 0;
            cooldown = 0;
            range = 0;
            onStart = a -> ActionResult.success();
            onFinish = a -> {};
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setColor(ChatColor color) {
            this.color = color;
            return this;
        }

        public Builder unbreakable() {
            unbreakable = true;
            return this;
        }

        public Builder setStartingCooldown(int cooldown) {
            startingCooldown = cooldown;
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

        public Builder onTrigger(int cooldown, Function<Action, ActionResult> onStart) {
            return onTrigger(cooldown, 0, onStart);
        }

        public Builder onTrigger(int cooldown, int range, Function<Action, ActionResult> start) {
            return onToggle(0, cooldown, range, start, onFinish);
        }

        public Builder onToggle(int duration, int cooldown, Function<Action, ActionResult> onStart) {
            return onToggle(duration, cooldown, 0, onStart);
        }

        public Builder onToggle(int duration, int cooldown, int range, Function<Action, ActionResult> onStart) {
            return onToggle(duration, cooldown, range, onStart, onFinish);
        }

        public Builder onToggle(int duration, int cooldown, int range, Function<Action, ActionResult> onStart, Consumer<Action> onFinish) {
            this.duration = duration;
            this.cooldown = cooldown;
            this.range = range;
            this.onStart = onStart;
            this.onFinish = onFinish;
            return this;
        }

        public MiniGameItem build() {
            return new MiniGameItem(
                    material,
                    name,
                    color,
                    tooltips,
                    unbreakable,
                    startingCooldown,
                    meta,
                    duration,
                    cooldown,
                    range,
                    onStart,
                    onFinish
            );
        }
    }

    @SuppressWarnings("unused")
    public static class Action {

        private final PlayerInteractEvent event;
        private final int duration;
        private final int cooldown;
        private final int range;

        public Action(PlayerInteractEvent event, int duration, int cooldown, int range) {
            this.event = event;
            this.duration = duration;
            this.cooldown = cooldown;
            this.range = range;
        }

        public PlayerInteractEvent getEvent() {
            return event;
        }

        public int getDuration() {
            return duration;
        }

        public int getCooldown() {
            return cooldown;
        }

        public int getRange() {
            return range;
        }
    }
}
