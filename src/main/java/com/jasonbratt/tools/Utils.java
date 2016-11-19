package com.jasonbratt.tools;

import com.jasonbratt.DevKit;
import com.jasonbratt.players.WatcherPlayer;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Created by jasonbratt on 11/18/16.
 */
public class Utils {
    private DevKit plugin;

    public Utils(DevKit plugin) {
        this.plugin = plugin;
    }

    public boolean pingServer(ServerInfo server) {
        Socket socket = new Socket();
        try {
            socket.connect(server.getAddress(), 500);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public HashSet<ProxiedPlayer> getConnectedPlayers(HashSet<WatcherPlayer> players, String serverName) {
        HashSet<ProxiedPlayer> connectedPlayers = new HashSet<>();

        Iterator it = players.iterator();

        while (it.hasNext()) {
            WatcherPlayer p = (WatcherPlayer) it.next();
            ProxiedPlayer cp = plugin.getProxy().getPlayer(p.playerUUID);
            if (cp.isConnected() && p.serverName.equalsIgnoreCase(serverName)) {
                connectedPlayers.add(cp);
            }
        }
        return connectedPlayers;
    }

    public class SelfTask {
        public ScheduledTask task;
    }

    public void reconnectPlayers(ServerInfo server, Collection<ProxiedPlayer> players) {
        if (players.size() > 0) {

            SelfTask t = new SelfTask();

            t.task = this.plugin.getProxy().getScheduler().schedule(this.plugin, new Runnable() {
                int timeSpent = 0;
                int timeOut = 10000;

                @Override
                public void run() {
                    if (pingServer(server)) {
                        for (ProxiedPlayer player : players) {
                            player.connect(server, new Callback<Boolean>() {
                                @Override
                                public void done(Boolean aBoolean, Throwable throwable) {
                                    if (aBoolean) {
                                        plugin.getLogger().info("Conencted player " + player.getDisplayName());
                                    } else {
                                        plugin.getLogger().info("bad connection " + player.getDisplayName());
                                    }
                                }
                            });
                        }
                        t.task.cancel();
                    } else if (timeSpent == timeOut ){
                        t.task.cancel();
                    }
                    timeSpent += 1000;
                }
            }, 1, 1, TimeUnit.SECONDS);
        }
    }
}
