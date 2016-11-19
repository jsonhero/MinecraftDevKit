package com.jasonbratt.watcher;

import com.google.gson.JsonObject;
import com.jasonbratt.DevKitBungee;

/**
 * Created by jasonbratt on 11/15/16.
 */
public class WatcherItem {
    public String watcherName;
    public String serverName;
    public Integer lastSuccessfulBuild;
    public String jarName = "";

    public WatcherItem(String watcherName, String serverName) {
        this.watcherName = watcherName;
        this.serverName = serverName;
    }

    public Integer setBuild() {
        JsonObject json = DevKitBungee.jr.makeRequest("/job/" + watcherName + "/api/json");
        if (!json.get("lastSuccessfulBuild").isJsonNull()) {
            Integer newBuild = json.get("lastSuccessfulBuild").getAsJsonObject().get("number").getAsInt();;
            if (lastSuccessfulBuild != newBuild) {
                lastSuccessfulBuild = newBuild;
                return lastSuccessfulBuild;

            }
        }

        if (lastSuccessfulBuild == null) {
            lastSuccessfulBuild = 0;
        }

        return lastSuccessfulBuild;
    }

    public void setJar(String jar) {
        jarName = jar;
    }

}
