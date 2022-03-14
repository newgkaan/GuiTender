package gui.tender;

import gui.tender.vs.VUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class Main extends JavaPlugin implements Listener, CommandExecutor {

    private Inventory inv_main;
    private Inventory inv_ihale;
    private Economy economy;


    private final Map<UUID, Boolean> cooldown = new HashMap<>();


    private VUtils vUtils;

    public void onEnable() {
        vUtils = new VUtils(getServer().getVersion());
        a();
        b();
        c();

        getServer().getPluginManager().registerEvents(this, this);
        getCommand("ihale").setExecutor(this);

        checkUpdates();
    }

    private String version = "0.1";
    private void checkUpdates() {
        try {
            URL link = new URL("https://raw.githubusercontent.com/kYaaaz/version-check/main/guiTender");
            URLConnection connection = link.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String var;
            while ((var = reader.readLine()) != null)
                if (var.contains(version)) hasUpdate = false;
            reader.close();
        } catch (Exception ignored) {
            getLogger().severe("Can't connect version check...");
        }
    }

    boolean hasUpdate = true;
    @EventHandler
    public void player_join(PlayerJoinEvent e) {
        if (!e.getPlayer().isOp()) return;
        if (!hasUpdate) return;
        e.getPlayer().sendMessage(" ");
        e.getPlayer().sendMessage(" ");
        e.getPlayer().sendMessage("§c>>> §eGuiTender ihale eklentisi için güncelleme mevcut:");
        e.getPlayer().sendMessage("§c>>> Link: §fhttps://github.com/kYaaaz/GuiTender");
        e.getPlayer().sendMessage(" ");
        e.getPlayer().sendMessage(" ");
    }



    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Bu komut sadece oyuncular içindir!");
            return true;
        }

        if (args.length < 1) {
            ((Player) sender).openInventory(inv_main);
        } else {
            if (args.length != 2) return true;
            if (!args[0].equalsIgnoreCase("başlat") || args[0].equalsIgnoreCase("baslat")) {
                return true;
            }


            if (cooldown.getOrDefault(((Player) sender).getUniqueId(), false)) {
                sender.sendMessage("§c15 saniyede bir ihale başlatabilirsin.");
                return true;
            }




            int balance = 0;
            try {
                balance = Integer.parseInt(args[1]);
            } catch (Exception ignored) {}
            if (balance == 0) {
                sender.sendMessage("§cHatalı değer girdin.");
                return true;
            }
            if (balance < 0) {
                sender.sendMessage("§cHatalı değer girdin.");
                return true;
            }

            Player player = (Player) sender;
            ItemStack stack = player.getItemInHand().clone();
            if (stack == null || stack.getType().equals(Material.AIR)) {
                player.sendMessage("§cÖnce eline izin verilen eşyalardan birisini almalısın.");
                return true;
            }
            player.setItemInHand(null);



            Bukkit.broadcastMessage("§c>> §9" + player.getName() + " §ayeni bir ihale başlattı.");
            ItemStack var = stack.clone();
            ItemMeta var1 = var.getItemMeta();
            var1.setDisplayName(null);
            var.setItemMeta(var1);
            Bukkit.broadcastMessage("§c>> " + var.getType());
            write_new_ihale(stack, player.getName(), balance);


            cooldown.put(player.getUniqueId(), true);
            new BukkitRunnable() {
                public void run() {
                    cooldown.remove(player.getUniqueId());
                }
            }.runTaskLater(this, 300L);
            return true;
        }
        return true;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void inventory_click(InventoryClickEvent e) {
        try {
            if (e.getClickedInventory() == null) return;
            String inv_title = vUtils.get_title_inventory(new Object[]{e.getWhoClicked(), e.getClickedInventory()});


            if (inv_title.contains("Teklif Veriliyor...")) {
                e.setCancelled(true);
                update_offer(e);
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
                            e.getWhoClicked().openInventory(inv_ihale);
                            break;
                        case "§aKutun":
                            open_box((Player) e.getWhoClicked());
                            break;
                        case "§aSenin ihalelerin":
                            open_your_ihale((Player) e.getWhoClicked());
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
                        e.getWhoClicked().openInventory(inv_main);
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

                        JSONParser js = new JSONParser();
                        FileReader reader = new FileReader(getDataFolder() + "/box/" + e.getWhoClicked().getName() + ".json");
                        JSONArray js_list = (JSONArray) js.parse(reader);

                        JSONObject obj0 = null;
                        for (Object var : js_list) {
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

                        js_list.remove(obj0);
                        FileWriter writer = new FileWriter(getDataFolder() + "/box/" + e.getWhoClicked().getName() + ".json");
                        writer.write(js_list.toJSONString());
                        writer.close();

                        String kutu_sahibi = e.getWhoClicked().getName();
                        String bitis = (String) obj0.get("bid");

                        if (kutu_sahibi.equalsIgnoreCase(bitis)) {
                            String severe = (String) obj0.get("stack");
                            ItemStack stack0 = itemFrom64(severe);
                            open_box((Player) e.getWhoClicked());
                            e.getWhoClicked().getInventory().addItem(stack0);
                            e.getWhoClicked().sendMessage("§f" + obj0.get("id") + " ID'li ihalenin eşyasını aldın.");
                        } else {
                            Long finish = (Long) obj0.get("finish");
                            open_box((Player) e.getWhoClicked());
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
                        e.getWhoClicked().openInventory(inv_main);
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

                        JSONParser js = new JSONParser();
                        FileReader reader = new FileReader(getDataFolder() + "/data.json");
                        JSONArray js_list = (JSONArray) js.parse(reader);


                        JSONObject obj0 = null;
                        for (Object var : js_list) {
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
                            open_box((Player) e.getWhoClicked());
                            break;
                        }

                        if (!obj0.get("bid").equals("null")) {
                            e.getWhoClicked().sendMessage("§cIptal etmeye çalıştığın ihaleye çoktan teklif verilmiş");
                            break;
                        }


                        e.getWhoClicked().sendMessage("§aIhale iptal edildi eşyanı depondan alabilirsin");
                        add_item_to_box(obj0);


                        js_list.remove(obj0);
                        FileWriter writer = new FileWriter(getDataFolder() + "/data.json");
                        writer.write(js_list.toJSONString());
                        writer.flush();

                        open_your_ihale((Player) e.getWhoClicked());






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
                        e.getWhoClicked().openInventory(inv_main);
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

                        JSONParser js = new JSONParser();
                        FileReader reader = new FileReader(getDataFolder() + "/data.json");
                        JSONArray js_list = (JSONArray) js.parse(reader);
                        JSONObject obj0 = null;
                        for (Object o : js_list) {
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
                        make_offer(obj0, (Player) e.getWhoClicked());



                    } catch (Exception ignored) {
                        break;
                    }
                    break;
                }




            }
        } catch (Exception ignored) {}
    }



    @SuppressWarnings({"unchecked", "deprecation"})
    private void update_offer(InventoryClickEvent e) {
        try {
            ItemStack stack = e.getCurrentItem();
            if (stack == null) return;

            String name = stack.getItemMeta().getDisplayName();


            String id = vUtils.get_title_inventory(new Object[]{e.getWhoClicked(), e.getClickedInventory()});;
            id = id.replaceAll("Teklif Veriliyor... ", "");
            Long id0 = Long.parseLong(id);

            JSONParser parser = new JSONParser();
            FileReader reader = new FileReader(getDataFolder() + "/data.json");
            JSONArray js_list = (JSONArray) parser.parse(reader);

            JSONObject obj0 = null;
            for (Object o : js_list) {
                JSONObject var = (JSONObject) o;
                if (id0.equals(var.get("id"))) {
                    obj0 = var;
                    break;
                }
            }
            if (obj0 == null) {
                e.getWhoClicked().openInventory(inv_ihale);
                return;
            }

            if (obj0.get("status").equals("full")) {
                e.getWhoClicked().openInventory(inv_ihale);
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
                    e.getWhoClicked().openInventory(inv_ihale);
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


                    FileWriter writer = new FileWriter(getDataFolder() + "/data.json");
                    int index = js_list.indexOf(obj0);
                    obj0.put("bid", e.getWhoClicked().getName());
                    obj0.put("finish", finish);
                    obj0.put("status", "full");
                    obj0.put("statusTimer", 3);
                    js_list.set(index, obj0);
                    writer.write(js_list.toJSONString());
                    writer.flush();






                    e.getWhoClicked().openInventory(inv_ihale);
                    e.getWhoClicked().sendMessage("§a" + id0 + " ID'li ihaleye başarıyla teklif verdin.");
                    economy.withdrawPlayer((Player) e.getWhoClicked(), finish);
                    return;
                }

                obj0.put("bid", e.getWhoClicked().getName());
                obj0.put("finish", finish);


                String severe = (String) obj0.get("stack");
                ItemStack stack1 = itemFrom64(severe);
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



    @SuppressWarnings("deprecation")
    private void make_offer(JSONObject obj0, Player player) {
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


            ItemStack ccc = vUtils.get_cam();
            ItemMeta ccc_meta = ccc.getItemMeta();
            ccc_meta.setDisplayName(" ");
            ccc.setItemMeta(ccc_meta);
            for (int var = 9; var <= 17; var++) {
                inv.setItem(var, ccc);
            }

            ItemStack onayla = vUtils.get_onayla();
            ItemMeta onayla_meta = onayla.getItemMeta();
            onayla_meta.setDisplayName("§aOnayla");
            onayla.setItemMeta(onayla_meta);

            ItemStack iptal = vUtils.get_iptal();
            ItemMeta iptal_meta = iptal.getItemMeta();
            iptal_meta.setDisplayName("§cIptal");
            iptal.setItemMeta(iptal_meta);

            inv.setItem(39, iptal);
            inv.setItem(41, onayla);

            String severe = (String) obj0.get("stack");
            ItemStack stack = itemFrom64(severe);
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
            player.openInventory(inv_main);
        }
    }


    private void a() {
        RegisteredServiceProvider<Economy> prv = getServer().getServicesManager().getRegistration(Economy.class);
        economy = prv.getProvider();
        if (economy == null) {
            getLogger().severe("Vault not found!");
            getPluginLoader().disablePlugin(this);
        }
    }
    private void b() {
        inv_main = Bukkit.createInventory(null, 27, "Ihale");
        b_1();
        b_2();
        b_3();
        b_4();
        b_5();
    }
    private void b_1() {
        ItemStack stack = new ItemStack(Material.GOLD_INGOT);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("§aMevcut ihaleler");
        List<String> list = new ArrayList<>();
        list.add("§7Burada aktif ihaleleri");
        list.add("§7görebilirsin.");
        list.add(" ");
        list.add("§eAçmak için tıkla");
        meta.setLore(list);
        stack.setItemMeta(meta);
        inv_main.setItem(10, stack);
    }
    private void b_2() {
        ItemStack stack = new ItemStack(Material.CHEST);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("§aKutun");
        List<String> list = new ArrayList<>();
        list.add("§7Ihaleden kazandığın");
        list.add("§7eşyalar, paralar veya");
        list.add("§7satılmayan yada iptal");
        list.add("§7ettiğin ihalelerin eşyaları");
        list.add("§7burada depolanır. Istediğin");
        list.add("§7zaman buraya gelip alabilirsin.");
        list.add(" ");
        list.add("§eAçmak için tıkla");
        meta.setLore(list);
        stack.setItemMeta(meta);
        inv_main.setItem(12, stack);
    }
    private void b_3() {
        ItemStack stack = new ItemStack(Material.DIAMOND);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("§aSenin ihalelerin");
        List<String> list = new ArrayList<>();
        list.add("§7Şuanda aktif ihalelerini");
        list.add("§7burada görebilir ve");
        list.add("§7iptal edebilirsin.");
        list.add(" ");
        list.add("§eAçmak için tıkla");
        meta.setLore(list);
        stack.setItemMeta(meta);
        inv_main.setItem(14, stack);
    }
    private void b_4() {
        ItemStack stack = vUtils.get_kitap();
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("§aIhale Geçmişi");
        List<String> list = new ArrayList<>();
        list.add("§7Son §f24 §7saatte");
        list.add("§7oluşturulmuş tüm");
        list.add("§7ihaleleri burada");
        list.add("§7bulabilirsin.");
        list.add(" ");
        list.add("§eAçmak için tıkla");
        meta.setLore(list);
        stack.setItemMeta(meta);
        inv_main.setItem(16, stack);
    }
    private void b_5() {
        ItemStack stack = new ItemStack(Material.BOOK);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("§eNasıl ihale başlatırım?");
        List<String> list = new ArrayList<>();
        list.add(" ");
        list.add("§f/ihale başlat <para>");
        list.add("§7komutunu kullanarak");
        list.add("§7elindeki eşya için");
        list.add("§7ihale başlatabilirsin.");
        meta.setLore(list);
        stack.setItemMeta(meta);
        inv_main.setItem(22, stack);
    }
    private void c() {
        inv_ihale = Bukkit.createInventory(null, 36, "Aktif Ihaleler");
        c_1();
        c_2();
        c_3();
        c_4();
    }
    private void c_1() {
        ItemStack stack = new ItemStack(Material.ARROW);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("§3< Geri Dön");
        List<String> list = new ArrayList<>();
        list.add("§7Tıkla ve bir önceki menüye dön");
        meta.setLore(list);
        stack.setItemMeta(meta);
        inv_ihale.setItem(27, stack);
    }
    private void c_2() {
        File file = new File(getDataFolder() + "/data.json");
        try {
            File file1 = new File(getDataFolder() + "/box/test.txt");
            if (!file1.exists()) {
                FileConfiguration c = YamlConfiguration.loadConfiguration(file1);
                c.save(file1);
            }
        } catch (Exception ignored) {}
        if (!file.exists()) {
            getLogger().info("Creating new 'data.json' file...");
            try {
                FileWriter writer = new FileWriter(getDataFolder() + "/data.json");
                writer.close();
            } catch (Exception e) {
                getLogger().severe("Exception handled on writing 'data.json' file. Plugin will stop!");
                getPluginLoader().disablePlugin(this);
            }
        }

    }
    private void c_3() {
        try {
            File file = new File(getDataFolder() + "/current.yml");
            if (!file.exists()) return;
            FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
            if (!configuration.isSet("current")) return;
            current = configuration.getLong("current");
        } catch (Exception ignored) {}
    }
    private void c_4() {
        try {
            BukkitRunnable runnable = new BukkitRunnable() {
                public void run() {
                    ArrayList<ItemStack> list = update_ihale_items();
                    if (list == null) return;
                    for (int var = 0; var < 27; var++) {
                        try {
                            ItemStack stack = list.get(var);
                            inv_ihale.setItem(var, stack);
                        } catch (Exception ignored) {
                            inv_ihale.setItem(var, new ItemStack(Material.AIR));
                        }
                    }
                }
            };
            runnable.runTaskTimer(this, 20L, 20L);
        } catch (Exception e) {
            getLogger().severe("An error found with something...");
            if (isEnabled()) getPluginLoader().disablePlugin(this);
        }
    }
    private String itemTo64(ItemStack stack) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(stack);

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            getLogger().severe("An error found on system!");
        }
        return null;
    }
    private ItemStack itemFrom64(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
                return (ItemStack) dataInput.readObject();
            }
        } catch (Exception e) {
            getLogger().severe("An error found on system!");
        }
        return null;
    }
    private long current = 0;
    @SuppressWarnings("unchecked")
    private synchronized void write_new_ihale(ItemStack stack, String player, int start_balance) {
        JSONParser js = new JSONParser();
        try {
            current++;
            File aa0 = new File(getDataFolder() + "/current.yml");
            FileConfiguration aa1 = YamlConfiguration.loadConfiguration(aa0);
            aa1.set("current", current);
            aa1.save(aa0);




            JSONObject obj0 = new JSONObject();
            obj0.put("stack", itemTo64(stack));
            obj0.put("player", player);
            obj0.put("start", start_balance);
            obj0.put("id", current);
            obj0.put("time", 900000);
            obj0.put("bid", "null");
            obj0.put("finish", 0L);
            obj0.put("status", "free");
            obj0.put("statustimer", 0);


            File check = new File(getDataFolder() + "/data.json");
            if (check.length() != 0) {
                FileReader reader = new FileReader(getDataFolder() + "/data.json");
                Object obj = js.parse(reader);
                JSONArray js_list = (JSONArray) obj;
                js_list.add(obj0);

                FileWriter file = new FileWriter(getDataFolder() + "/data.json");
                file.write(js_list.toJSONString());
                file.flush();
            } else {
                JSONArray js_list = new JSONArray();
                js_list.add(obj0);
                FileWriter file = new FileWriter(getDataFolder() + "/data.json");
                file.write(js_list.toJSONString());
                file.flush();
            }



        } catch (Exception e) {
            getLogger().severe(e.getLocalizedMessage());
            getPluginLoader().disablePlugin(this);
        }
    }
    @SuppressWarnings({"unchecked", "deprecation"})
    private ArrayList<ItemStack> update_ihale_items() {
        try {
            File check = new File(getDataFolder() + "/data.json");
            if (check.length() == 0) return null;
            final ArrayList<ItemStack> arrayList = new ArrayList<>();
            JSONParser js = new JSONParser();
            FileReader reader = new FileReader(getDataFolder() + "/data.json");
            Object obj = js.parse(reader);
            JSONArray js_list = (JSONArray) obj;
            for (int var = 0; var < 27; var++) {
                if (js_list.size() - 1 < var) {
                    break;
                }

                JSONObject var1 = (JSONObject) js_list.get(var);
                String severe = (String) var1.get("stack");
                ItemStack stack = itemFrom64(severe);
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
                js_list.set(var, var1);
                if (time <= 0) {
                    js_list.remove(var1);
                    add_item_to_box(var1);
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

            FileWriter file = new FileWriter(getDataFolder() + "/data.json");
            file.write(js_list.toJSONString());
            file.flush();

            return arrayList;
        } catch (Exception e) {
            getLogger().severe("Exception handled on reading 'data.json' file. Match will be return empty");
            getPluginLoader().disablePlugin(this);
        }
        return null;
    }
    @SuppressWarnings("unchecked")
    private void add_item_to_box(JSONObject var) {
        try {
            JSONParser js = new JSONParser();


            String bid = (String) var.get("bid");
            String player = (String) var.get("player");


            if (!bid.equals("null")) {
                File check = new File(getDataFolder() + "/box/" + bid + ".json");
                if (check.length() != 0) {
                    FileReader reader = new FileReader(getDataFolder() + "/box/" + bid + ".json");
                    Object obj = js.parse(reader);
                    JSONArray js_list = (JSONArray) obj;
                    js_list.add(var);

                    FileWriter file = new FileWriter(getDataFolder() + "/box/" + bid + ".json");
                    file.write(js_list.toJSONString());
                    file.close();




                } else {
                    JSONArray js_list = new JSONArray();
                    js_list.add(var);
                    FileWriter file = new FileWriter(getDataFolder() + "/box/" + bid + ".json");
                    file.write(js_list.toJSONString());
                    file.close();
                }
            } else {
                Player p = Bukkit.getPlayer(player);
                if (p != null) {
                    p.sendMessage("§e" + var.get("id") + " ID'li ihalene kimse teklif vermediği için iptal edildi, eşyanı depondan alabilirsin.");
                }
            }
            File check = new File(getDataFolder() + "/box/" + player + ".json");
            if (!check.exists()) //noinspection ResultOfMethodCallIgnored
                check.createNewFile();
            if (check.length() != 0) {
                FileReader reader = new FileReader(getDataFolder() + "/box/" + player + ".json");
                Object obj = js.parse(reader);
                JSONArray js_list = (JSONArray) obj;
                js_list.add(var);

                FileWriter file = new FileWriter(getDataFolder() + "/box/" + player + ".json");
                file.write(js_list.toJSONString());
                file.close();
            } else {
                JSONArray js_list = new JSONArray();
                js_list.add(var);
                FileWriter file = new FileWriter(getDataFolder() + "/box/" + player + ".json");
                file.write(js_list.toJSONString());
                file.close();
            }


            Player p = Bukkit.getPlayer(player);
            Player p1 = Bukkit.getPlayer(bid);
            if (p != null) {
                p.sendMessage("§a" + var.get("id") + " ID'li ihalen sona erdi, paranı depondan alabilirsin.");
            }
            if (p1 != null) {
                p1.sendMessage("§a" + var.get("id") + " ID'li ihale sona erdi, eşyanı depondan alabilirsin.");
            }

        } catch (Exception ignored) {
        }
    }
    private void open_box(Player player) {
        try {
            Inventory inv = open_boxA();
            File check = new File(getDataFolder() + "/box/" + player.getName() + ".json");


            if (check.length() == 0) {
                player.openInventory(inv);
            } else {

                JSONParser js = new JSONParser();
                FileReader reader = new FileReader(getDataFolder() + "/box/" + player.getName() + ".json");
                Object obj = js.parse(reader);
                JSONArray js_list = (JSONArray) obj;

                int count = 0;
                for (Object o : js_list) {

                    if (count >= 27) break;


                    JSONObject obj0 = (JSONObject) o;

                    String bid = (String) obj0.get("bid");
                    if (bid.equalsIgnoreCase(player.getName())) {
                        String severe = (String) obj0.get("stack");
                        ItemStack stack = itemFrom64(severe);
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
    private Inventory open_boxA() {
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
    @SuppressWarnings({"unchecked", "deprecation"})
    private void open_your_ihale(Player player) {
        try {
            Inventory inv = open_your_ihaleA();

            File check = new File(getDataFolder() + "/data.json");
            if (check.length() == 0) {
                player.openInventory(inv);
                return;
            }

            JSONParser js = new JSONParser();
            FileReader reader = new FileReader(getDataFolder() + "/data.json");
            Object obj = js.parse(reader);
            JSONArray js_list = (JSONArray) obj;
            JSONArray js_player = new JSONArray();

            for (Object o : js_list) {
                JSONObject obj0 = (JSONObject) o;
                if (obj0.get("player").equals(player.getName())) {
                    js_player.add(obj0);
                }
            }



            for (Object o : js_player) {
                JSONObject obj0 = (JSONObject) o;

                String severe = (String) obj0.get("stack");
                ItemStack stack = itemFrom64(severe);
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
    private Inventory open_your_ihaleA() {
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
