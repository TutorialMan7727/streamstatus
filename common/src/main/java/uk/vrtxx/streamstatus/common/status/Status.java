package uk.vrtxx.streamstatus.common.status;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public enum Status {
    STREAMING("streaming", "▶", NamedTextColor.GOLD, "streaming"),
    RECORDING("recording", "⏺", NamedTextColor.RED, "recording");

    public final String commandName;
    public final String icon;
    public final NamedTextColor color;
    public final String verb;

    Status(String commandName, String icon, NamedTextColor color, String verb) {
        this.commandName = commandName;
        this.icon = icon;
        this.color = color;
        this.verb = verb;
    }

    public Component tabTag() {
        return Component.text(icon, color);
    }
}
