package org.faweFractions.listeners;

import org.faweFractions.FaweFractions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlaceholderListener implements Listener {
    private final FaweFractions plugin;
    
    public PlaceholderListener(FaweFractions plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        
        if (message.contains("%fractions_prefix%")) {
            String prefix = plugin.getFractionManager().getPlayerPrefix(player.getUniqueId());
            message = message.replace("%fractions_prefix%", prefix);
            event.setMessage(message);
        }
    }
}
