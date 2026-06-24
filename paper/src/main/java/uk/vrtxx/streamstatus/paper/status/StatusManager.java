package uk.vrtxx.streamstatus.paper.status;

import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import uk.vrtxx.streamstatus.common.status.*;

public class StatusManager {

    private static final long REMINDER_PERIOD_TICKS = 20L * 60L * 30L;

    private final JavaPlugin plugin;
    private final StatusStore store;

    private BukkitTask reminderTask;

    public StatusManager(JavaPlugin plugin, StatusStore store) {
        this.plugin = plugin;
        this.store = store;
    }

    public void toggle(Player player, Status status) {
        setStatus(player, status, !store.has(player.getUniqueId(), status));
    }

    public boolean hasAny(UUID uuid) {
        return store.hasAny(uuid);
    }

    public void setStatus(Player player, Status status, boolean enable) {
        UUID uuid = player.getUniqueId();

        boolean changed = store.set(uuid, status, enable);

        if (!changed) {
            player.sendMessage(
                enable
                    ? StatusTextFactory.already(status)
                    : StatusTextFactory.notSet(status)
            );
            return;
        }

        Bukkit.broadcast(
            enable
                ? StatusTextFactory.enabled(player.getName(), status)
                : StatusTextFactory.disabled(player.getName(), status)
        );

        updateTab(player);
    }

    public void onStatusChanged(Player player, Status status, boolean state) {
        Bukkit.broadcast(
            state
                ? StatusTextFactory.enabled(player.getName(), status)
                : StatusTextFactory.disabled(player.getName(), status)
        );

        updateTab(player);
    }

    public void updateTab(Player player) {
        UUID uuid = player.getUniqueId();

        var set = store.get(uuid);

        if (set.isEmpty()) {
            player.playerListName(null);
            return;
        }

        Component prefix = StatusTextFactory.tabPrefix(set);

        player.playerListName(
            StatusTextFactory.tabDisplay(player.getName(), prefix)
        );
    }

    public void removeAll(Player player) {
        store.clear(player.getUniqueId());
        player.playerListName(null);
    }

    public void startReminderTask() {
        reminderTask = Bukkit.getScheduler().runTaskTimer(
            plugin,
            this::sendReminders,
            REMINDER_PERIOD_TICKS,
            REMINDER_PERIOD_TICKS
        );
    }

    public void shutdown() {
        if (reminderTask != null) {
            reminderTask.cancel();
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playerListName(null);
        }

        store.clearAll();
    }

    private void sendReminders() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!store.hasAny(p.getUniqueId())) {
                p.sendMessage(StatusTextFactory.reminder());
            }
        }
    }
}
