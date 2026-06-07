package uk.vrtxx.streamstatus.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import uk.vrtxx.streamstatus.status.StatusManager;

public class PlayerListener implements Listener {

    private final StatusManager manager;

    public PlayerListener(StatusManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();

        manager.updateTab(player);

        if (!manager.hasAny(player.getUniqueId())) {
            player.sendMessage("Use /streaming or /recording to set your status.");
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        manager.removeAll(event.getPlayer());
    }
}