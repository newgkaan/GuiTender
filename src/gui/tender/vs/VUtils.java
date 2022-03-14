package gui.tender.vs;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class VUtils {

    private final String version;

    public VUtils(String version) {
        this.version = version;
    }



    public void set_item_hand(Player p, ItemStack s) {
        if (version.contains("1.7")) {
            new gui.tender.vs.v1_7.Utils().set_item_hand(p, s);
            return;
        }
        if (version.contains("1.8")) {
            new gui.tender.vs.v_1_8.Utils().set_item_hand(p, s);
            return;
        }
        if (version.contains("1.9")) {
            new gui.tender.vs.v1_9.Utils().set_item_hand(p, s);
            return;
        }
        if (version.contains("1.10")) {
            new gui.tender.vs.v1_10.Utils().set_item_hand(p, s);
            return;
        }
        if (version.contains("1.11")) {
            new gui.tender.vs.v_1_11.Utils().set_item_hand(p, s);
            return;
        }
        if (version.contains("1.12")) {
            new gui.tender.vs.v_1_12.Utils().set_item_hand(p, s);
            return;
        }
        if (version.contains("1.13")) {
            new gui.tender.vs.v_1_13.Utils().set_item_hand(p, s);
            return;
        }
        if (version.contains("1.14")) {
            new gui.tender.vs.v_1_14.Utils().set_item_hand(p, s);
            return;
        }
        if (version.contains("1.15")) {
            new gui.tender.vs.v_1_15.Utils().set_item_hand(p, s);
            return;
        }
        if (version.contains("1.16")) {
            new gui.tender.vs.v_1_16.Utils().set_item_hand(p, s);
        }

    }
    public ItemStack get_item_hand(Player p) {
        if (version.contains("1.7")) {
            return new gui.tender.vs.v1_7.Utils().get_item_hand(p);
        }
        if (version.contains("1.8")) {
            return new gui.tender.vs.v_1_8.Utils().get_item_hand(p);
        }
        if (version.contains("1.9")) {
            return new gui.tender.vs.v1_9.Utils().get_item_hand(p);
        }
        if (version.contains("1.10")) {
            return new gui.tender.vs.v1_10.Utils().get_item_hand(p);
        }
        if (version.contains("1.11")) {
            return new gui.tender.vs.v_1_11.Utils().get_item_hand(p);
        }
        if (version.contains("1.12")) {
            return new gui.tender.vs.v_1_12.Utils().get_item_hand(p);
        }
        if (version.contains("1.13")) {
            return new gui.tender.vs.v_1_13.Utils().get_item_hand(p);
        }
        if (version.contains("1.14")) {
            return new gui.tender.vs.v_1_14.Utils().get_item_hand(p);
        }
        if (version.contains("1.15")) {
            return new gui.tender.vs.v_1_15.Utils().get_item_hand(p);
        }
        if (version.contains("1.16")) {
            return new gui.tender.vs.v_1_16.Utils().get_item_hand(p);
        }
        return null;
    }

    public String get_title_inventory(Object[] o) {
        if (version.contains("1.7")) {
            return new gui.tender.vs.v1_7.Utils().get_title_inventory(o);
        }
        if (version.contains("1.8")) {
            return new gui.tender.vs.v_1_8.Utils().get_title_inventory(o);
        }
        if (version.contains("1.9")) {
            return new gui.tender.vs.v1_9.Utils().get_title_inventory(o);
        }
        if (version.contains("1.10")) {
            return new gui.tender.vs.v1_10.Utils().get_title_inventory(o);
        }
        if (version.contains("1.11")) {
            return new gui.tender.vs.v_1_11.Utils().get_title_inventory(o);
        }
        if (version.contains("1.12")) {
            return new gui.tender.vs.v_1_12.Utils().get_title_inventory(o);
        }
        if (version.contains("1.13")) {
            return new gui.tender.vs.v_1_13.Utils().get_title_inventory(o);
        }
        if (version.contains("1.14")) {
            return new gui.tender.vs.v_1_14.Utils().get_title_inventory(o);
        }
        if (version.contains("1.15")) {
            return new gui.tender.vs.v_1_15.Utils().get_title_inventory(o);
        }
        if (version.contains("1.16")) {
            return new gui.tender.vs.v_1_16.Utils().get_title_inventory(o);
        }
        return null;
    }

    public ItemStack get_onayla() {
        if (version.contains("1.7")) {
            return new gui.tender.vs.v1_7.Utils().get_onayla();
        }
        if (version.contains("1.8")) {
            return new gui.tender.vs.v_1_8.Utils().get_onayla();
        }
        if (version.contains("1.9")) {
            return new gui.tender.vs.v1_9.Utils().get_onayla();
        }
        if (version.contains("1.10")) {
            return new gui.tender.vs.v1_10.Utils().get_onayla();
        }
        if (version.contains("1.11")) {
            return new gui.tender.vs.v_1_11.Utils().get_onayla();
        }
        if (version.contains("1.12")) {
            return new gui.tender.vs.v_1_12.Utils().get_onayla();
        }
        if (version.contains("1.13")) {
            return new gui.tender.vs.v_1_13.Utils().get_onayla();
        }
        if (version.contains("1.14")) {
            return new gui.tender.vs.v_1_14.Utils().get_onayla();
        }
        if (version.contains("1.15")) {
            return new gui.tender.vs.v_1_15.Utils().get_onayla();
        }
        if (version.contains("1.16")) {
            return new gui.tender.vs.v_1_16.Utils().get_onayla();
        }
        return null;
    }
    public ItemStack get_iptal() {
        if (version.contains("1.7")) {
            return new gui.tender.vs.v1_7.Utils().get_iptal();
        }
        if (version.contains("1.8")) {
            return new gui.tender.vs.v_1_8.Utils().get_iptal();
        }
        if (version.contains("1.9")) {
            return new gui.tender.vs.v1_9.Utils().get_iptal();
        }
        if (version.contains("1.10")) {
            return new gui.tender.vs.v1_10.Utils().get_iptal();
        }
        if (version.contains("1.11")) {
            return new gui.tender.vs.v_1_11.Utils().get_iptal();
        }
        if (version.contains("1.12")) {
            return new gui.tender.vs.v_1_12.Utils().get_iptal();
        }
        if (version.contains("1.13")) {
            return new gui.tender.vs.v_1_13.Utils().get_iptal();
        }
        if (version.contains("1.14")) {
            return new gui.tender.vs.v_1_14.Utils().get_iptal();
        }
        if (version.contains("1.15")) {
            return new gui.tender.vs.v_1_15.Utils().get_iptal();
        }
        if (version.contains("1.16")) {
            return new gui.tender.vs.v_1_16.Utils().get_iptal();
        }
        return null;
    }
    public ItemStack get_cam() {
        if (version.contains("1.7")) {
            return new gui.tender.vs.v1_7.Utils().get_cam();
        }
        if (version.contains("1.8")) {
            return new gui.tender.vs.v_1_8.Utils().get_cam();
        }
        if (version.contains("1.9")) {
            return new gui.tender.vs.v1_9.Utils().get_cam();
        }
        if (version.contains("1.10")) {
            return new gui.tender.vs.v1_10.Utils().get_cam();
        }
        if (version.contains("1.11")) {
            return new gui.tender.vs.v_1_11.Utils().get_cam();
        }
        if (version.contains("1.12")) {
            return new gui.tender.vs.v_1_12.Utils().get_cam();
        }
        if (version.contains("1.13")) {
            return new gui.tender.vs.v_1_13.Utils().get_cam();
        }
        if (version.contains("1.14")) {
            return new gui.tender.vs.v_1_14.Utils().get_cam();
        }
        if (version.contains("1.15")) {
            return new gui.tender.vs.v_1_15.Utils().get_cam();
        }
        if (version.contains("1.16")) {
            return new gui.tender.vs.v_1_16.Utils().get_cam();
        }
        return null;
    }

    public ItemStack get_kitap() {
        if (version.contains("1.7")) {
            return new gui.tender.vs.v1_7.Utils().get_kitap();
        }
        if (version.contains("1.8")) {
            return new gui.tender.vs.v_1_8.Utils().get_kitap();
        }
        if (version.contains("1.9")) {
            return new gui.tender.vs.v1_9.Utils().get_kitap();
        }
        if (version.contains("1.10")) {
            return new gui.tender.vs.v1_10.Utils().get_kitap();
        }
        if (version.contains("1.11")) {
            return new gui.tender.vs.v_1_11.Utils().get_kitap();
        }
        if (version.contains("1.12")) {
            return new gui.tender.vs.v_1_12.Utils().get_kitap();
        }
        if (version.contains("1.13")) {
            return new gui.tender.vs.v_1_13.Utils().get_kitap();
        }
        if (version.contains("1.14")) {
            return new gui.tender.vs.v_1_14.Utils().get_kitap();
        }
        if (version.contains("1.15")) {
            return new gui.tender.vs.v_1_15.Utils().get_kitap();
        }
        if (version.contains("1.16")) {
            return new gui.tender.vs.v_1_16.Utils().get_kitap();
        }
        return null;
    }





}
