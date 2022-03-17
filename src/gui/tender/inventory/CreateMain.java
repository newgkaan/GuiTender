package gui.tender.inventory;


import gui.tender.itemstack.ISCreate;
import gui.tender.version.VHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import java.util.Arrays;

public class CreateMain {

    private final VHandler vHandler;
    public CreateMain(VHandler vHandler) {
        this.vHandler = vHandler;
    }

    public Inventory create() {
        final Inventory inventoryMain = Bukkit.createInventory(null, 27, "Ihale");


        inventoryMain.setItem(10, new ISCreate().createA(Material.GOLD_INGOT, "§aMevcut ihaleler", Arrays.asList(
                "§7Burada aktif ihaleleri", "§7görebilirsin.", " ", "§eAçmak için tıkla"
        )));
        inventoryMain.setItem(12, new ISCreate().createA(Material.CHEST, "§aKutun", Arrays.asList(
                "§7Ihaleden kazandığın", "§7eşyalar, paralar veya", "§7satılmayan yada iptal",
                "§7ettiğin ihalelerin eşyalar", "§7burada depolanır. Istediğin", "§7zaman buraya gelip alabilirsin.",
                " ", "§eAçmak için tıkla"
        )));
        inventoryMain.setItem(14, new ISCreate().createA(Material.DIAMOND, "§aSenin ihalelerin", Arrays.asList(
                "§7Şuanda aktif ihalelerini", "§7burada görebilir ve", "§7iptal edebilirsin.", " ", "§eAçmak için tıkla"
        )));
        inventoryMain.setItem(16, new ISCreate().createB(vHandler.get_kitap(), "§aIhale Geçmişi", Arrays.asList(
                "§7Sunucu yeniden başlatılana", "§7kadar oluşturulmuş tüm", "§7ihaleleri burada",
                "§7bulabilirsin.", " ", "§eAçmak için tıkla"
        )));
        inventoryMain.setItem(22, new ISCreate().createA(Material.BOOK, "§eNasıl ihale başlatırım?", Arrays.asList(
                " ", "§f/ihale başlat <para>", "§7komutunu kullanarak", "§7elindeki eşya için",
                "§7ihale başlatabilirsin."
        )));
        return inventoryMain;
    }







}
