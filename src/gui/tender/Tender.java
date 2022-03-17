package gui.tender;

import gui.tender.json.IFormat;
import gui.tender.json.JReader;
import gui.tender.json.JWriter;
import gui.tender.version.VHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.*;

@SuppressWarnings({"unchecked", "deprecation"})
public class Tender {

    private Inventory inventoryTender;
    private final Inventory inventoryMain;
    private final VHandler vHandler;
    private final Economy economy;
    private final File path;
    public Tender(File path, Economy economy, VHandler vHandler, Inventory inventoryMain) {
        this.path = path;
        this.economy = economy;
        this.vHandler = vHandler;
        this.inventoryMain = inventoryMain;
        
        readCurrent();
    }
    
    public void readCurrent() {
        File file = new File(path + "/current.yml");
        if (!file.exists()) return;
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        if (!configuration.isSet("current")) return;
        current = configuration.getLong("current");
    }
    public Inventory getInventoryTender() {
        return inventoryTender;
    }
    public void setInventoryTender(Inventory inventoryTender) {
        this.inventoryTender = inventoryTender;
    }
    private long current = 0;
    public synchronized void writeNewTender(ItemStack stack, String player, int start_balance) {
        try {
            current++;
            File aa0 = new File(path + "/current.yml");
            FileConfiguration aa1 = YamlConfiguration.loadConfiguration(aa0);
            aa1.set("current", current);
            aa1.save(aa0);



            JSONObject obj0 = new JSONObject();
            obj0.put("stack", new IFormat().to64(stack));
            obj0.put("player", player);
            obj0.put("start", start_balance);
            obj0.put("id", current);
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
    public void updateOffer(InventoryClickEvent e) {
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
    public void openOwnTender(Player player) {
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
    public ArrayList<ItemStack> updateTenderItems() {
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
                    addItemToBox(var1);
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
    public void makeOffer(JSONObject obj0, Player player) {
        try {
            Inventory inv = Bukkit.createInventory(null, 54, "Teklif Veriliyor... " + obj0.get("id"));

            ItemStack a100 = new ItemStack(Material.GOLD_INGOT);
            a100.setAmount(1);
            ItemMeta a100_meta = a100.getItemMeta();
            a100_meta.setDisplayName("§a+100");
            a100.setItemMeta(a100_meta);


            ItemStack a500 = new ItemStack(Material.GOLD_INGOT);
            a500.setAmount(2);
            ItemMeta a500_meta = a500.getItemMeta();
            a500_meta.setDisplayName("§a+500");
            a500.setItemMeta(a500_meta);


            ItemStack a1000 = new ItemStack(Material.GOLD_INGOT);
            a1000.setAmount(3);
            ItemMeta a1000_meta = a1000.getItemMeta();
            a1000_meta.setDisplayName("§a+1.000");
            a1000.setItemMeta(a1000_meta);


            ItemStack e100 = new ItemStack(Material.GOLD_INGOT);
            e100.setAmount(1);
            ItemMeta e100_meta = e100.getItemMeta();
            e100_meta.setDisplayName("§c-100");
            e100.setItemMeta(e100_meta);


            ItemStack e500 = new ItemStack(Material.GOLD_INGOT);
            e500.setAmount(2);
            ItemMeta e500_meta = e500.getItemMeta();
            e500_meta.setDisplayName("§c-500");
            e500.setItemMeta(e500_meta);


            ItemStack e1000 = new ItemStack(Material.GOLD_INGOT);
            e1000.setAmount(3);
            ItemMeta e1000_meta = e1000.getItemMeta();
            e1000_meta.setDisplayName("§c-1.000");
            e1000.setItemMeta(e1000_meta);


            inv.setItem(33, a100);
            inv.setItem(34, a500);
            inv.setItem(35, a1000);
            inv.setItem(27, e1000);
            inv.setItem(28, e500);
            inv.setItem(29, e100);


            ItemStack ccc = vHandler.get_cam();
            ItemMeta ccc_meta = ccc.getItemMeta();
            ccc_meta.setDisplayName(" ");
            ccc.setItemMeta(ccc_meta);
            for (int var = 9; var <= 17; var++) {
                inv.setItem(var, ccc);
            }

            ItemStack onayla = vHandler.get_onayla();
            ItemMeta onayla_meta = onayla.getItemMeta();
            onayla_meta.setDisplayName("§aOnayla");
            onayla.setItemMeta(onayla_meta);

            ItemStack iptal = vHandler.get_iptal();
            ItemMeta iptal_meta = iptal.getItemMeta();
            iptal_meta.setDisplayName("§cIptal");
            iptal.setItemMeta(iptal_meta);

            inv.setItem(39, iptal);
            inv.setItem(41, onayla);

            String severe = (String) obj0.get("stack");
            ItemStack stack = new IFormat().from64(severe);
            if (stack == null) return;
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

            meta.setLore(list);
            stack.setItemMeta(meta);
            inv.setItem(13, stack);


            player.openInventory(inv);
        } catch (Exception ignored) {
            player.openInventory(inventoryMain);
        }
    }
    public void addItemToBox(JSONObject var) {
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
    public void openBox(Player player) {
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
                    if (bid.equalsIgnoreCase(player.getName())) {
                        String severe = (String) obj0.get("stack");
                        ItemStack stack = new IFormat().from64(severe);
                        if (stack == null) continue;
                        ItemMeta meta = stack.getItemMeta();
                        meta.setDisplayName(null);
                        List<String> list = new ArrayList<>();
                        list.add(" ");
                        list.add("§eID: §f" + obj0.get("id"));
                        list.add("§eSatıcı: §f" + obj0.get("player"));
                        list.add(" ");
                        list.add("§eAlmak için tıkla");
                        meta.setLore(list);
                        stack.setItemMeta(meta);

                        inv.setItem(count, stack);
                    } else {
                        ItemStack stack = new ItemStack(Material.GOLD_INGOT);
                        ItemMeta meta = stack.getItemMeta();
                        meta.setDisplayName(obj0.get("finish").toString());
                        List<String> list = new ArrayList<>();
                        list.add(" ");
                        list.add("§eID: §f" + obj0.get("id"));
                        list.add("§eSatın Alan: §f" + bid);
                        list.add(" ");
                        list.add("§eAlmak için tıkla");
                        meta.setLore(list);
                        stack.setItemMeta(meta);
                        inv.setItem(count, stack);
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
