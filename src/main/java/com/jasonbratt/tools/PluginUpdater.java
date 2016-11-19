package com.jasonbratt.tools;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by jasonbratt on 11/16/16.
 */
public class PluginUpdater {

    public static void saveFile(URL url, File file) throws IOException {
        URLConnection connection = url.openConnection();
        String basicAuth = "Basic " + new String(new Base64().encode(url.getUserInfo().getBytes()));
        connection.setRequestProperty("Authorization", basicAuth);
        //Fix for Jenkins not sending an auth-challenge response for file downloads
        connection.setRequestProperty("WWW-Authenticate", "Basic realm=\"fake\"");

        InputStream in = connection.getInputStream();
        FileOutputStream fos = new FileOutputStream(file);

        int length = -1;
        byte[] buffer = new byte[1024];// buffer for portion of data from
        // connection
        while ((length = in.read(buffer)) > -1) {
            fos.write(buffer, 0, length);
        }

        fos.close();
        in.close();
    }

}
