package gui.tender.tender;

import gui.tender.json.IFormat;
import gui.tender.json.JReader;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.*;

@SuppressWarnings({"deprecation", "unchecked"})
public class OpenOwn {
    private final Player player;
    private final String path;
    public OpenOwn(Player player, String path) {
        this.player = player;
        this.path = path;
    }
    public void open() {
        try {
            Inventory inv = openOwnTenderA();

            File check = new File(path + "/data.json");
            if (check.length() == 0) {
                player.openInventory(inv);
                return;
            }


            JSONArray jsonArray = new JReader().read(path + "/data.json");
            JSONArray jsonPlayer = new JSONArray();

            for (Object o : jsonArray) {
                JSONObject obj0 = (JSONObject) o;
                if (obj0.get("player").equals(player.getName())) {
                    jsonPlayer.add(obj0);
                }
            }



            for (Object o : jsonPlayer) {
                JSONObject obj0 = (JSONObject) o;

                String severe = (String) obj0.get("stack");
                ItemStack stack = new IFormat().from64(severe);
                if (stack == null) continue;
                ItemMeta meta = stack.getItemMeta();
                meta.setDisplayName(null);
                List<String> list = new ArrayList<>();
                list.add(" ");
                list.add("§eID: §f" + obj0.get("id"));
                list.add("§eSatıcı: §f" + obj0.get("player"));
                list.add("§eBaşlangıç Fiyatı: §6" + obj0.get("start"));


                String bid = (String) obj0.get("bid");
                if (bid.equals("null")) {
                    list.add("§eTeklif Veren: §fYok");
                    list.add("§eŞuanki Fiyat: §d" + "0");
                } else {
                    list.add("§eTeklif Veren: §f" + bid);
                    list.add("§eŞuanki Fiyat: §d" + obj0.get("finish"));
                }


                GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("US/Central"));
                calendar.setTimeInMillis((Long) obj0.get("time"));
                Date date = calendar.getTime();
                String sure = date.getMinutes() + "dk " + date.getSeconds() + "sn";
                list.add("§eSüre: §f" + sure + " §7(Güncellenmedi)");
                list.add(" ");

                if (date.getMinutes() < 14) {
                    list.add("§cIptal etmek için çok geç");
                } else {
                    list.add("§aIptal etmek için tıkla");
                }

                meta.setLore(list);
                stack.setItemMeta(meta);
                inv.addItem(stack);
            }


            player.openInventory(inv);
        } catch (Exception ignored) {
            player.closeInventory();
            player.sendMessage("§cİhale menüsü yüklenirken bir hata oluştu.");
        }
    }
    private Inventory openOwnTenderA() {
        Inventory inv = Bukkit.createInventory(null, 36, "Senin Ihalelerin");
        ItemStack stack = new ItemStack(Material.ARROW);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("§3< Geri Dön");
        List<String> list = new ArrayList<>();
        list.add("§7Tıkla ve bir önceki menüye dön");
        meta.setLore(list);
        stack.setItemMeta(meta);
        inv.setItem(27, stack);
        return inv;
    }
}
