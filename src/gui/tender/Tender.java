package gui.tender;


import gui.tender.tender.*;
import gui.tender.version.VHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.*;

public class Tender {

    private Inventory inventoryTender;
    private final Inventory inventoryMain;
    private final VHandler vHandler;
    private final Economy economy;
    private final String path;
    private final History history;



    private final TenderID tenderId;


    public Tender(String path, Economy economy, VHandler vHandler, Inventory inventoryMain) {
        this.path = path;
        this.economy = economy;
        this.vHandler = vHandler;
        this.inventoryMain = inventoryMain;
        this.history = new History();

        tenderId = new TenderID();
        tenderId.create(path);

    }

    public History getHistory() {
        return this.history;
    }
    public void openHistory(Player player) {
        history.open(player);
    }
    public Inventory getInventoryTender() {
        return inventoryTender;
    }
    public void setInventoryTender(Inventory inventoryTender) {
        this.inventoryTender = inventoryTender;
    }

    public synchronized void writeNewTender(ItemStack stack, String player, int start) {
        new WriteNew(stack, player, start, path).write(tenderId);
    }
    public void updateOffer(InventoryClickEvent e) {
        new UpdateOffer(e, vHandler, path, economy, inventoryTender).update();
    }
    public void openOwnTender(Player player) {
        new OpenOwn(player, path).open();
    }

    public ArrayList<ItemStack> updateTenderItems() {
        return new Updater(path).getUpdates();
    }

    public void makeOffer(JSONObject obj0, Player player) {
        new MakeOffer(obj0, player, vHandler, inventoryMain).make();
    }
    public void addItemToBox(JSONObject var) {
        new AddItemBox(var, path).add();
    }
    public void openBox(Player player) {
        new OpenBox(player, path).open();
    }

}
