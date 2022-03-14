package gui.tender.vs.v_1_16;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Utils {
    public Utils() {

    }
    public ItemStack get_item_hand(Player p) {
        return p.getInventory().getItemInMainHand();
    }

    public void set_item_hand(Player p, ItemStack s) {
        p.getInventory().setItemInMainHand(s);
    }

    public String get_title_inventory(Object[] o) {
        return ((Player) o[0]).getOpenInventory().getTitle();
    }

    public ItemStack get_onayla() {
        return new ItemStack(Material.matchMaterial("GREEN_WOOL"));
    }
    public ItemStack get_iptal() {
        return new ItemStack(Material.matchMaterial("RED_WOOL"));
    }

    public ItemStack get_cam() {
        return new ItemStack(Material.matchMaterial("WHITE_STAINED_GLASS_PANE"));
    }
    public ItemStack get_kitap() {
        return new ItemStack(Material.matchMaterial("WRITABLE_BOOK"));
    }
}
