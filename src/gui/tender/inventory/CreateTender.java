package gui.tender.inventory;

import gui.tender.Main;
import gui.tender.itemstack.ISCreate;
import gui.tender.json.JWriter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class CreateTender {

    private final File path;
    public CreateTender(File path) {
        this.path = path;
    }

    public void create() {
        final Inventory tender = Bukkit.createInventory(null, 36, "Aktif Ihaleler");


        tender.setItem(27, new ISCreate().createA(Material.ARROW, "§3< Geri Dön", Collections.singletonList(
                "§7Tıkla ve bir önceki menüye dön"
        )));


        cb();

        Main.tender.setInventoryTender(tender);
    }

    private void cb() {
        File file = new File(path + "/data.json");
        try {
            File file1 = new File(path + "/box/test.txt");
            if (!file1.exists()) {
                FileConfiguration c = YamlConfiguration.loadConfiguration(file1);
                c.save(file1);
            }
        } catch (IOException ignored) {}
        if (!file.exists()) {
            System.out.println("Creating new 'data.json' file...");
            try {
                new JWriter(null, path + "/data.json", JWriter.JWriteOption.CLOSE).writeEmpty();
            } catch (Exception e) {
                System.err.println("Exception handled on writing 'data.json' file. Plugin will stop!");
                Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("GuiTender"));
            }
        }

    }

}
