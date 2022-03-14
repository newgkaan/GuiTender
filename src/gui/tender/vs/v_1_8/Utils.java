package gui.tender.vs.v_1_8;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Utils {
    public Utils() {

    }
    public void set_item_hand(Player p, ItemStack s) {
        p.setItemInHand(s);
    }
    public ItemStack get_item_hand(Player p) {
        return p.getItemInHand();
    }
    public String get_title_inventory(Object[] o) {
        return ((Player) o[0]).getOpenInventory().getTitle();
    }

    public ItemStack get_onayla() {
        return new ItemStack(Material.WOOL, 1, (short) 5);
    }
    public ItemStack get_iptal() {
        return new ItemStack(Material.WOOL, 1, (short) 14);
    }
    public ItemStack get_cam() {
        return new ItemStack(Material.STAINED_GLASS_PANE);
    }
    public ItemStack get_kitap() {
        return new ItemStack(Material.BOOK_AND_QUILL);
    }
}
