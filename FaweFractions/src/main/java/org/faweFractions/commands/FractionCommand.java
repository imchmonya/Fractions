package org.faweFractions.commands;

import org.faweFractions.FaweFractions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FractionCommand implements CommandExecutor, TabCompleter {
    private final FaweFractions plugin;
    
    public FractionCommand(FaweFractions plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭту команду могут использовать только игроки!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "create":
                handleCreate(player, args);
                break;
            case "delete":
                handleDelete(player, args);
                break;
            case "info":
                handleInfo(player, args);
                break;
            case "invite":
                handleInvite(player, args);
                break;
            case "kick":
                handleKick(player, args);
                break;
            case "leave":
                handleLeave(player);
                break;
            case "accept":
                handleAccept(player, args);
                break;
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    private void handleCreate(Player player, String[] args) {
        if (!player.hasPermission("fractions.admin")) {
            player.sendMessage("§cУ вас нет прав!");
            return;
        }
        
        if (args.length < 3) {
            player.sendMessage("/fractions create <id> <префикс>");
            return;
        }
        
        String id = args[1];
        String prefix = args[2];
        
        if (plugin.getFractionManager().createFraction(id, prefix, player.getUniqueId())) {
            player.sendMessage("§fФракция §9" + id + " §fуспешно создана!");
        } else {
            player.sendMessage("§fФракция с таким ID уже существует!");
        }
    }
    
    private void handleDelete(Player player, String[] args) {
        if (!player.hasPermission("fractions.admin")) {
            player.sendMessage("§cУ вас нет прав!");
            return;
        }
        
        if (args.length < 2) {
            player.sendMessage("/fractions delete <id>");
            return;
        }
        
        String id = args[1];
        
        if (plugin.getFractionManager().deleteFraction(id)) {
            player.sendMessage("§fФракция §9" + id + " §fуспешно удалена!");
        } else {
        }
    }
    
    private void handleInfo(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("/fractions info <название>");
            return;
        }
        
        String id = args[1];
        var fraction = plugin.getFractionManager().getFraction(id);
        
        if (fraction == null) {
            player.sendMessage("§fТакой фракции нету");
            return;
        }

        player.sendMessage("§fНазвание: §9" + fraction.getId());
        player.sendMessage("§fЛидер: §9" + fraction.getLeaderName());
        player.sendMessage("§fКоличество участников: §9" + fraction.getMemberCount());
        player.sendMessage("§fУчастники:");
        
        for (UUID memberUuid : fraction.getMembers()) {
            org.bukkit.OfflinePlayer offlinePlayer = org.bukkit.Bukkit.getOfflinePlayer(memberUuid);
            String playerName = offlinePlayer.getName() != null ? offlinePlayer.getName() : "Неизвестно";
            
            if (offlinePlayer.isOnline()) {
                player.sendMessage("§9- " + playerName);
            } else {
                player.sendMessage("§7- " + playerName);
            }
        }
    }
    
    private void handleInvite(Player player, String[] args) {
        if (!plugin.getFractionManager().isLeader(player.getUniqueId())) {
            player.sendMessage("§cУ вас нету прав!");
            return;
        }
        
        if (args.length < 2) {
            player.sendMessage("/fractions invite <игрок>");
            return;
        }
        
        Player target = player.getServer().getPlayer(args[1]);
        if (target == null) {
            player.sendMessage("§cИгрок не найден!");
            return;
        }
        
        if (plugin.getFractionManager().getPlayerFraction(target.getUniqueId()) != null) {
            player.sendMessage("§fЭтот игрок уже состоит в фракции!");
            return;
        }
        
        var fraction = plugin.getFractionManager().getPlayerFraction(player.getUniqueId());
        if (plugin.getFractionManager().invitePlayer(target.getUniqueId(), fraction.getId())) {
            player.sendMessage("§fУспешно!");
            target.sendMessage("§fВы были приглашены во фракцию &9" + fraction.getId());
            target.sendMessage("§fИспользуйте &9/fractions accept &fдля принятия приглашения");
        } else {
            player.sendMessage("§fТакого игрока нет!");
        }
    }
    
    private void handleKick(Player player, String[] args) {
        if (!plugin.getFractionManager().isLeader(player.getUniqueId())) {
            player.sendMessage("§cУ вас нету прав!");
            return;
        }
        
        if (args.length < 2) {
            player.sendMessage("/fractions kick <игрок>");
            return;
        }
        
        Player target = player.getServer().getPlayer(args[1]);
        if (target == null) {
            player.sendMessage("§cИгрок не найден!");
            return;
        }
        
        var fraction = plugin.getFractionManager().getPlayerFraction(player.getUniqueId());
        var targetFraction = plugin.getFractionManager().getPlayerFraction(target.getUniqueId());
        
        if (targetFraction == null || !targetFraction.getId().equals(fraction.getId())) {
            player.sendMessage("§cУ вас нету прав");
            return;
        }
        
        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage("§f/fractions leave");
            return;
        }
        
        if (plugin.getFractionManager().leaveFraction(target.getUniqueId())) {
            player.sendMessage("§fИгрок §9" + target.getName() + " §fисключен из фракции!");
            target.sendMessage("§fВы были исключены из фракции &9" + fraction.getId());
        } else {
            player.sendMessage("§cНе удалось исключить игрока!");
        }
    }
    
    private void handleLeave(Player player) {
        var fraction = plugin.getFractionManager().getPlayerFraction(player.getUniqueId());
        
        if (fraction == null) {
            player.sendMessage("§cВы не состоите в фракции!");
            return;
        }
        
        if (fraction.isLeader(player.getUniqueId())) {
            return;
        }
        
        if (plugin.getFractionManager().leaveFraction(player.getUniqueId())) {
            player.sendMessage("§fВы вышли из фракции &9" + fraction.getId());
        } else {
        }
    }
    
    private void handleAccept(Player player, String[] args) {
        if (!plugin.getFractionManager().hasInvitation(player.getUniqueId())) {
            player.sendMessage("§fУ вас нет приглашений!");
            return;
        }
        
        String fractionId = plugin.getFractionManager().getInvitation(player.getUniqueId());
        var fraction = plugin.getFractionManager().getFraction(fractionId);
        
        if (fraction == null) {
            player.sendMessage("§fФракция не найдена!");
            return;
        }
        
        if (plugin.getFractionManager().acceptInvitation(player.getUniqueId())) {
            player.sendMessage("§fВы вступили во фракцию &9" + fraction.getId());
        } else {
            player.sendMessage("§fНе удалось вступить во фракцию!");
        }
    }
    
    private void sendHelp(Player player) {
        player.sendMessage("§9/fractions info <id> §f- информация о фракции");
        player.sendMessage("§9/fractions invite <игрок> §f- пригласить игрока (лидер)");
        player.sendMessage("§9/fractions accept §f- принять приглашение");
        player.sendMessage("§9/fractions kick <игрок> §f- исключить игрока (лидер)");
        player.sendMessage("§9/fractions leave §f- выйти из фракции");
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("create", "delete", "info", "invite", "kick", "leave", "accept");
            for (String sub : subCommands) {
                if (sub.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            switch (subCommand) {
                case "delete":
                case "info":
                    for (String fractionId : plugin.getFractionManager().getAllFractions().keySet()) {
                        if (fractionId.toLowerCase().startsWith(args[1].toLowerCase())) {
                            completions.add(fractionId);
                        }
                    }
                    break;
                case "invite":
                case "kick":
                    for (Player player : sender.getServer().getOnlinePlayers()) {
                        if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                            completions.add(player.getName());
                        }
                    }
                    break;
            }
        }
        
        return completions;
    }
}
