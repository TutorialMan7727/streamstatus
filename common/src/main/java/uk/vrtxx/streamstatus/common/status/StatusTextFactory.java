package uk.vrtxx.streamstatus.common.status;

import java.util.EnumSet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class StatusTextFactory {

    private StatusTextFactory() {}

    public static Component already(Status status) {
        return Component.text(
            "Already " + status.verb + ".",
            NamedTextColor.YELLOW
        );
    }

    public static Component notSet(Status status) {
        return Component.text(
            "Not currently " + status.verb + ".",
            NamedTextColor.YELLOW
        );
    }

    public static Component enabled(String name, Status status) {
        return Component.text("⚠ ", NamedTextColor.GOLD)
            .append(Component.text(name, NamedTextColor.WHITE))
            .append(
                Component.text(
                    " is now " + status.verb + "!",
                    NamedTextColor.GRAY
                )
            );
    }

    public static Component disabled(String name, Status status) {
        return Component.text("ℹ ", NamedTextColor.AQUA)
            .append(Component.text(name, NamedTextColor.WHITE))
            .append(
                Component.text(
                    " stopped " + status.verb + ".",
                    NamedTextColor.GRAY
                )
            );
    }

    public static Component reminder() {
        return Component.text(
            "Use /streaming or /recording to set your status.",
            NamedTextColor.YELLOW
        );
    }

    public static Component tabPrefix(EnumSet<Status> set) {
        Component prefix = Component.empty();

        if (set.contains(Status.STREAMING)) {
            prefix = prefix.append(Status.STREAMING.tabTag());
        }

        if (set.contains(Status.RECORDING)) {
            prefix = prefix.append(Status.RECORDING.tabTag());
        }

        return prefix;
    }

    public static Component tabDisplay(String name, Component prefix) {
        return Component.text("[", NamedTextColor.GRAY)
            .append(prefix)
            .append(Component.text("] ", NamedTextColor.GRAY))
            .append(Component.text(name, NamedTextColor.WHITE));
    }
}
