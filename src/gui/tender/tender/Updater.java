package gui.tender.tender;

import gui.tender.Main;
import gui.tender.json.IFormat;
import gui.tender.json.JReader;
import gui.tender.json.JWriter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.*;

@SuppressWarnings({"unchecked", "deprecation"})
public class Updater {
    private final String path;
    public Updater(String path) {
        this.path = path;
    }
    public ArrayList<ItemStack> getUpdates() {
        try {
            File check = new File(path + "/data.json");
            if (check.length() == 0) return null;
            final ArrayList<ItemStack> arrayList = new ArrayList<>();

            JSONArray jsonArray = new JReader().read(path + "/data.json");


            for (int var = 0; var < 27; var++) {
                if (jsonArray.size() - 1 < var) {
                    break;
                }

                JSONObject var1 = (JSONObject) jsonArray.get(var);
                String severe = (String) var1.get("stack");
                ItemStack stack = new IFormat().from64(severe);
                if (stack == null) continue;
                ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName(null);
                List<String> list = new ArrayList<>();
                list.add(" ");
                list.add("§eID: §f" + var1.get("id"));
                list.add("§eSatıcı: §f" + var1.get("player"));
                list.add("§eBaşlangıç Fiyatı: §f" + var1.get("start"));
                if ((var1.get("bid")).equals("null")) {
                    list.add("§eTeklif Veren: §fYok");
                } else {
                    list.add("§eTeklif Veren: §f" + var1.get("bid"));
                }
                list.add("§eŞuanki Fiyat: §d" + var1.get("finish"));

                Long time= (Long) var1.get("time");
                time -= 1000;
                var1.put("time", time);
                jsonArray.set(var, var1);
                if (time <= 0) {
                    jsonArray.remove(var1);
                    new AddItemBox(var1, path).add();
                    Main.tender.getHistory().add(var1);
                }

                GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("US/Central"));
                calendar.setTimeInMillis(((Long) var1.get("time")));
                Date date = calendar.getTime();
                @SuppressWarnings("deprecation") String sure = date.getMinutes() + "dk " + date.getSeconds() + "sn";

                if (date.getMinutes() == 0 && date.getSeconds() < 10) {
                    var1.put("status", "ending");
                }

                list.add("§eSüre: §f" + sure);
                list.add(" ");
                String status = (String) var1.get("status");
                if (Objects.equals(status, "free")) {
                    list.add("§aTeklif vermek için tıkla");
                } else if (Objects.equals(status, "full")) {
                    list.add("§6Başka birisi teklif vermiş");

                    Long statusTimer = (Long) var1.get("statusTimer");
                    statusTimer--;
                    if (statusTimer <= 0) {
                        var1.put("status", "free");
                        var1.put("statusTimer", 0);
                    } else {
                        var1.put("statusTimer", statusTimer);
                    }

                } else if (Objects.equals(status, "ending")) {
                    list.add("§cTeklif vermek için çok geç");
                }
                list.add("");
                meta.setLore(list);
                stack.setItemMeta(meta);
                arrayList.add(stack);
            }

            new JWriter(jsonArray, path + "/data.json", JWriter.JWriteOption.FLUSH).write();
            return arrayList;
        } catch (Exception e) {
            System.err.println("Exception handled on reading 'data.json' file. Match will be return empty");
            Bukkit.getPluginManager().disablePlugin(Bukkit.getPluginManager().getPlugin("GuiTender"));
        }

        return null;
    }

}
