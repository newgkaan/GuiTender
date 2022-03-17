package gui.tender.version;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class VHandler {

    private final String version;

    public VHandler(String version) {
        this.version = version;


        if (version.contains("1.7") || version.contains("1.8") || version.contains("1.9") || version.contains("1.10") || version.contains("1.11") || version.contains("1.12")) {
            kitap = new ItemStack(Material.getMaterial("BOOK_AND_QUILL"));
            onayla = new ItemStack(Material.getMaterial("WOOL"), 1, (short) 5);
            iptal = new ItemStack(Material.getMaterial("WOOL"), 1, (short) 14);
            cam = new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"));
        } else {
            kitap = new ItemStack(Material.getMaterial("WRITABLE_BOOK"));
            onayla = new ItemStack(Material.getMaterial("GREEN_WOOL"));
            iptal = new ItemStack(Material.getMaterial("RED_WOOL"));
            cam = new ItemStack(Material.getMaterial("WHITE_STAINED_GLASS_PANE"));
        }
    }


    public void set_item_hand(Player p, ItemStack s) {
        if (version.contains("1.7") || version.contains("1.8")) {
            //noinspection deprecation
            p.setItemInHand(s);
        } else {
            p.getInventory().setItemInMainHand(s);
        }

    }


    public ItemStack get_item_hand(Player p) {
        if (version.contains("1.7") || version.contains("1.8")) {
            //noinspection deprecation
            return p.getItemInHand();
        } else {
            return p.getInventory().getItemInMainHand();
        }
    }

    public String get_title_inventory(Object[] o) {
        return ((Player) o[0]).getOpenInventory().getTitle();
    }

    private final ItemStack onayla;
    public final ItemStack get_onayla() {
        return onayla;
    }

    private final ItemStack iptal;
    public final ItemStack get_iptal() {
        return iptal;
    }

    private final ItemStack cam;
    public final ItemStack get_cam() {
        return cam;
    }

    private final ItemStack kitap;
    public final ItemStack get_kitap() {
        return kitap;
    }





}
