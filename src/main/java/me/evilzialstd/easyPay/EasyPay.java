package me.evilzialstd.easyPay;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class EasyPay extends JavaPlugin implements CommandExecutor, Listener {

    private Map<UUID, Integer> balances = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("balance").setExecutor(this);
        getCommand("pay").setExecutor(this);
        getLogger().info("enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("disabled.");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        if (!balances.containsKey(id)) {
            balances.put(id, 100);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // Is the sender a player?
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        // UUID player
        UUID uuid = player.getUniqueId();
        int bal = balances.getOrDefault(uuid, 0);

        // /balance
        if (cmd.getName().equalsIgnoreCase("balance")) {
            player.sendMessage("Balance: " + bal);
            return true;
        }

        // Check format
        if (args.length < 2) {
            player.sendMessage("Usage: /pay <player> <amount>");
            return true;
        }


        // Target player
        Player target = getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage("That player is offline. You can't send coins right now.");
            return true;
        }


        // Amount to send
        int transfer;
        try {
            transfer = Integer.parseInt(args[1]);
        } catch (NumberFormatException e){
            player.sendMessage("Did you type that right?");
            return true;
        }

        UUID uuidTarget = target.getUniqueId();
        int balTarget = balances.getOrDefault(uuidTarget, 0);
        // /pay
        if (cmd.getName().equalsIgnoreCase("pay")) {
            if (bal >= transfer) {
                balances.put(uuid, bal - transfer);
                player.sendMessage("You sent that " + args[0] + " " + transfer + " coins.");

                balances.put(uuidTarget, balTarget + transfer);
                target.sendMessage(player.getName() + " sent you " + transfer + " coins.");
            } else {
                player.sendMessage("You don't have enough money for that. Your balance: " + bal);
            }
        }


        return true;
    }
}
