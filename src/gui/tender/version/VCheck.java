package gui.tender.version;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class VCheck {

    public boolean makeCheck() {
        try {
            final String version = "0.3";
            URL link = new URL("https://raw.githubusercontent.com/kYaaaz/version-check/main/guiTender");
            URLConnection connection = link.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String var;
            while ((var = reader.readLine()) != null)
                if (var.contains(version)) return false;
            reader.close();
        } catch (Exception ignored) {
            System.err.println("Can't connect version check...");
            return false;
        }
        return true;
    }
}
