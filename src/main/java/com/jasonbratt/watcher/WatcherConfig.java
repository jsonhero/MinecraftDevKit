package com.jasonbratt.watcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jasonbratt.DevKitBungee;

import java.io.*;
import java.util.HashSet;

/**
 * Created by jasonbratt on 11/16/16.
 */
public class WatcherConfig {
    private File file = new File("plugins/DevKit/", "watchers.json");
    private HashSet<WatcherItem> watchers;
    private boolean loaded = false;
    private DevKitBungee plugin;

    public WatcherConfig (DevKitBungee plugin) {
        this.plugin = plugin;
    }

    public HashSet<WatcherItem> load() {
        try {
            if (!file.exists()) {
                this.watchers = new HashSet<>();
                this.save();
            } else {
                FileReader reader = new FileReader(file);
                HashSet<WatcherItem> watchers = new Gson().fromJson(reader, new TypeToken<HashSet<WatcherItem>>(){}.getType());
                this.watchers = watchers;
                reader.close();
            }
            loaded = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return watchers;
    }

    public HashSet<WatcherItem> getWatchers() {
        return watchers;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void save() {
        try {
            FileWriter writer = new FileWriter(file);
            new GsonBuilder().setPrettyPrinting().create().toJson(watchers, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public WatcherItem watcherExists(String watcherName, String serverName) {
        for (WatcherItem watcher: watchers) {
            if (watcher.watcherName.equalsIgnoreCase(watcherName) && watcher.serverName.equalsIgnoreCase(serverName)) {
                return watcher;
            }
        }

        return null;
    }

    public boolean addWatcher(WatcherItem item) {
        if (!this.loaded) {
            return false;
        }

        if (this.watcherExists(item.watcherName, item.serverName) != null) {
            return false;
        }

        watchers.add(item);
        this.save();
        return true;
    }

    public boolean removeWatcher(String watcherName, String serverName) {
        if (!this.loaded) {
            return false;
        }

        WatcherItem watcher;
        if ((watcher = this.watcherExists(watcherName, serverName)) != null) {
            watchers.remove(watcher);
            this.save();
            return true;
        }

        return false;
    }

}
