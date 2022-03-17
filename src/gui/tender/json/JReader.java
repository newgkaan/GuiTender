package gui.tender.json;

import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class JReader {
    public JSONArray read(String path) {
        try {
            JSONParser parser = new JSONParser();
            FileReader reader = new FileReader(path);
            return (JSONArray) parser.parse(reader);
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
            Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("GuiTender"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
