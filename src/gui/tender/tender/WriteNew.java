package gui.tender.tender;

import gui.tender.json.IFormat;
import gui.tender.json.JReader;
import gui.tender.json.JWriter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;

@SuppressWarnings("unchecked")
public class WriteNew {
    private final ItemStack stack;
    private final String player;
    private final int start;
    private final String path;
    public WriteNew(ItemStack stack, String player, int start, String path) {
        this.stack = stack;
        this.player = player;
        this.start = start;
        this.path = path;
    }
    public void write(TenderID tenderId) {
        try {
            tenderId.add();
            File aa0 = new File(path + "/current.yml");
            FileConfiguration aa1 = YamlConfiguration.loadConfiguration(aa0);
            aa1.set("current", tenderId.get());
            aa1.save(aa0);



            JSONObject obj0 = new JSONObject();
            obj0.put("stack", new IFormat().to64(stack));
            obj0.put("player", player);
            obj0.put("start", start);
            obj0.put("id", tenderId.get());
            obj0.put("time", 900000);
            obj0.put("bid", "null");
            obj0.put("finish", 0L);
            obj0.put("status", "free");
            obj0.put("statustimer", 0);


            File check = new File(path + "/data.json");
            if (check.length() != 0) {
                JSONArray jsonArray = new JReader().read(path + "/data.json");
                jsonArray.add(obj0);

                new JWriter(jsonArray, path + "/data.json", JWriter.JWriteOption.FLUSH).write();
            } else {
                JSONArray jsonArray = new JSONArray();
                jsonArray.add(obj0);
                new JWriter(jsonArray, path + "/data.json", JWriter.JWriteOption.FLUSH).write();
            }

        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
            Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("GuiTender"));
        }
    }
}
