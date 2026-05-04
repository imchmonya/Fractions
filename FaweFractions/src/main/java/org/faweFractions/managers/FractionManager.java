package org.faweFractions.managers;

import org.faweFractions.models.Fraction;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FractionManager {
    private final Map<String, Fraction> fractions;
    private final Map<UUID, String> playerFractions;
    private final Map<UUID, String> invitations;
    
    public FractionManager() {
        this.fractions = new HashMap<>();
        this.playerFractions = new HashMap<>();
        this.invitations = new HashMap<>();
    }
    
    public boolean createFraction(String id, String prefix, UUID leader) {
        if (fractions.containsKey(id.toLowerCase())) {
            return false;
        }
        
        Fraction fraction = new Fraction(id, prefix, leader);
        fractions.put(id.toLowerCase(), fraction);
        playerFractions.put(leader, id.toLowerCase());
        return true;
    }
    
    public boolean deleteFraction(String id) {
        Fraction fraction = fractions.get(id.toLowerCase());
        if (fraction == null) {
            return false;
        }
        
        for (UUID member : fraction.getMembers()) {
            playerFractions.remove(member);
        }
        
        fractions.remove(id.toLowerCase());
        return true;
    }
    
    public Fraction getFraction(String id) {
        return fractions.get(id.toLowerCase());
    }
    
    public Fraction getPlayerFraction(UUID player) {
        String fractionId = playerFractions.get(player);
        return fractionId != null ? fractions.get(fractionId) : null;
    }
    
    public boolean joinFraction(UUID player, String fractionId) {
        if (playerFractions.containsKey(player)) {
            return false;
        }
        
        Fraction fraction = fractions.get(fractionId.toLowerCase());
        if (fraction == null) {
            return false;
        }
        
        fraction.addMember(player);
        playerFractions.put(player, fractionId.toLowerCase());
        return true;
    }
    
    public boolean leaveFraction(UUID player) {
        String fractionId = playerFractions.get(player);
        if (fractionId == null) {
            return false;
        }
        
        Fraction fraction = fractions.get(fractionId);
        if (fraction != null) {
            fraction.removeMember(player);
        }
        
        playerFractions.remove(player);
        return true;
    }
    
    public boolean isLeader(UUID player) {
        Fraction fraction = getPlayerFraction(player);
        return fraction != null && fraction.isLeader(player);
    }
    
    public String getPlayerPrefix(UUID player) {
        Fraction fraction = getPlayerFraction(player);
        return fraction != null ? fraction.getPrefix() : "";
    }
    
    public Map<String, Fraction> getAllFractions() {
        return new HashMap<>(fractions);
    }
    
    public boolean invitePlayer(UUID player, String fractionId) {
        if (playerFractions.containsKey(player)) {
            return false;
        }
        
        Fraction fraction = fractions.get(fractionId.toLowerCase());
        if (fraction == null) {
            return false;
        }
        
        invitations.put(player, fractionId.toLowerCase());
        return true;
    }
    
    public boolean acceptInvitation(UUID player) {
        String fractionId = invitations.remove(player);
        if (fractionId == null) {
            return false;
        }
        
        return joinFraction(player, fractionId);
    }
    
    public boolean declineInvitation(UUID player) {
        return invitations.remove(player) != null;
    }
    
    public String getInvitation(UUID player) {
        return invitations.get(player);
    }
    
    public boolean hasInvitation(UUID player) {
        return invitations.containsKey(player);
    }
}
