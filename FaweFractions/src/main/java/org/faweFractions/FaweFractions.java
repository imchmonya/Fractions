package org.faweFractions;

import org.faweFractions.commands.FractionCommand;
import org.faweFractions.expansions.FractionsExpansion;
import org.faweFractions.listeners.PlaceholderListener;
import org.faweFractions.managers.FractionManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class FaweFractions extends JavaPlugin {
    private FractionManager fractionManager;
    private FractionsExpansion expansion;
    
    @Override
    public void onEnable() {
        fractionManager = new FractionManager();
        
        getServer().getPluginManager().registerEvents(new PlaceholderListener(this), this);
        getCommand("fractions").setExecutor(new FractionCommand(this));
        
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            expansion = new FractionsExpansion(this);
            expansion.register();
        } else {
        }
        
        getLogger().info("Плагин вкл!");
    }

    @Override
    public void onDisable() {
        if (expansion != null) {
            expansion.unregister();
        }
    }
    
    public FractionManager getFractionManager() {
        return fractionManager;
    }
}
