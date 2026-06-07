package uk.vrtxx.streamstatus.status;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class StatusManager {

    private static final long REMINDER_PERIOD_TICKS = 20L * 60L * 5L;

    private final JavaPlugin plugin;
    private final Map<UUID, EnumSet<Status>> activeStatuses = new HashMap<>();
    private BukkitTask reminderTask;

    public StatusManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void toggle(Player player, Status status) {
        setStatus(player, status, !has(player.getUniqueId(), status));
    }

    public void setStatus(Player player, Status status, boolean enable) {
        UUID uuid = player.getUniqueId();

        EnumSet<Status> set =
                activeStatuses.computeIfAbsent(uuid, k -> EnumSet.noneOf(Status.class));

        boolean changed = enable ? set.add(status) : set.remove(status);

        if (!changed) {
            player.sendMessage(Component.text(
                    enable
                            ? "Already " + status.verb + "."
                            : "Not currently " + status.verb + ".",
                    NamedTextColor.YELLOW
            ));
            return;
        }

        if (set.isEmpty()) {
            activeStatuses.remove(uuid);
        }

        Bukkit.broadcast(statusMessage(player.getName(), status, enable));
        updateTab(player);
    }

    public boolean has(UUID uuid, Status status) {
        EnumSet<Status> set = activeStatuses.get(uuid);
        return set != null && set.contains(status);
    }

    public boolean hasAny(UUID uuid) {
        EnumSet<Status> set = activeStatuses.get(uuid);
        return set != null && !set.isEmpty();
    }

    public void removeAll(Player player) {
        activeStatuses.remove(player.getUniqueId());
        player.playerListName(null);
    }

    public void updateTab(Player player) {
        EnumSet<Status> set = activeStatuses.get(player.getUniqueId());

        if (set == null || set.isEmpty()) {
            player.playerListName(null);
            return;
        }

        Component prefix = Component.empty();

        if (set.contains(Status.STREAMING)) {
            prefix = prefix.append(Status.STREAMING.tabTag());

        }

        if (set.contains(Status.RECORDING)) {
            prefix = prefix.append(Status.RECORDING.tabTag());
        }

        player.playerListName(
            Component.empty()
                .append(Component.text("[", NamedTextColor.GRAY))
                .append(prefix)
                .append(Component.text("] ", NamedTextColor.GRAY))
                .append(Component.text(player.getName(), NamedTextColor.WHITE))
        );
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

        activeStatuses.clear();
    }

    private void sendReminders() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!hasAny(p.getUniqueId())) {
                p.sendMessage(Component.text(
                        "Use /streaming or /recording to set your status.",
                        NamedTextColor.YELLOW
                ));
            }
        }
    }

    private Component statusMessage(String name, Status status, boolean enabled) {
        if (enabled) {
            return Component.text("⚠ ", NamedTextColor.GOLD)
                    .append(Component.text(name, NamedTextColor.WHITE))
                    .append(Component.text(" is now " + status.verb + "!", NamedTextColor.GRAY));
        }

        return Component.text("ℹ ", NamedTextColor.AQUA)
                .append(Component.text(name, NamedTextColor.WHITE))
                .append(Component.text(" stopped " + status.verb + ".", NamedTextColor.GRAY));
    }
}