package com.jasonbratt.players;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jasonbratt.DevKitBungee;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by jasonbratt on 11/18/16.
 */
public class PlayerConfig {
    private File file = new File("plugins/DevKit/", "players.json");
    private HashSet<WatcherPlayer> players;
    private boolean loaded = false;
    private DevKitBungee plugin;

    public PlayerConfig (DevKitBungee plugin) {
        this.plugin = plugin;
    }

    public HashSet<WatcherPlayer> load() {
        try {
            if (!file.exists()) {
                this.players = new HashSet<>();
                this.save();
            } else {
                FileReader reader = new FileReader(file);
                HashSet<WatcherPlayer> players = new Gson().fromJson(reader, new TypeToken<HashSet<WatcherPlayer>>(){}.getType());
                this.players = players;
                reader.close();
            }
            loaded = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return players;
    }

    public HashSet<WatcherPlayer> getPlayers() {
        return players;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void save() {
        try {
            FileWriter writer = new FileWriter(file);
            new GsonBuilder().setPrettyPrinting().create().toJson(players, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public WatcherPlayer playerExists(UUID uuid, String serverName) {
        for (WatcherPlayer player: players) {
            if (player.serverName.equalsIgnoreCase(serverName) && (player.playerUUID.compareTo(uuid) == 0)) {
                return player;
            }
        }

        return null;
    }

    public boolean addPlayer(WatcherPlayer player) {
        if (!this.loaded) {
            return false;
        }

        if (this.playerExists(player.playerUUID, player.serverName) != null) {
            return false;
        }

        players.add(player);
        this.save();
        return true;
    }

    public boolean removePlayer(UUID uuid, String serverName) {
        if (!this.loaded) {
            return false;
        }

        WatcherPlayer player;
        if ((player = this.playerExists(uuid, serverName)) != null) {
            players.remove(player);
            this.save();
            return true;
        }

        return false;
    }
}
