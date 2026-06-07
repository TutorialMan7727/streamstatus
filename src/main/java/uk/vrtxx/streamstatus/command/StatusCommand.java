package uk.vrtxx.streamstatus.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import uk.vrtxx.streamstatus.status.Status;
import uk.vrtxx.streamstatus.status.StatusManager;

import java.util.Locale;

public class StatusCommand implements CommandExecutor {

    private final StatusManager manager;

    public StatusCommand(StatusManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender == null) 
            return true;
        

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Players only.");
            return true;
        }

        Status status = Status.valueOf(command.getName().toUpperCase(Locale.ROOT));

        if (args.length == 0) {
            manager.toggle(player, status);
            return true;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "on" -> manager.setStatus(player, status, true);
            case "off" -> manager.setStatus(player, status, false);
            default -> player.sendMessage(Component.text(
                    "Usage: /" + status.commandName + " [on|off]",
                    NamedTextColor.YELLOW
            ));
        }

        return true;
    }
}