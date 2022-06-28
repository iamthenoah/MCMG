package com.than00ber.mcmg.core;

import com.google.common.collect.ImmutableList;
import com.than00ber.mcmg.core.configuration.Configurable;
import com.than00ber.mcmg.core.configuration.ConfigurableProperty;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

import static org.bukkit.scoreboard.Team.OptionStatus;

public class MiniGameTeam implements Configurable {

    private final ConfigurableProperty.StringProperty name;
    private final ConfigurableProperty.StringProperty visibleName;
    private final ConfigurableProperty.DoubleProperty weight;
    private final ConfigurableProperty.IntegerProperty threshold;
    private final ConfigurableProperty.ChatColorProperty color;
    private final ConfigurableProperty.EnumProperty<OptionStatus> visibility;
    private final ConfigurableProperty.StringProperty catchPhrase;
    private final ConfigurableProperty.StringProperty objective;
    private final ConfigurableProperty.EnumProperty<Sound> sound;
    private final ConfigurableProperty.BooleanProperty isSpectator;
    private final ConfigurableProperty.BooleanProperty isRequired;
    private final ConfigurableProperty.BooleanProperty disableWhileGrace;
    private final Consumer<Player> preparePlayer;

    private MiniGameTeam(
            String name,
            String visibleName,
            double weight,
            int threshold,
            ChatColor color,
            OptionStatus visibility,
            String catchPhrase,
            String objective,
            Sound sound,
            boolean isSpectator,
            boolean isRequired,
            boolean disableWhileGrace,
            Consumer<Player> preparePlayer
    ) {
        this.name = new ConfigurableProperty.StringProperty("name", name);
        this.visibleName = new ConfigurableProperty.StringProperty("visibleName", visibleName);
        this.weight = new ConfigurableProperty.DoubleProperty("weight", weight);
        this.threshold = new ConfigurableProperty.IntegerProperty("threshold", threshold);
        this.color = new ConfigurableProperty.ChatColorProperty("color", color);
        this.visibility = new ConfigurableProperty.EnumProperty<>("visibility", OptionStatus.class, visibility);
        this.catchPhrase = new ConfigurableProperty.StringProperty("phrase", catchPhrase);
        this.objective = new ConfigurableProperty.StringProperty("objective", objective);
        this.sound = new ConfigurableProperty.EnumProperty<>("sound", Sound.class, sound);
        this.isSpectator = new ConfigurableProperty.BooleanProperty("spectator", isSpectator);
        this.isRequired = new ConfigurableProperty.BooleanProperty("required", isRequired);
        this.disableWhileGrace = new ConfigurableProperty.BooleanProperty("disableWhileGrace", disableWhileGrace);
        this.preparePlayer = preparePlayer;
    }

    @Override
    public String getName() {
        return name.get();
    }

    public String getVisibleName() {
        return visibleName.get();
    }

    public Double getWeight() {
        return weight.get();
    }

    public Integer getThreshold() {
        return threshold.get();
    }

    public ChatColor getColor() {
        return color.get();
    }

    public OptionStatus getVisibility() {
        return visibility.get();
    }

    public String getCatchPhrase() {
        return catchPhrase.get();
    }

    public String getObjective() {
        return objective.get();
    }

    public Sound getSound() {
        return sound.get();
    }

    public boolean isSpectator() {
        return isSpectator.get();
    }

    public boolean isRequired() {
        return isRequired.get();
    }

    public boolean disableWhileGrace() {
        return disableWhileGrace.get();
    }

    public void prepare(Player player) {
        preparePlayer.accept(player);
    }

    @Override
    public ImmutableList<ConfigurableProperty<?>> getProperties() {
        return ImmutableList.of(
                visibleName,
                weight,
                threshold,
                color,
                visibility,
                catchPhrase,
                objective,
                sound,
                isSpectator,
                isRequired,
                disableWhileGrace
        );
    }

    public static class Builder {

        private final String name;
        private String visibleName;
        private double weight;
        private int threshold;
        private ChatColor color;
        private OptionStatus visibility;
        private String catchPhrase;
        private String objective;
        private Sound sound;
        private boolean isSpectator;
        private boolean isRequired;
        private boolean disableWhileGrace;
        Consumer<Player> preparePlayer;

        public Builder(String name) {
            this.name = name;
            visibleName = name;
            weight = 0;
            threshold = 0;
            color = ChatColor.WHITE;
            visibility = OptionStatus.FOR_OWN_TEAM;
            isSpectator = false;
            isRequired = false;
            disableWhileGrace = false;
            preparePlayer = p -> {};
        }

        public Builder setDisplayName(String name) {
            visibleName = name;
            return this;
        }

        public Builder setWeight(double weight) {
            this.weight = weight;
            return this;
        }

        public Builder setThreshold(int threshold) {
            this.threshold = threshold;
            return this;
        }

        public Builder setColor(ChatColor color) {
            this.color = color;
            return this;
        }

        public Builder setVisibility(OptionStatus visibility) {
            this.visibility = visibility;
            return this;
        }

        public Builder setCatchPhrase(String phrase) {
            catchPhrase = phrase;
            return this;
        }

        public Builder setObjective(String objective) {
            this.objective = objective;
            return this;
        }

        public Builder setSound(Sound sound) {
            this.sound = sound;
            return this;
        }

        public Builder setSpectator() {
            isSpectator = true;
            return this;
        }

        public Builder isRequired() {
            isRequired = true;
            return this;
        }

        public Builder disableWhileInGrace() {
            disableWhileGrace = true;
            return this;
        }

        public Builder prepare(Consumer<Player> consumer) {
            preparePlayer = consumer;
            return this;
        }

        public MiniGameTeam build() {
            return new MiniGameTeam(
                    name,
                    visibleName,
                    weight,
                    threshold,
                    color,
                    visibility,
                    catchPhrase,
                    objective,
                    sound,
                    isSpectator,
                    isRequired,
                    disableWhileGrace,
                    preparePlayer
            );
        }
    }
}
