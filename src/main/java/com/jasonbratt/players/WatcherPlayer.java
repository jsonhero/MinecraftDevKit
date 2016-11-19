package com.jasonbratt.players;
import java.util.UUID;

/**
 * Created by jasonbratt on 11/18/16.
 */
public class WatcherPlayer {
    public UUID playerUUID;
    public String serverName;

    public WatcherPlayer(UUID UUID, String serverName) {
        this.playerUUID = UUID;
        this.serverName = serverName;
    }
}
