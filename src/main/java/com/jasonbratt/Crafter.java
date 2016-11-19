package com.jasonbratt;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.jasonbratt.tools.PluginUpdater;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by jasonbratt on 11/16/16.
 */
public class Crafter extends JavaPlugin implements PluginMessageListener {

    @Override
    public void onEnable() {
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
        getLogger().info("Yiippity yawdip dog");
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();

        if (!channel.equals("BungeeCord")) {
            return;
        }

        if (subchannel.equals("DevKitChannel")) {
            String url = in.readUTF();
            String jarName = in.readUTF();
            this.getServer().broadcastMessage("blah " + url);
            this.getServer().broadcastMessage("jar  " + jarName);
            File file = new File("plugins/", jarName);

            try {
                URL fileUrl = new URL(url);
                PluginUpdater.saveFile(fileUrl, file);
                this.getServer().shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onDisable() {

    }
}
