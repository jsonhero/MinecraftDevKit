package com.jasonbratt.tools;


import net.md_5.bungee.api.ChatColor;

/**
 * Created by jasonbratt on 11/19/16.
 */
public class Chat {

    public static String chatPrefix() {
        return ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "[" + ChatColor.GOLD + ChatColor.BOLD + "DevKit" + ChatColor.DARK_AQUA + ChatColor.BOLD + "] ";
    }

    public static String chatServerTransfer(String serverName) {
        return chatPrefix() + ChatColor.GRAY + "Transferring to " + ChatColor.GREEN + serverName + ChatColor.GRAY + " server.";
    }
}
