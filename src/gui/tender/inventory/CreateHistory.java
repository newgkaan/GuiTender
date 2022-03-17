package gui.tender.inventory;

import gui.tender.itemstack.ISCreate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import java.util.Collections;

public class CreateHistory {

    public Inventory create() {
        final Inventory inventory = Bukkit.createInventory(null, 45, "Ihale Gecmisi");
        inventory.setItem(36, new ISCreate().createA(Material.ARROW, "§3< Geri Dön", Collections.singletonList(
                "§7Tıkla ve bir önceki menüye dön"
        )));
        return inventory;
    }

}
