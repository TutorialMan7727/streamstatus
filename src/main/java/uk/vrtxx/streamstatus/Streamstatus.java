package uk.vrtxx.streamstatus;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import uk.vrtxx.streamstatus.command.StatusCommand;
import uk.vrtxx.streamstatus.listener.PlayerListener;
import uk.vrtxx.streamstatus.status.StatusManager;

public final class Streamstatus extends JavaPlugin {

    private StatusManager statusManager;

    @Override
    public void onEnable() {
        this.statusManager = new StatusManager(this);

        Bukkit.getPluginManager().registerEvents(
                new PlayerListener(statusManager),
                this
        );

        StatusCommand command = new StatusCommand(statusManager);

        getCommand("streaming").setExecutor(command);
        getCommand("recording").setExecutor(command);

        statusManager.startReminderTask();

        getLogger().info("Streamstatus enabled");
    }

    @Override
    public void onDisable() {
        statusManager.shutdown();
        getLogger().info("Streamstatus disabled");
    }

    public StatusManager getStatusManager() {
        return statusManager;
    }
}