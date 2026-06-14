package uk.vrtxx.streamstatus.paper.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import uk.vrtxx.streamstatus.common.listener.StatusLifecycleService;
import uk.vrtxx.streamstatus.common.status.StatusTextFactory;
import uk.vrtxx.streamstatus.paper.status.StatusManager;

public class PlayerListener implements Listener {

    private final StatusManager manager;
    private final StatusLifecycleService lifecycle;

    public PlayerListener(
        StatusManager manager,
        StatusLifecycleService lifecycle
    ) {
        this.manager = manager;
        this.lifecycle = lifecycle;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();

        manager.updateTab(player);

        if (!lifecycle.hasAny(player.getUniqueId())) {
            player.sendMessage(StatusTextFactory.reminder());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        var player = event.getPlayer();

        manager.removeAll(player);
        lifecycle.remove(player.getUniqueId());
    }
}
