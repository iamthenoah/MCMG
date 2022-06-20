package com.than00ber.mcmg;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WinCondition<G> {

    private final Predicate<G> condition;
    private final List<MiniGameTeam> winners;
    private final @Nullable String winReason;
    private final @Nullable String loseReason;
    public final @Nullable String cancellationReason;

    public WinCondition(
            Predicate<G> condition,
            List<MiniGameTeam> winners,
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
        return condition.test(game);
    }

    public List<MiniGameTeam> getWinners() {
        return winners;
    }

    public String getTitleFor(MiniGameTeam team) {
        if (cancellationReason != null) {
            return ChatColor.GOLD + "" + ChatColor.BOLD + "Minigame Cancelled.";
        }
        ChatColor color = winners.contains(team) ? ChatColor.DARK_GREEN : ChatColor.DARK_RED;
        return color + "" + ChatColor.BOLD + (winners.contains(team) ? "Minigame won!" : "Minigame lost.");
    }

    public String getSubTitleFor(MiniGameTeam team) {
        if (cancellationReason != null) {
            return cancellationReason;
        }
        String reason = winners.contains(team) ? winReason : loseReason;
        return Optional.ofNullable(reason).orElse(getDefaultSubTitle());
    }

    private String getDefaultSubTitle() {
        List<String> winningTeams = winners.stream()
                .map(MiniGameTeam::getDisplayName)
                .collect(Collectors.toList());
        String winningTeam = String.join(", ", winningTeams);
        String team = "Team" + (winningTeams.size() > 1 ? "s " : " ");
        return team + winningTeam + " won the minigame.";
    }

    public static class Builder<G> {

        private Predicate<G> winCondition;
        private final List<MiniGameTeam> winners;
        private String winReason;
        private String loseReason;
        private String cancellationReason;

        public Builder() {
            winCondition = game -> true;
            winners = new ArrayList<>();
        }

        public Builder<G> setCondition(Predicate<G> condition) {
            winCondition = condition;
            return this;
        }

        public Builder<G> setWinReason(String title) {
            winReason = title;
            return this;
        }

        public Builder<G> setLoseReason(String title) {
            loseReason = title;
            return this;
        }

        public Builder<G> setWinners(MiniGameTeam... role) {
            winners.addAll(List.of(role));
            return this;
        }

        public Builder<G> setCancellationReason(String reason) {
            cancellationReason = reason;
            return this;
        }

        public WinCondition<G> build() {
            return new WinCondition<>(
                    winCondition,
                    winners,
                    winReason,
                    loseReason,
                    cancellationReason
            );
        }
    }
}
