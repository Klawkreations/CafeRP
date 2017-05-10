package io.github.klawkreations.caferp;

import io.github.klawkreations.caferp.rp.RPCommands;
import io.github.klawkreations.caferp.rp.RPRoles;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class CafeRP extends JavaPlugin implements Listener {
    private RPRoles roleManager;
    private RPCommands rpManager;

    @Override
    public void onEnable(){
        roleManager = new RPRoles();
        rpManager = new RPCommands(roleManager);

        getServer().getPluginManager().registerEvents(this, this);

        getLogger().info("onEnable has been invoked!");
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        roleManager.leaveRole(event.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        roleManager.leaveRole(event.getPlayer());
    }

    /**
     * TODO: Add more cases for the messages commands send back to the player i.e. if they typed the wrong name of a player!
     * @param sender
     * @param cmd
     * @param label
     * @param args
     * @return
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (cmd.getName().equalsIgnoreCase("rp")) {
            if (args[0].equalsIgnoreCase("list") && args[0] != null){
                sender.sendMessage(roleManager.listRoles());
            }
            else if (args[0].equalsIgnoreCase("commands") && args[0] != null) {
                sender.sendMessage("Possible commands are: /rp join, /rp switch, /rp leave, /rp cuff, /rp uncuff");
            }
            else if (args[0].equalsIgnoreCase("join")) {
                Player player = (Player)sender;
                sender.sendMessage(roleManager.joinRole(player, args[1]));
            }
            else if (args[0].equalsIgnoreCase("switch")) {
                Player player = (Player)sender;
                sender.sendMessage(roleManager.switchRole(player, args[1]));
            }
            else if (args[0].equalsIgnoreCase("leave")) {
                Player player = (Player)sender;
                sender.sendMessage(roleManager.leaveRole(player));
            }
            else if (args[0].equalsIgnoreCase("cuff")) {
                Player player = (Player)sender;
                sender.sendMessage(rpManager.cuff(player, args[1]));
            }
            else if (args[0].equalsIgnoreCase("uncuff")){
                Player player = (Player)sender;
                sender.sendMessage(rpManager.unCuff(player, args[1]));
            }
            return true;
        }
        else {
            return false;
        }
    }
}
