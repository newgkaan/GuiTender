package gui.tender.tender;

import gui.tender.itemstack.ISCreate;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OpenBox {
    private final Player player;
    private final String path;
    public OpenBox(Player player, String path) {
        this.player = player;
        this.path = path;
    }
    public void open() {
        try {
            Inventory inv = openBoxA();
            File check = new File(path + "/box/" + player.getName() + ".json");


            if (check.length() == 0) {
                player.openInventory(inv);
            } else {
                JSONArray jsonArray = new JReader().read(path + "/box/" + player.getName() + ".json");

                int count = 0;
                for (Object o : jsonArray) {

                    if (count >= 27) break;


                    JSONObject obj0 = (JSONObject) o;

                    String bid = (String) obj0.get("bid");
                    if (bid.equalsIgnoreCase(player.getName()) || bid.equalsIgnoreCase("null")) {
                        String severe = (String) obj0.get("stack");
                        ItemStack stack = new IFormat().from64(severe);
                        if (stack == null) continue;


                        inv.setItem(count, new ISCreate().createB(stack, null, Arrays.asList(
                                " ", "§eID: §f" + obj0.get("id"), "§eSatıcı: §f" + obj0.get("player"),
                                " ", "§eAlmak için tıkla"
                        )));
                    } else {

                        inv.setItem(count, new ISCreate().createA(Material.GOLD_INGOT, obj0.get("finish").toString(), Arrays.asList(
                                " ", "§eID: §f" + obj0.get("id"), "§eSatın Alan: §f" + bid, " ", "§eAlmak için tıkla"
                        )));
                    }
                    count++;
                }

                player.openInventory(inv);
            }
        } catch (Exception ignored){
            player.sendMessage("§cİhale menüsü yüklenirken bir hata oluştu.");
            player.closeInventory();
        }
    }
    private Inventory openBoxA() {

        Inventory inv = Bukkit.createInventory(null, 36, "Ihale Kutun");

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
