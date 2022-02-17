package com.than00ber.mcmg.util;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

public class ActionResult {

    private final ChatColor color;
    private final @Nullable String[] messages;
    private final boolean successful;

    public ActionResult(ChatColor color, boolean successful, String... messages) {
        this.color = color;
        this.successful = successful;
        this.messages = messages;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public boolean hasMessages() {
        return messages.length > 0;
    }

    public String[] getFormattedMessages() {
        String[] formatted = new String[messages.length];
        for (int i = 0; i < messages.length; i++) {
            formatted[i] = color + messages[i];
        }
        return formatted;
    }

    public static ActionResult info(String... output) {
        return new ActionResult(ChatColor.WHITE, true, output);
    }

    public static ActionResult success(String... output) {
        return new ActionResult(ChatColor.GREEN, true, output);
    }

    public static ActionResult warn(String... output) {
        return new ActionResult(ChatColor.GOLD, false, output);
    }

    public static ActionResult failure(String... output) {
        return new ActionResult(ChatColor.RED, false, output);
    }
}
