package uk.vrtxx.streamstatus.paper;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import uk.vrtxx.streamstatus.common.command.StatusCommandHandler;
import uk.vrtxx.streamstatus.common.listener.StatusLifecycleService;
import uk.vrtxx.streamstatus.common.status.StatusStore;
import uk.vrtxx.streamstatus.paper.command.StatusCommand;
import uk.vrtxx.streamstatus.paper.listener.PlayerListener;
import uk.vrtxx.streamstatus.paper.status.StatusManager;

public final class Streamstatus extends JavaPlugin {

    private StatusManager statusManager;

    @Override
    public void onEnable() {
        StatusStore store = new StatusStore();
        StatusLifecycleService lifecycle = new StatusLifecycleService(store);
        StatusCommandHandler commandHandler = new StatusCommandHandler(store);

        this.statusManager = new StatusManager(this, store);

        Bukkit.getPluginManager().registerEvents(
            new PlayerListener(statusManager, lifecycle),
            this
        );

        StatusCommand command = new StatusCommand(
            statusManager,
            commandHandler
        );

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
