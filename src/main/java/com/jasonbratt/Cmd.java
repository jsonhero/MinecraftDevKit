package com.jasonbratt;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jasonbratt.players.WatcherPlayer;
import com.jasonbratt.watcher.WatcherItem;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by jasonbratt on 11/14/16.
*/


public class Cmd extends Command {
    private DevKitBungee plugin;

    public Cmd(DevKitBungee plugin) {
        super("devkit");
        this.plugin = plugin;
    }

    public void helpMenu(CommandSender sender) {
        sender.sendMessage(new TextComponent(ChatColor.GOLD + "-----(=) DevKit (=)-----"));
        sender.sendMessage(new TextComponent(ChatColor.AQUA + "/devkit watchlist - List possible watchers."));
        sender.sendMessage(new TextComponent(ChatColor.AQUA + "/devkit addwatcher <watcher> <server> - Add watcher to a server."));
        sender.sendMessage(new TextComponent(ChatColor.AQUA + "/devkit removewatcher <watcher> <server> - Remove watcher from a server."));
        sender.sendMessage(new TextComponent(ChatColor.AQUA + "/devkit watchers - Show all registered watchers."));
        sender.sendMessage(new TextComponent(ChatColor.AQUA + "/devkit listen <server> - Listen to a server for watcher updates."));
        sender.sendMessage(new TextComponent(ChatColor.AQUA + "/devkit unlisten <server> - Stop listening to a sever."));
        sender.sendMessage(new TextComponent(ChatColor.AQUA + "/devkit me - Show servers you're listening to."));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            this.helpMenu(sender);
            return;
        }

        if (args[0].equalsIgnoreCase("watchlist")) {
            JsonObject jo = this.plugin.jr.makeRequest("/api/json");
            sender.sendMessage(new TextComponent(ChatColor.LIGHT_PURPLE + "Watcher List:"));
            for (JsonElement j : jo.get("jobs").getAsJsonArray()) {
                sender.sendMessage(new TextComponent(ChatColor.GRAY + j.getAsJsonObject().get("name").getAsString()));
            }
        } else if (args[0].equalsIgnoreCase("addwatcher") && args.length == 3 ) {
            JsonObject jo = this.plugin.jr.makeRequest("/api/json");
            for (JsonElement j : jo.get("jobs").getAsJsonArray()) {
                if (args[1].equalsIgnoreCase(j.getAsJsonObject().get("name").getAsString())) {
                    WatcherItem watcher = new WatcherItem(j.getAsJsonObject().get("name").getAsString(), args[2]);
                    watcher.setBuild();
                    boolean added = this.plugin.watcherConfig.addWatcher(watcher);
                    if (added) {
                        sender.sendMessage(new TextComponent(ChatColor.GREEN + "Added watcher!"));
                    } else {
                        sender.sendMessage(new TextComponent(ChatColor.RED + "That watcher is already registered."));
                    }
                }
            }
        } else if (args[0].equalsIgnoreCase("removewatcher") && args.length == 3) {
            if (this.plugin.watcherConfig.removeWatcher(args[1], args[2])) {
                sender.sendMessage(new TextComponent(ChatColor.GREEN + "Removed watcher"));
            } else {
                sender.sendMessage(new TextComponent(ChatColor.RED + "That watcher is not registered."));
            }
        } else if (args[0].equalsIgnoreCase("listen") && args[1].length() > 0) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (this.plugin.playerConfig.addPlayer(new WatcherPlayer(player.getUniqueId(), args[1]))) {
                sender.sendMessage(new TextComponent(ChatColor.GREEN +"Added listener"));
            } else {
                sender.sendMessage(new TextComponent(ChatColor.RED + "That listener is already registered."));

            }

        } else if (args[0].equalsIgnoreCase("unlisten") && args.length == 2) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (this.plugin.playerConfig.removePlayer(player.getUniqueId(), args[1])) {
                sender.sendMessage(new TextComponent(ChatColor.GREEN +"Removed listener"));
            } else {
                sender.sendMessage(new TextComponent(ChatColor.RED + "That listener is not registered."));
            }
        } else if (args[0].equalsIgnoreCase("watchers")) {
            sender.sendMessage(new TextComponent(ChatColor.LIGHT_PURPLE + "Watchers:"));
            for (WatcherItem watcher : this.plugin.watcherConfig.getWatchers()) {
                sender.sendMessage(new TextComponent(ChatColor.GRAY + "(Watcher: " + ChatColor.YELLOW + watcher.watcherName + ChatColor.GRAY + " | Server: " + ChatColor.YELLOW + watcher.serverName + ChatColor.GRAY + ")"));
            }
        } else if (args[0].equalsIgnoreCase("me")) {
            sender.sendMessage(new TextComponent(ChatColor.LIGHT_PURPLE + "Servers:"));
            ProxiedPlayer player = (ProxiedPlayer) sender;
            for (WatcherPlayer wp : this.plugin.playerConfig.getPlayers()) {
                if (player.getUniqueId().compareTo(wp.playerUUID) == 0) {
                    sender.sendMessage(new TextComponent(ChatColor.GRAY + wp.serverName));
                }
            }

        } else {
            this.helpMenu(sender);
        }

    }


}
