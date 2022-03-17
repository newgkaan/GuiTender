package gui.tender.inventory;

import gui.tender.Main;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;



import java.util.ArrayList;
import java.util.List;

public class CreateMain {

    public CreateMain() {
    }
    
    private Inventory inventoryMain;
    public Inventory create() {
        inventoryMain = Bukkit.createInventory(null, 27, "Ihale");
        ba();
        bb();
        bc();
        bd();
        be();
        return inventoryMain;
    }

    private void ba() {
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
        inventoryMain.setItem(10, stack);
    }
    private void bb() {
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
        inventoryMain.setItem(12, stack);
    }
    private void bc() {
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
        inventoryMain.setItem(14, stack);
    }
    private void bd() {
        ItemStack stack = Main.vHandler.get_kitap();
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
        inventoryMain.setItem(16, stack);
    }
    private void be() {
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
        inventoryMain.setItem(22, stack);
    }


}
