package gui.tender.tender;

import gui.tender.json.IFormat;
import gui.tender.version.VHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;

import java.util.*;

@SuppressWarnings("deprecation")
public class MakeOffer {


    private final JSONObject obj0;
    private final Player player;
    private final VHandler vHandler;
    private final Inventory inventoryMain;
    public MakeOffer(JSONObject obj0 ,Player player, VHandler vHandler, Inventory inventoryMain) {
        this.obj0 = obj0;
        this.player = player;
        this.vHandler = vHandler;
        this.inventoryMain = inventoryMain;
    }
    public void make() {
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
}
