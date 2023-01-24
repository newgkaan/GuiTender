package gui.tender.json;

import org.bukkit.Bukkit;
import org.json.simple.JSONArray;

import java.io.FileWriter;
import java.io.IOException;

public class JWriter {
    public enum JWriteOption {
        CLOSE, FLUSH
    }

    private final JSONArray write;
    private final String path;
    private final JWriteOption option;
    public JWriter(JSONArray write, String path, JWriteOption option) {
        this.write = write;
        this.path = path;
        this.option = option;
    }
    public void writeEmpty() {
        try {
            FileWriter writer = new FileWriter(path);
            switch (option) {
                case CLOSE:
                    writer.close();
                    break;
                case FLUSH:
                    writer.flush();
                    break;
            }
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
            Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("GuiTender"));
        }
    }
    public void write() {
        try {
            FileWriter writer = new FileWriter(path);
            writer.write(write.toJSONString());
            switch (option) {
                case CLOSE:
                    writer.close();
                    break;
                case FLUSH:
                    writer.flush();
                    break;
            }
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
            Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("GuiTender"));
        }
    }
}