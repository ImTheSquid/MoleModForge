package dev.jackhogan.molemodforge;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class OffenseTracker {
    private boolean isOffending = false;
    private int offenses = 1;

    public int getOffenses() {
        return offenses;
    }

    public void startOffense(Player player) {
        if (!isOffending) {
            int end = offenses % 10;
            int second = (offenses / 10) % 10;
            String suffix = switch (end) {
                case 1 -> "st";
                case 2 -> "nd";
                case 3 -> "rd";
                default -> "th";
            };
            if (second == 1) {
                suffix = "th";
            }
            player.displayClientMessage(Component.literal(ChatFormatting.RED + "IT BURNS! This is the " + offenses + suffix + " time you've ventured out!"), true);
        }
        isOffending = true;
    }

    public void endOffense() {
        if (!isOffending) {
            return;
        }
        isOffending = false;
        offenses++;
    }
}
