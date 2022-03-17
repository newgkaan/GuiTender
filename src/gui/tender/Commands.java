package gui.tender;


import gui.tender.version.VHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Commands implements CommandExecutor {

    private final Plugin guiTender;
    private final Map<UUID, Boolean> cooldown = new HashMap<>();
    private final Inventory inventoryMain;
    private final VHandler vHandler;
    
    public Commands(Plugin guiTender, Inventory inventoryMain, VHandler vHandler) {
        this.inventoryMain = inventoryMain;
        this.vHandler = vHandler;
        this.guiTender = guiTender;

    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Bu komut sadece oyuncular içindir!");
            return true;
        }

        if (args.length < 1) {
            ((Player) sender).openInventory(inventoryMain);
        } else {
            if (args.length != 2) return true;
            if (!args[0].equalsIgnoreCase("başlat") || args[0].equalsIgnoreCase("baslat")) {
                return true;
            }


            if (cooldown.getOrDefault(((Player) sender).getUniqueId(), false)) {
                sender.sendMessage("§c15 saniyede bir ihale başlatabilirsin.");
                return true;
            }




            int balance = 0;
            try {
                balance = Integer.parseInt(args[1]);
            } catch (Exception ignored) {}
            if (balance == 0) {
                sender.sendMessage("§cHatalı değer girdin.");
                return true;
            }
            if (balance < 0) {
                sender.sendMessage("§cHatalı değer girdin.");
                return true;
            }

            Player player = (Player) sender;
            ItemStack stack = vHandler.get_item_hand(player).clone();
            if (stack == null || stack.getType().equals(Material.AIR)) {
                player.sendMessage("§cÖnce eline izin verilen eşyalardan birisini almalısın.");
                return true;
            }
            vHandler.set_item_hand(player, null);



            Bukkit.broadcastMessage("§c>> §9" + player.getName() + " §ayeni bir ihale başlattı.");
            ItemStack var = stack.clone();
            ItemMeta var1 = var.getItemMeta();
            var1.setDisplayName(null);
            var.setItemMeta(var1);
            Bukkit.broadcastMessage("§c>> " + var.getType());
            Main.tender.writeNewTender(stack, player.getName(), balance);


            cooldown.put(player.getUniqueId(), true);
            new BukkitRunnable() {
                public void run() {
                    cooldown.remove(player.getUniqueId());
                }
            }.runTaskLater(guiTender, 300L);
            return true;
        }
        return true;
    }





    
    
}
