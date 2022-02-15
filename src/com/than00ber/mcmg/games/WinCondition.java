package com.than00ber.mcmg.games;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WinCondition<G> {

    private final Predicate<G> condition;
    private final List<GameTeam> winners;
    private final @Nullable String winReason;
    private final @Nullable String loseReason;
    public final @Nullable String cancellationReason;

    public WinCondition(
            Predicate<G> condition,
            List<GameTeam> winners,
            @Nullable String winReason,
            @Nullable String loseReason,
            @Nullable String cancellationReason
    ) {
        this.condition = condition;
        this.winners = winners;
        this.winReason = winReason;
        this.loseReason = loseReason;
        this.cancellationReason = cancellationReason;
    }

    public boolean check(G game) {
        return this.condition.test(game);
    }

    public List<GameTeam> getWinners() {
        return this.winners;
    }

    public String getTitleFor(GameTeam team) {
        if (this.cancellationReason != null) {
            return ChatColor.GOLD + "" + ChatColor.BOLD + "Game Cancelled.";
        }
        ChatColor color = this.winners.contains(team) ? ChatColor.DARK_GREEN : ChatColor.DARK_RED;
        return color + "" + ChatColor.BOLD + (this.winners.contains(team) ? "Game won!" : "Game lost.");
    }

    public String getSubTitleFor(GameTeam team) {
        if (this.cancellationReason != null) {
            return this.cancellationReason;
        }
        String reason = this.winners.contains(team) ? this.winReason : this.loseReason;
        return Optional.ofNullable(reason).orElse(this.getDefaultSubTitle());
    }

    private String getDefaultSubTitle() {
        List<String> winningTeams = this.winners.stream()
                .map(GameTeam::getDisplayName)
                .collect(Collectors.toList());
        String winningTeam = String.join(", ", winningTeams);
        String team = "Team" + (winningTeams.size() > 1 ? "s " : " ");
        return team + winningTeam + " won the game.";
    }

    public static class Builder<G> {

        private Predicate<G> condition;
        private final List<GameTeam> winners;
        private String winReason;
        private String loseReason;
        private String cancellationReason;

        public Builder() {
            this.condition = game -> true;
            this.winners = new ArrayList<>();
        }

        public Builder<G> setCondition(Predicate<G> condition) {
            this.condition = condition;
            return this;
        }

        public Builder<G> setWinReason(String title) {
            this.winReason = title;
            return this;
        }

        public Builder<G> setLoseReason(String title) {
            this.loseReason = title;
            return this;
        }

        public Builder<G> setWinners(GameTeam... role) {
            this.winners.addAll(List.of(role));
            return this;
        }

        public Builder<G> setCancellationReason(String reason) {
            this.cancellationReason = reason;
            return this;
        }

        public WinCondition<G> build() {
            return new WinCondition<>(
                    this.condition,
                    this.winners,
                    this.winReason,
                    this.loseReason,
                    this.cancellationReason
            );
        }
    }
}
