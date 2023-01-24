package gui.tender.tender;

import gui.tender.json.IFormat;
import gui.tender.json.JReader;
import gui.tender.json.JWriter;
import gui.tender.version.VHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

@SuppressWarnings({"unchecked", "deprecation"})
public class UpdateOffer {
    private final InventoryClickEvent e;
    private final VHandler vHandler;
    private final String path;
    private final Economy economy;
    private final Inventory inventoryTender;
    public UpdateOffer(InventoryClickEvent e, VHandler vHandler, String path, Economy economy, Inventory inventoryTender) {
        this.e = e;
        this.vHandler = vHandler;
        this.path = path;
        this.economy = economy;
        this.inventoryTender = inventoryTender;
    }
    public void update() {
        try {
            ItemStack stack = e.getCurrentItem();
            if (stack == null) return;

            String name = stack.getItemMeta().getDisplayName();


            String id = vHandler.get_title_inventory(new Object[]{e.getWhoClicked(), e.getClickedInventory()});
            id = id.replaceAll("Teklif Veriliyor... ", "");
            Long id0 = Long.parseLong(id);

            JSONArray jsonArray = new JReader().read(path + "/data.json");

            JSONObject obj0 = null;
            for (Object o : jsonArray) {
                JSONObject var = (JSONObject) o;
                if (id0.equals(var.get("id"))) {
                    obj0 = var;
                    break;
                }
            }
            if (obj0 == null) {
                e.getWhoClicked().openInventory(inventoryTender);
                return;
            }

            if (obj0.get("status").equals("full")) {
                e.getWhoClicked().openInventory(inventoryTender);
                return;
            }



            ItemStack fav = e.getClickedInventory().getItem(13);
            long current = 0L;
            for (String var : fav.getItemMeta().getLore()) {
                if (var.contains("§eŞuanki Fiyat:")) {
                    var = var.replace("§eŞuanki Fiyat: §d", "");
                    current = Long.parseLong(var);
                    break;
                }
            }

            long finish = current;
            boolean update = false;
            boolean onayla = false;
            switch (name) {
                case "§a+100":
                    finish += 100;
                    update = true;
                    break;
                case "§a+500":
                    finish += 500;
                    update = true;
                    break;
                case "§a+1.000":
                    finish += 1000;
                    update = true;
                    break;
                case "§c-100":
                    finish -= 100;
                    update = true;
                    break;
                case "§c-500":
                    finish -= 500;
                    update = true;
                    break;
                case "§c-1.000":
                    finish -= 1000;
                    update = true;
                    break;
                case "§cIptal":
                    e.getWhoClicked().openInventory(inventoryTender);
                    break;
                case "§aOnayla":
                    onayla = true;
                    update = true;
                    break;

            }

            if (update) {



                if (onayla) {

                    if (finish <= (Long) obj0.get("start")) {
                        e.getWhoClicked().sendMessage("§cTeklif başlangıç fiyatından düşük olamaz");
                        return;
                    }

                    if (!economy.has((Player) e.getWhoClicked(), finish)) {
                        e.getWhoClicked().sendMessage("§cTeklif vermek için yeterli paran yok");
                        return;
                    }



                    String bid = (String) obj0.get("bid");
                    Long offer = (Long) obj0.get("finish");

                    if (bid.equals(e.getWhoClicked().getName())) {
                        e.getWhoClicked().sendMessage("§cİhaleye zaten teklif vermişsin");
                        return;
                    }


                    if (finish <= offer) {
                        e.getWhoClicked().sendMessage("§cBir önceki teklifçi senden yüksek fiyat biçmiş.");
                        return;
                    }



                    if (!bid.equals("null")) {
                        economy.depositPlayer(bid, offer);
                        Player var = Bukkit.getPlayer(bid);
                        if (var != null) var.sendMessage("§e" + id0 + " ID'li ihaleye birisi senden yüksek teklif verdiği için teklifin iptal edildi.");
                    }



                    int index = jsonArray.indexOf(obj0);
                    obj0.put("bid", e.getWhoClicked().getName());
                    obj0.put("finish", finish);
                    obj0.put("status", "full");
                    obj0.put("statusTimer", 3);
                    jsonArray.set(index, obj0);

                    new JWriter(jsonArray, path + "/data.json", JWriter.JWriteOption.FLUSH).write();






                    e.getWhoClicked().openInventory(inventoryTender);
                    e.getWhoClicked().sendMessage("§a" + id0 + " ID'li ihaleye başarıyla teklif verdin.");
                    economy.withdrawPlayer((Player) e.getWhoClicked(), finish);
                    return;
                }

                obj0.put("bid", e.getWhoClicked().getName());
                obj0.put("finish", finish);


                String severe = (String) obj0.get("stack");
                ItemStack stack1 = new IFormat().from64(severe);
                if (stack1 == null) return;
                ItemMeta meta = stack1.getItemMeta();
                meta.setDisplayName(null);
                List<String> list = new ArrayList<>();

                list.add(" ");
                list.add("§eID: §f" + obj0.get("id"));
                list.add("§eSatıcı: §f" + obj0.get("player"));
                list.add("§eBaşlangıç Fiyatı: §6" + obj0.get("start"));


                String bid = (String) obj0.get("bid");
                list.add("§eTeklif Veren: §f" + bid);
                list.add("§eŞuanki Fiyat: §d" + finish);



                GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("US/Central"));
                calendar.setTimeInMillis((Long) obj0.get("time"));
                Date date = calendar.getTime();
                String sure = date.getMinutes() + "dk " + date.getSeconds() + "sn";
                list.add("§eSüre: §f" + sure + " §7(Güncellenmedi)");
                list.add(" ");

                meta.setLore(list);
                stack1.setItemMeta(meta);
                e.getClickedInventory().setItem(13, stack1);
            }


        } catch (Exception ignored) {}
    }
}
