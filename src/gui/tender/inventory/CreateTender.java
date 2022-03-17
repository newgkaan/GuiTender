package gui.tender.inventory;

import gui.tender.Main;
import gui.tender.json.JWriter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateTender {

    private final File path;
    public CreateTender(File path) {
        this.path = path;
    }

    private Inventory tender;
    public void create() {
        tender = Bukkit.createInventory(null, 36, "Aktif Ihaleler");
        ca();
        cb();

        Main.tender.setInventoryTender(tender);
    }

    private void ca() {
        ItemStack stack = new ItemStack(Material.ARROW);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("§3< Geri Dön");
        List<String> list = new ArrayList<>();
        list.add("§7Tıkla ve bir önceki menüye dön");
        meta.setLore(list);
        stack.setItemMeta(meta);
        tender.setItem(27, stack);
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
