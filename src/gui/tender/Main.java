package gui.tender;

import gui.tender.event.InventoryClick;
import gui.tender.event.PlayerJoin;
import gui.tender.inventory.CreateMain;
import gui.tender.inventory.CreateTender;
import gui.tender.version.VCheck;
import gui.tender.version.VHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Main extends JavaPlugin {

    public static Tender tender;
    private Economy economy;

    public void onEnable() {


        VHandler vHandler = new VHandler(getServer().getVersion());
        a();
        Inventory inventoryMain = new CreateMain(vHandler).create();
        tender = new Tender(getDataFolder().getPath(), economy, vHandler, inventoryMain);
        new CreateTender(getDataFolder()).create();




        getServer().getPluginManager().registerEvents(new PlayerJoin(new VCheck().makeCheck()), this);
        getServer().getPluginManager().registerEvents(new InventoryClick(getDataFolder().getPath(), inventoryMain, economy, vHandler), this);
        getCommand("ihale").setExecutor(new Commands(this, inventoryMain, vHandler));


        startTender();
    }

    private void a() {
        RegisteredServiceProvider<Economy> prv = getServer().getServicesManager().getRegistration(Economy.class);
        economy = prv.getProvider();
        if (economy == null) {
            getLogger().severe("Vault not found!");
            getPluginLoader().disablePlugin(this);
        }
    }



    private void startTender() {
        try {
            BukkitRunnable runnable = new BukkitRunnable() {
                public void run() {
                    ArrayList<ItemStack> list = tender.updateTenderItems();
                    if (list == null) return;
                    for (int var = 0; var < 27; var++) {
                        try {
                            ItemStack stack = list.get(var);
                            tender.getInventoryTender().setItem(var, stack);
                        } catch (Exception ignored) {
                            tender.getInventoryTender().setItem(var, new ItemStack(Material.AIR));
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





}
