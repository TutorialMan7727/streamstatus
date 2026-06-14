package uk.vrtxx.streamstatus.paper.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import uk.vrtxx.streamstatus.common.command.StatusCommandHandler;
import uk.vrtxx.streamstatus.paper.status.StatusManager;

public class StatusCommand implements CommandExecutor {

    private final StatusManager manager;
    private final StatusCommandHandler handler;

    public StatusCommand(StatusManager manager, StatusCommandHandler handler) {
        this.manager = manager;
        this.handler = handler;
    }

    @Override
    public boolean onCommand(
        CommandSender sender,
        Command command,
        String label,
        String[] args
    ) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Players only.");
            return true;
        }

        var result = handler.execute(
            player.getUniqueId(),
            command.getName(),
            args
        );

        switch (result.type()) {
            case APPLIED -> {
                manager.onStatusChanged(
                    player,
                    result.status(),
                    result.state()
                );
            }
            case UNCHANGED -> player.sendMessage(
                Component.text(
                    result.state() ? "Already enabled." : "Already disabled.",
                    NamedTextColor.YELLOW
                )
            );
            case INVALID -> player.sendMessage(
                Component.text(
                    "Usage: /" + command.getName() + " [on|off]",
                    NamedTextColor.YELLOW
                )
            );
        }

        return true;
    }
}
