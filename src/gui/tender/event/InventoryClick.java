package gui.tender.event;

import gui.tender.Main;
import gui.tender.json.IFormat;
import gui.tender.json.JReader;
import gui.tender.json.JWriter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

public class InventoryClick implements Listener {
    
    private final File path;
    private final Inventory inventoryMain;
    private final Economy economy;
    public InventoryClick(File path, Inventory inventoryMain, Economy economy) {
        this.path = path;
        this.inventoryMain = inventoryMain;
        this.economy = economy;
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(InventoryClickEvent e) {
        try {
            if (e.getClickedInventory() == null) return;
            String inv_title = Main.vHandler.get_title_inventory(new Object[]{e.getWhoClicked(), e.getClickedInventory()});


            if (inv_title.contains("Teklif Veriliyor...")) {
                e.setCancelled(true);
                Main.tender.updateOffer(e);
                return;
            }

            switch (inv_title) {
                case "Ihale": {
                    e.setCancelled(true);

                    ItemStack stack = e.getCurrentItem();
                    if (stack == null) return;
                    String name = stack.getItemMeta().getDisplayName();

                    switch (name) {
                        case "§aMevcut ihaleler":
                            e.getWhoClicked().openInventory(Main.tender.getInventoryTender());
                            break;
                        case "§aKutun":
                            Main.tender.openBox((Player) e.getWhoClicked());
                            break;
                        case "§aSenin ihalelerin":
                            Main.tender.openOwnTender((Player) e.getWhoClicked());
                            break;
                        case "§aIhale Geçmişi":
                            e.getWhoClicked().sendMessage("§cBu özellik bir sonraki patchda eklenecektir");
                            break;
                    }
                    break;
                }
                case "Ihale Kutun": {
                    e.setCancelled(true);
                    ItemStack stack = e.getCurrentItem();
                    String name = stack.getItemMeta().getDisplayName();
                    if (name != null && name.equalsIgnoreCase("§3< Geri Dön")) {
                        e.getWhoClicked().openInventory(inventoryMain);
                        break;
                    }

                    if (e.getWhoClicked().getInventory().firstEmpty() == -1) {
                        e.getWhoClicked().sendMessage("§cEnvanterinde boş yer yok");
                        break;
                    }

                    List<String> list = stack.getItemMeta().getLore();
                    String id = null;
                    for (String s : list) {
                        if (s.contains("§eID:")) {
                            id = s;
                            break;
                        }
                    }
                    if (id == null) {
                        e.getWhoClicked().closeInventory();
                        e.getWhoClicked().sendMessage("§cIhale menüsünde bir sorun oluştu");
                        break;
                    }


                    try {
                        id = id.replaceAll("§eID: §f", "").toLowerCase();
                        Long id0 = Long.parseLong(id);

                        JSONArray jsonArray = new JReader().read(path + "/box/" + e.getWhoClicked().getName() + ".json");


                        JSONObject obj0 = null;
                        for (Object var : jsonArray) {
                            JSONObject var1 = (JSONObject) var;
                            Long var2 = (Long) var1.get("id");
                            if (id0.equals(var2)) {
                                obj0 = var1;
                                break;
                            }
                        }

                        if (obj0 == null) {
                            e.getWhoClicked().closeInventory();
                            e.getWhoClicked().sendMessage("§cIhale menüsünde bir sorun oluştu");
                            break;
                        }

                        jsonArray.remove(obj0);

                        new JWriter(jsonArray, path + "/box/" + e.getWhoClicked().getName() + ".json", JWriter.JWriteOption.CLOSE).write();

                        String kutu_sahibi = e.getWhoClicked().getName();
                        String bitis = (String) obj0.get("bid");

                        if (kutu_sahibi.equalsIgnoreCase(bitis)) {
                            String severe = (String) obj0.get("stack");
                            ItemStack stack0 = new IFormat().from64(severe);
                            Main.tender.openBox((Player) e.getWhoClicked());
                            e.getWhoClicked().getInventory().addItem(stack0);
                            e.getWhoClicked().sendMessage("§f" + obj0.get("id") + " ID'li ihalenin eşyasını aldın.");
                        } else {
                            Long finish = (Long) obj0.get("finish");
                            Main.tender.openBox((Player) e.getWhoClicked());
                            economy.depositPlayer((Player) e.getWhoClicked(), finish);
                            e.getWhoClicked().sendMessage("§f" + obj0.get("id") + " ID'li ihalenin parasını aldın.");
                        }





                    } catch (Exception ignored) {
                        e.getWhoClicked().closeInventory();
                        e.getWhoClicked().sendMessage("§cIhale menüsünde bir sorun oluştu");
                    }
                    break;
                }
                case "Senin Ihalelerin": {
                    e.setCancelled(true);
                    ItemStack stack = e.getCurrentItem();
                    String name = stack.getItemMeta().getDisplayName();
                    if (name != null && name.equalsIgnoreCase("§3< Geri Dön")) {
                        e.getWhoClicked().openInventory(inventoryMain);
                        break;
                    }


                    List<String> list = stack.getItemMeta().getLore();


                    boolean status = false;



                    String id = null;
                    for (String s : list) {
                        if (s.contains("§eID:")) {
                            id = s;
                        }
                        if (s.contains("§aIptal etmek için tıkla")) {
                            status = true;
                        }
                    }
                    if (!status) {
                        break;
                    }

                    if (id == null) {
                        e.getWhoClicked().closeInventory();
                        e.getWhoClicked().sendMessage("§cIhale menüsünde bir sorun oluştu");
                        break;
                    }

                    try {
                        id = id.replaceAll("§eID: §f", "").toLowerCase();
                        Long id0 = Long.parseLong(id);

                        JSONArray jsonArray = new JReader().read(path + "/data.json");


                        JSONObject obj0 = null;
                        for (Object var : jsonArray) {
                            JSONObject var1 = (JSONObject) var;
                            Long var2 = (Long) var1.get("id");
                            if (id0.equals(var2)) {
                                obj0 = var1;
                                break;
                            }
                        }
                        if (obj0 == null) break;

                        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("US/Central"));
                        calendar.setTimeInMillis(((Long) obj0.get("time")));
                        Date date = calendar.getTime();

                        //noinspection deprecation
                        if (date.getMinutes() < 14) {
                            Main.tender.openBox((Player) e.getWhoClicked());
                            break;
                        }

                        if (!obj0.get("bid").equals("null")) {
                            e.getWhoClicked().sendMessage("§cIptal etmeye çalıştığın ihaleye çoktan teklif verilmiş");
                            break;
                        }


                        e.getWhoClicked().sendMessage("§aIhale iptal edildi eşyanı depondan alabilirsin");
                        Main.tender.addItemToBox(obj0);


                        jsonArray.remove(obj0);
                        new JWriter(jsonArray, path + "/data.json", JWriter.JWriteOption.FLUSH);

                        Main.tender.openOwnTender((Player) e.getWhoClicked());



                    } catch (Exception ignored) {
                        e.getWhoClicked().closeInventory();
                        e.getWhoClicked().sendMessage("§cIhale menüsünde bir sorun oluştu");
                    }






                    break;
                }
                case "Aktif Ihaleler": {
                    e.setCancelled(true);
                    ItemStack stack = e.getCurrentItem();
                    String name = stack.getItemMeta().getDisplayName();
                    if (name != null && name.equalsIgnoreCase("§3< Geri Dön")) {
                        e.getWhoClicked().openInventory(inventoryMain);
                        break;
                    }


                    List<String> list = stack.getItemMeta().getLore();

                    String id = null;
                    for (String var : list) {
                        if (var.contains("§eID:")) {
                            id = var;
                            break;
                        }
                    }
                    if (id == null) break;
                    try {
                        id = id.replaceAll("§eID: §f", "");
                        Long id0 = Long.parseLong(id);


                        JSONArray jsonArray = new JReader().read(path + "/data.json");
                        JSONObject obj0 = null;
                        for (Object o : jsonArray) {
                            JSONObject obj = (JSONObject) o;
                            Long var = (Long) obj.get("id");
                            if (var.equals(id0)) {
                                obj0 = obj;
                                break;
                            }
                        }
                        if (obj0 == null) break;


                        String player = (String) obj0.get("player");
                        String bid = (String) obj0.get("bid");
                        if (player.equals(e.getWhoClicked().getName())) break;
                        if (bid.equals(e.getWhoClicked().getName())) break;

                        String status = (String) obj0.get("status");
                        if (status.equals("full") || status.equals("ending")) break;
                        Main.tender.makeOffer(obj0, (Player) e.getWhoClicked());



                    } catch (Exception ignored) {
                        break;
                    }
                    break;
                }




            }
        } catch (Exception ignored) {}
    }
}
