/*
 * StreamStatusPlugin
 * A Paper plugin that lets players toggle streaming or recording status.
 * When toggled on, it broadcasts to all players and tags them in the tab list.
 */

package uk.vrtxx.streamstatus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Streamstatus extends JavaPlugin implements Listener, CommandExecutor {

    private final Set<UUID> streamingPlayers = new HashSet<>();
    private final Set<UUID> recordingPlayers = new HashSet<>();

    @Override
    public void onEnable() {
        // Register commands
        getCommand("streaming").setExecutor(this);
        getCommand("recording").setExecutor(this);
        // Register events
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("StreamStatusPlugin enabled");
    }

    @Override
    public void onDisable() {
        streamingPlayers.clear();
        recordingPlayers.clear();
        getLogger().info("StreamStatusPlugin disabled");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;
        Set<UUID> targetSet;
        String type;

        if (label.equalsIgnoreCase("streaming")) {
            targetSet = streamingPlayers;
            type = "streaming";
        } else if (label.equalsIgnoreCase("recording")) {
            targetSet = recordingPlayers;
            type = "recording";
        } else {
            return false;
        }

        if (args.length != 1) {
            player.sendMessage("Usage: /" + label + " <on|off>");
            return true;
        }

        String action = args[0].toLowerCase();
        if (action.equals("on")) {
            if (targetSet.add(player.getUniqueId())) {
                Bukkit.broadcastMessage("⚠ " + player.getName() + " is now " + type + "!");
                updateTabList(player, true);
            } else {
                player.sendMessage("You are already " + type + "!");
            }
        } else if (action.equals("off")) {
            if (targetSet.remove(player.getUniqueId())) {
                Bukkit.broadcastMessage("ℹ " + player.getName() + " has stopped " + type + ".");
                updateTabList(player, false);
            } else {
                player.sendMessage("You are not currently " + type + ".");
            }
        } else {
            player.sendMessage("Usage: /" + label + " <on|off>");
        }
        return true;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        boolean wasStreaming = streamingPlayers.remove(p.getUniqueId());
        boolean wasRecording = recordingPlayers.remove(p.getUniqueId());
        if (wasStreaming) {
            Bukkit.broadcastMessage("ℹ " + p.getName() + " has disconnected and stopped streaming.");
        }
        if (wasRecording) {
            Bukkit.broadcastMessage("ℹ " + p.getName() + " has disconnected and stopped recording.");
        }
    }

    // Adds or removes [LIVE] tag in the tab list prefix
    private void updateTabList(Player player, boolean add) {
        if (add) {
            player.setPlayerListName(ChatColor.RED + "[LIVE] " + ChatColor.RESET + player.getName());
        } else {
            player.setPlayerListName(player.getName());
        }
    }
}
