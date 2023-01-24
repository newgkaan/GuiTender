package gui.tender.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
    private final boolean hasUpdate;
    public PlayerJoin(boolean hasUpdate) {
        this.hasUpdate = hasUpdate;
    }



    @EventHandler
    public void on(PlayerJoinEvent e) {
        if (!e.getPlayer().isOp()) return;
        if (!hasUpdate) return;
        e.getPlayer().sendMessage(" ");
        e.getPlayer().sendMessage(" ");
        e.getPlayer().sendMessage("§c>>> §eGuiTender ihale eklentisi için güncelleme mevcut:");
        e.getPlayer().sendMessage("§c>>> Link: §fhttps://github.com/kYaaaz/GuiTender");
        e.getPlayer().sendMessage(" ");
        e.getPlayer().sendMessage(" ");
    }
}
