package com.jasonbratt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jasonbratt.players.PlayerConfig;
import com.jasonbratt.tools.JenkinsRequest;
import com.jasonbratt.tools.Utils;
import com.jasonbratt.watcher.WatcherConfig;
import com.jasonbratt.watcher.WatcherItem;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;


import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class DevKit extends Plugin

{
    public static Config config;
    public static JenkinsRequest jr;
    public WatcherConfig watcherConfig = new WatcherConfig(this);
    public HashMap<String, Collection<ProxiedPlayer>> waitingPlayers = new HashMap<>();
    public Utils utils = new Utils(this);
    public PlayerConfig playerConfig = new PlayerConfig(this);



    public void onEnable() {
        config = this.getConfig();
        watcherConfig.load();
        playerConfig.load();

        jr = new JenkinsRequest(config.username, config.password, config.jenkinsHost, config.apiToken, this);
        if (jr.healthCheck()) {
            this.enableWatchers(watcherConfig.getWatchers());
            getProxy().getPluginManager().registerCommand(this, new Cmd(this));
        }
    }

    public void enableWatchers(HashSet<WatcherItem> watchers) {
        this.getProxy().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                for (WatcherItem watcher : watchers) {
                    enableWatcher(watcher);
                }
            }
        }, 1, 10, TimeUnit.SECONDS);

    }

    public void enableWatcher(WatcherItem watcher) {
        Integer oldBuild = watcher.lastSuccessfulBuild;
        Integer checkedBuild = watcher.setBuild();

        if (checkedBuild != 0 && oldBuild != checkedBuild) {
            watcherConfig.save();

            String url = jr.getArtifactUrl(watcher, checkedBuild);
            ServerInfo server = getProxy().getServerInfo(watcher.serverName);

            if (server != null) {
                Collection<ProxiedPlayer> players = server.getPlayers();
                waitingPlayers.put(server.getName(), players);

                for (ProxiedPlayer player : players) {
                    player.connect(getProxy().getServerInfo(config.lobbyServer));
                    getLogger().info("wee");
                }

                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);
                try {
                    out.writeUTF("DevKitChannel");
                    out.writeUTF(url);
                    out.writeUTF(watcher.jarName);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                utils.reconnectPlayers(server, utils.getConnectedPlayers(playerConfig.getPlayers(), server.getName()));
                server.sendData("BungeeCord", b.toByteArray());
            }
        }
    }

    public Config getConfig() {
        File file = new File("plugins/DevKit/", "config.json");

        if (!file.exists()) {

            file.getParentFile().mkdirs();
            FileWriter writer = null;
            try {
                writer = new FileWriter(file);
                new GsonBuilder().setPrettyPrinting().create().toJson(new Config(), writer);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileReader reader = null;
        try {
            reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return new Gson().fromJson(reader, new TypeToken<Config>(){}.getType());
    }

}
