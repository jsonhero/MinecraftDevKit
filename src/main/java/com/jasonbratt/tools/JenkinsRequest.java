package com.jasonbratt.tools;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jasonbratt.DevKitBungee;
import com.jasonbratt.watcher.WatcherItem;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;


public class JenkinsRequest {
    private String baseUrl;
    private String token;
    private DevKitBungee plugin;

    public JenkinsRequest(String username, String password, String host, String token, DevKitBungee plugin) {
        this.baseUrl = "http://" + username + ":" + password + "@" + host;
        this.token = "?token=" + token;
        this.plugin = plugin;
    }

    public JsonObject makeRequest(String path) {
        URL url = null;
        JsonObject jo = null;
        try {
            url = new URL(this.formUrl(path));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            jo = this.readJsonFromUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jo;
    }

    public boolean healthCheck() {
        URLConnection connection = null;
        try {
            URL url = new URL(this.formUrl("/api/json"));
            connection = url.openConnection();
            connection.connect();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getArtifactUrl(WatcherItem watcher, int buildNum) {
        URL url = null;
        try {
            url = new URL(this.formUrl("/job/" + watcher.watcherName + "/" + Integer.toString(buildNum) + "/api/json"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        JsonObject jo = null;
        try {
            jo = this.readJsonFromUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JsonArray ar;
        if ((ar = jo.get("artifacts").getAsJsonArray()).size() > 0) {
            watcher.setJar(ar.get(0).getAsJsonObject().get("fileName").getAsString());
            String arPath = ar.get(0).getAsJsonObject().get("relativePath").getAsString();
            plugin.watcherConfig.save();
            return this.formUrl("/job/" + watcher.watcherName + "/" + Integer.toString(buildNum) + "/artifact/" + arPath);
        }

        return null;
    }

    public String formUrl(String path) {
        return this.baseUrl + path + this.token;
    }


    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private JsonObject readJsonFromUrl(URL url) throws IOException {
        URLConnection connection = url.openConnection();
        if (url.getUserInfo() != null) {
            String basicAuth = "Basic " + new String(new Base64().encode(url.getUserInfo().getBytes()));
            connection.setRequestProperty("Authorization", basicAuth);
        }
        InputStream is = connection.getInputStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JsonObject json = new JsonParser().parse(jsonText).getAsJsonObject();
            return json;
        } finally {
            is.close();
        }
    }
}
