package gui.tender.tender;

import gui.tender.json.JReader;
import gui.tender.json.JWriter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;

@SuppressWarnings("unchecked")
public class AddItemBox {
    private final JSONObject var;
    private final String path;
    public AddItemBox(JSONObject var, String path) {
        this.var = var;
        this.path = path;
    }
    public void add() {
        try {

            String bid = (String) var.get("bid");
            String player = (String) var.get("player");

            boolean cancel = false;
            if (!bid.equals("null")) {
                File check = new File(path + "/box/" + bid + ".json");
                if (check.length() != 0) {
                    JSONArray jsonArray = new JReader().read(path + "/box/" + bid + ".json");
                    jsonArray.add(var);
                    new JWriter(jsonArray, path + "/box/" + bid + ".json", JWriter.JWriteOption.CLOSE).write();


                } else {
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.add(var);
                    new JWriter(jsonArray, path + "/box/" + bid + ".json", JWriter.JWriteOption.CLOSE).write();
                }
            } else {
                Player p = Bukkit.getPlayer(player);
                if (p != null) {
                    p.sendMessage("§e" + var.get("id") + " ID'li ihalene kimse teklif vermediği için iptal edildi, eşyanı depondan alabilirsin.");
                    cancel = true;
                }
            }
            File check = new File(path + "/box/" + player + ".json");
            if (!check.exists()) //noinspection ResultOfMethodCallIgnored
                check.createNewFile();
            if (check.length() != 0) {

                JSONArray jsonArray = new JReader().read(path + "/box/" + player + ".json");
                jsonArray.add(var);
                new JWriter(jsonArray, path + "/box/" + player + ".json", JWriter.JWriteOption.CLOSE).write();

            } else {
                JSONArray jsonArray = new JSONArray();
                jsonArray.add(var);
                new JWriter(jsonArray, path + "/box/" + player + ".json", JWriter.JWriteOption.CLOSE).write();
            }


            Player p = Bukkit.getPlayer(player);
            Player p1 = Bukkit.getPlayer(bid);
            if (p != null && !cancel) {
                p.sendMessage("§a" + var.get("id") + " ID'li ihalen sona erdi, paranı depondan alabilirsin.");
            }
            if (p1 != null) {
                p1.sendMessage("§a" + var.get("id") + " ID'li ihale sona erdi, eşyanı depondan alabilirsin.");
            }

        } catch (Exception ignored) {
        }
    }
}
