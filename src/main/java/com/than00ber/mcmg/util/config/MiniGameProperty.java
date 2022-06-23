package com.than00ber.mcmg.util.config;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class MiniGameProperty<V> extends ConfigProperty<V> {

    private final BiFunction<Player, String[], V> parser;
    private Predicate<V> validator;

    public MiniGameProperty(String name, V defaultValue, BiFunction<Player, String[], V> parser, Predicate<V> validator) {
        super(name, defaultValue);
        this.parser = parser;
        this.validator = validator;
    }

    public MiniGameProperty(String name, V defaultValue) {
        this(name, defaultValue, (p, a) -> (V) a[0], b -> true);
    }

    public MiniGameProperty(String name) {
        this(name, null);
    }

    public <P extends MiniGameProperty<V>> P validate(Predicate<V> predicate) {
        validator = predicate;
        return (P) this;
    }

    public V parse(Player player, String[] args) {
        return set(parser.apply(player, args));
    }

    public boolean isValidValue(Object value) {
        return validator.test((V) value);
    }

    /**
     * BooleanProperty
     */
    public static class BooleanProperty extends MiniGameProperty<Boolean> {

        public BooleanProperty(String name, Boolean defaultValue) {
            super(name, defaultValue, (p, a) -> Boolean.valueOf(a[0]), b -> true);
        }

        public BooleanProperty(String name) {
            this(name, false);
        }
    }

    /**
     * StringProperty
     */
    public static class StringProperty extends MiniGameProperty<String> {

        public StringProperty(String name, String defaultValue) {
            super(name, defaultValue, (p, a) -> a[0], s -> true);
        }

        public StringProperty(String name) {
            this(name, null);
        }
    }

    /**
     * IntegerProperty
     */
    public static class IntegerProperty extends MiniGameProperty<Integer> {

        public static final Predicate<Integer> POSITIVE = i -> i >= 0;
        public static final Predicate<Integer> NEGATIVE = i -> i <= 0;

        public IntegerProperty(String name, Integer defaultValue) {
            super(name, defaultValue, (p, a) -> Integer.parseInt(a[0]), i -> true);
        }

        public IntegerProperty(String name) {
            this(name, 1);
        }
    }

    /**
     * DoubleProperty
     */
    public static class DoubleProperty extends MiniGameProperty<Double> {

        public DoubleProperty(String name, Double defaultValue) {
            super(name, defaultValue, (p, a) -> Double.parseDouble(a[0]), d -> true);
        }

        public DoubleProperty(String name) {
            this(name, 0.0);
        }
    }

    /**
     * EnumProperty
     */
    public static class EnumProperty<E extends Enum<E>> extends MiniGameProperty<E> {

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
        public void load(YamlConfiguration configs) {
            Object obj = configs.get(getPath());
            if (obj != null) {
                set(E.valueOf(enumClass, (String) obj));
            }
        }

        @Override
        public void save(YamlConfiguration configs) {
            if (get() != null) {
                configs.set(getPath(), get().toString());
            }
        }
    }

    /**
     * LocationProperty
     */
    public static class LocationProperty extends MiniGameProperty<Location> {

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

    /**
     * ChatColorProperty
     */
    public static class ChatColorProperty extends MiniGameProperty<ChatColor> {

        private ChatColorProperty(String name, ChatColor defaultValue, BiFunction<Player, String[], ChatColor> parser, Predicate<ChatColor> validator) {
            super(name, defaultValue, parser, validator);
        }

        public ChatColorProperty(String name, ChatColor defaultValue) {
            this(name, defaultValue, ChatColorProperty::toChatColor, Objects::nonNull);
        }

        @Override
        public void load(YamlConfiguration configs) {
            Object obj = configs.get(getPath());
            if (obj != null) {
                set(ChatColor.getByChar((String) obj));
            }
        }

        @Override
        public void save(YamlConfiguration configs) {
            configs.set(getPath(), get().getChar());
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

    public static <E extends Enum<E>> E safeValueOf(Class<E> enumClass, String string, E defaultValue) {
        return Optional.ofNullable(safeValueOf(enumClass, string)).orElse(defaultValue);
    }
}
