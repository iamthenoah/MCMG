package com.than00ber.mcmg.core.configuration;

import com.than00ber.mcmg.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@SuppressWarnings({"unchecked", "unused"})
public class ConfigurableProperty<V> extends Property<V> {

    private final BiFunction<Player, String[], V> parser;
    private Predicate<V> validator;

    private ConfigurableProperty(String name, V defaultValue, BiFunction<Player, String[], V> parser, Predicate<V> validator) {
        super(name, defaultValue);
        this.parser = parser;
        this.validator = validator;
    }

    public <P extends ConfigurableProperty<V>> P verify(Predicate<V> validator) {
        this.validator = validator;
        return (P) this;
    }

    public boolean setIfValid(Player player, String[] args) {
        V value = parser.apply(player, args);
        boolean isValid = validator.test(value);
        if (isValid) set(value);
        return isValid;
    }

    public static final class BooleanProperty extends ConfigurableProperty<Boolean> {

        public BooleanProperty(String name, Boolean defaultValue) {
            super(name, defaultValue, (p, a) -> Boolean.valueOf(a[0]), b -> true);
        }

        public BooleanProperty(String name) {
            this(name, false);
        }
    }

    public static final class StringProperty extends ConfigurableProperty<String> {

        public StringProperty(String name, String defaultValue) {
            super(name, defaultValue, (p, a) -> a[0], s -> true);
        }

        public StringProperty(String name) {
            this(name, null);
        }
    }

    public static final class IntegerProperty extends ConfigurableProperty<Integer> {

        public IntegerProperty(String name, Integer defaultValue) {
            super(name, defaultValue, (p, a) -> Integer.parseInt(a[0]), i -> true);
        }

        public IntegerProperty(String name) {
            this(name, 0);
        }
    }

    public static final class DoubleProperty extends ConfigurableProperty<Double> {

        public DoubleProperty(String name, Double defaultValue) {
            super(name, defaultValue, (p, a) -> Double.parseDouble(a[0]), d -> true);
        }

        public DoubleProperty(String name) {
            this(name, 0.0);
        }
    }

    public static class EnumProperty<E extends Enum<E>> extends ConfigurableProperty<E> {

        private final Class<E> enumClass;

        private EnumProperty(String name, E defaultValue, BiFunction<Player, String[], E> parser, Predicate<E> validator, Class<E> enumClass) {
            super(name, defaultValue, parser, validator);
            this.enumClass = enumClass;
        }

        public EnumProperty(String name, Class<E> enumClass, E defaultValue) {
            this(name, defaultValue, (p, a) -> safeValueOf(enumClass, a[0]), Objects::nonNull, enumClass);
        }

        public EnumProperty(String name, Class<E> enumClass) {
            this(name, null, (p, a) -> safeValueOf(enumClass, a[0]), Objects::nonNull, enumClass);
        }

        @Override
        public void load(ConfigurationSection configs) {
            Object obj = configs.get(getName());
            if (obj != null) set(E.valueOf(enumClass, (String) obj));
        }

        @Override
        public void save(ConfigurationSection configs) {
            if (get() != null) configs.set(getName(), get().toString());
        }
    }

    public static class LocationProperty extends ConfigurableProperty<Location> {

        public LocationProperty(String name, Location defaultValue) {
            super(name, defaultValue, LocationProperty::toLocation, l -> true);
        }

        public LocationProperty(String name) {
            this(name, null);
        }

        private static Location toLocation(Player player, String[] args) {
            Location location = player.getLocation();
            if (args.length != 0) {
                location.setX(Double.parseDouble(args[0]));
                location.setY(Double.parseDouble(args[1]));
                location.setZ(Double.parseDouble(args[2]));
            } else {
                location.setX(location.getBlockX());
                location.setY(location.getBlockY());
                location.setZ(location.getBlockZ());
            }
            return location;
        }
    }

    public static class ChatColorProperty extends ConfigurableProperty<ChatColor> {

        private ChatColorProperty(String name, ChatColor defaultValue, BiFunction<Player, String[], ChatColor> parser, Predicate<ChatColor> validator) {
            super(name, defaultValue, parser, validator);
        }

        public ChatColorProperty(String name, ChatColor defaultValue) {
            this(name, defaultValue, ChatColorProperty::toChatColor, Objects::nonNull);
        }

        @Override
        public void load(ConfigurationSection configs) {
            Object obj = configs.get(getName());
            if (obj != null) set(ChatColor.getByChar((String) obj));
        }

        @Override
        public void save(ConfigurationSection configs) {
            configs.set(getName(), get().getChar());
        }

        @Override
        public String toString() {
            return getName() + ":" + get().getChar();
        }

        private static ChatColor toChatColor(Player player, String[] args) {
            return safeValueOf(ChatColor.class, args[0]);
        }
    }

    public static <E extends Enum<E>> @Nullable E safeValueOf(Class<E> enumClass, String string) {
        try {
            return E.valueOf(enumClass, string);
        } catch (Exception e) {
            return null;
        }
    }

    public static final class CheckIf {
        public static final Predicate<Integer> POSITIVE_INT = i -> i >= 0;
        public static final Predicate<Integer> NEGATIVE_INT = i -> i <= 0;
        public static final Predicate<Double> POSITIVE_DOUBLE = i -> i >= 0;
        public static final Predicate<Double> NEGATIVE_DOUBLE = i -> i <= 0;
        public static final Predicate<Integer> LESS_THAN_A_DAY = i -> i > 0 && i <= 86400;
        public static final Predicate<Integer> LESS_THEN_PLAYERS = i -> i > 0 && i <= Main.WORLD.getPlayers().size();
    }
}
