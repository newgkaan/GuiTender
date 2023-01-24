package gui.tender.tender;

import gui.tender.inventory.CreateHistory;
import gui.tender.json.IFormat;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class History {
    private final Inventory inventory;
    public History() {
        inventory = new CreateHistory().create();
        update();
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    List<JSONObject> list = new ArrayList<>();
    public void add(JSONObject jsonObject) {
        if(list.size() >= 45) {
            list.remove(1);
        }
        list.add(jsonObject);

    }
    @SuppressWarnings("BusyWait")
    public void update() {
        boolean[] cancel = {false};
        Thread thread = new Thread(() -> {
            try {
                while (!cancel[0]) {

                    List<JSONObject> update = new ArrayList<>(list);
                    Collections.reverse(update);

                    for (int var = 0 ; var < 45 ; var++) {
                        if (update.size() <= var) {
                            break;
                        }
                        JSONObject jsonObject = update.get(var);
                        ItemStack stack = new IFormat().from64((String) jsonObject.get("stack"));
                        ItemMeta meta = stack.getItemMeta();
                        meta.setDisplayName(null);
                        List<String> list = new ArrayList<>();
                        list.add("");
                        list.add("§eID: §f" + jsonObject.get("id"));
                        list.add("§eSatıcı: §f" + jsonObject.get("player"));
                        list.add("§eBaşlangıç Fiyatı: §6" + jsonObject.get("start"));

                        String bid = (String) jsonObject.get("bid");
                        if (bid.equalsIgnoreCase("null")) {
                            list.add("§eTeklif Veren: §fYok");
                            list.add("§eFiyat: §d0");
                        } else {
                            list.add("§eTeklif Veren: §f" + bid);
                            list.add("§eFiyat: §d" + jsonObject.get("finish"));
                        }
                        list.add(" ");
                        meta.setLore(list);
                        stack.setItemMeta(meta);

                        inventory.setItem(var, stack);
                        Thread.sleep(100);
                    }
                    Thread.sleep(35000);
                }
            } catch (Exception ignored) {
                cancel[0] = true;
                update();
            }
        });
        thread.start();
    }



}
