package org.faweFractions.expansions;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.faweFractions.FaweFractions;
import org.bukkit.OfflinePlayer;

public class FractionsExpansion extends PlaceholderExpansion {
    private final FaweFractions plugin;
    
    public FractionsExpansion(FaweFractions plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getIdentifier() {
        return "fractions";
    }
    
    @Override
    public String getAuthor() {
        return "_chmonya";
    }
    
    @Override
    public String getVersion() {
        return "1.0.0";
    }
    
    @Override
    public boolean canRegister() {
        return true;
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player == null) {
            return "";
        }
        
        if (params.equals("prefix")) {
            return plugin.getFractionManager().getPlayerPrefix(player.getUniqueId());
        }
        
        if (params.equals("id")) {
            var fraction = plugin.getFractionManager().getPlayerFraction(player.getUniqueId());
            return fraction != null ? fraction.getId() : "";
        }
        
        if (params.equals("leader")) {
            var fraction = plugin.getFractionManager().getPlayerFraction(player.getUniqueId());
            return fraction != null && fraction.isLeader(player.getUniqueId()) ? "true" : "false";
        }
        
        if (params.equals("members")) {
            var fraction = plugin.getFractionManager().getPlayerFraction(player.getUniqueId());
            return fraction != null ? String.valueOf(fraction.getMemberCount()) : "0";
        }
        
        return null;
    }
}
