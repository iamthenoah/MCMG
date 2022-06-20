package com.than00ber.mcmg;

import com.than00ber.mcmg.util.config.Configurable;
import com.than00ber.mcmg.util.config.MiniGameProperty;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static org.bukkit.scoreboard.Team.OptionStatus;

public class MiniGameTeam implements Configurable {

    private final MiniGameProperty.StringProperty teamId;
    private final MiniGameProperty.StringProperty displayName;
    private final MiniGameProperty.DoubleProperty weight;
    private final MiniGameProperty.IntegerProperty threshold;
    private final MiniGameProperty.ChatColorProperty color;
    private final MiniGameProperty.EnumProperty<OptionStatus> visibility;
    private final MiniGameProperty.StringProperty catchPhrase;
    private final MiniGameProperty.StringProperty objective;
    private final MiniGameProperty.EnumProperty<Sound> sound;
    private final MiniGameProperty.BooleanProperty isSpectator;
    private final MiniGameProperty.BooleanProperty isRequired;
    private final MiniGameProperty.BooleanProperty disableWhileGrace;
    private final Consumer<Player> preparePlayer;

    public MiniGameTeam(
            String teamId,
            String displayName,
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
        this.teamId = new MiniGameProperty.StringProperty(teamId + ".id", teamId);
        this.displayName = new MiniGameProperty.StringProperty(teamId + ".name", displayName);
        this.weight = new MiniGameProperty.DoubleProperty(teamId + ".weight", weight);
        this.threshold = new MiniGameProperty.IntegerProperty(teamId + ".threshold", threshold);
        this.color = new MiniGameProperty.ChatColorProperty(teamId + ".color", color);
        this.visibility = new MiniGameProperty.EnumProperty<>(teamId + ".visibility", OptionStatus.class, visibility);
        this.catchPhrase = new MiniGameProperty.StringProperty(teamId + ".phrase", catchPhrase);
        this.objective = new MiniGameProperty.StringProperty(teamId + ".objective", objective);
        this.sound = new MiniGameProperty.EnumProperty<>(teamId + ".sound", Sound.class, sound);
        this.isSpectator = new MiniGameProperty.BooleanProperty(teamId + ".spectator", isSpectator);
        this.isRequired = new MiniGameProperty.BooleanProperty(teamId + ".required", isRequired);
        this.disableWhileGrace = new MiniGameProperty.BooleanProperty(teamId + ".disableWhileGrace", disableWhileGrace);
        this.preparePlayer = preparePlayer;
    }

    public String getTeamId() {
        return teamId.get();
    }

    public String getDisplayName() {
        return displayName.get();
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
    public String getConfigName() {
        return getTeamId();
    }

    @Override
    public List<MiniGameProperty<?>> getProperties() {
        return Arrays.asList(
                teamId,
                displayName,
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

        private final String teamId;
        private String displayName;
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

        public Builder(String id) {
            teamId = id;
            displayName = teamId;
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
            displayName = name;
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
                    teamId,
                    displayName,
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
