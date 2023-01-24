package gui.tender.tender;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class TenderID {
    private Long id = 0L;
    public TenderID() {

    }

    public void add() {
        id += 1L;
    }
    public Long get() {
        return this.id;
    }

    public void create(String path) {
        File file = new File(path + "/current.yml");
        if (!file.exists()) return;
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        if (!configuration.isSet("current")) return;
        id = configuration.getLong("current");
    }
}
