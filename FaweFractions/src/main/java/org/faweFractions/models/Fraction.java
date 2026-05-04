package org.faweFractions.models;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Fraction {
    private final String id;
    private final String prefix;
    private final Set<UUID> members;
    private UUID leader;
    
    public Fraction(String id, String prefix, UUID leader) {
        this.id = id.toLowerCase();
        this.prefix = prefix;
        this.leader = leader;
        this.members = new HashSet<>();
        this.members.add(leader);
    }
    
    public String getId() {
        return id;
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public UUID getLeader() {
        return leader;
    }
    
    public void setLeader(UUID leader) {
        this.leader = leader;
    }
    
    public Set<UUID> getMembers() {
        return new HashSet<>(members);
    }
    
    public boolean isMember(UUID player) {
        return members.contains(player);
    }
    
    public boolean isLeader(UUID player) {
        return leader.equals(player);
    }
    
    public void addMember(UUID player) {
        members.add(player);
    }
    
    public void removeMember(UUID player) {
        members.remove(player);
        if (leader.equals(player) && !members.isEmpty()) {
            leader = members.iterator().next();
        }
    }
    
    public int getMemberCount() {
        return members.size();
    }
    
    public String getLeaderName() {
        OfflinePlayer player = Bukkit.getOfflinePlayer(leader);
        return player.getName() != null ? player.getName() : "";
    }
}
