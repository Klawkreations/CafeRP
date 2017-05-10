package io.github.klawkreations.caferp.rp;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class RPCommands {
    private RPRoles playerRoles;
    private Location jail;
    private Map<Player, Location> previousLocation;

    private Economy econ;

    //TODO: Refactor this class... make it beautiful
    public RPCommands(RPRoles playerRoles, Economy econ){
        this.playerRoles = playerRoles;
        previousLocation = new HashMap<>();

        jail = null;

        this.econ = econ;
    }

    // Helper methods
    public Player getPlayerByName(String playerName){
        for (Player players : Bukkit.getOnlinePlayers()){
            if (players.getName().equalsIgnoreCase(playerName)){
                return players;
            }
        }
        return null;
    }

    // Officer commands
    public String cuff(Player instigator, String targetName) {
        Player target = getPlayerByName(targetName);

        if (target == null) {
            return "Unable to cuff targeted player as they may not exist";
        }

        if (instigator.getLocation().distance(target.getLocation()) < 5.00 &&
                playerRoles.getRole(instigator) == ERole.OFFICER){
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10000, 100));
            return "Successfully cuffed " + target.getName() + "!";
        }
        return "Unable to cuff " + target.getName() + "!";
    }

    public String unCuff(Player instigator, String targetName) {
        Player target = getPlayerByName(targetName);

        if (target == null) {
            return "Unable to uncuff targeted player as they may not exist";
        }

        if (instigator.getLocation().distance(target.getLocation()) < 5.00 &&
                playerRoles.getRole(instigator) == ERole.OFFICER){
            target.removePotionEffect(PotionEffectType.SLOW);
            return "Successfully uncuffed " + target.getName() + "!";
        }
        return "Unable to uncuff " + target.getName() + "!";
    }

    public String jailPlayer(Player instigator, String targetName, String period) {
        Player target = getPlayerByName(targetName);

        if (target == null) {
            return "Unable to jail targeted player as they may not exist";
        }

        if (jail == null){
            return "Unable to jail " + target.getName() + " since a jail has not been set by an admin!";
        }
        // TODO: Modularize this period with a config file!
        // TODO: Implement an isInt method and check for it
        else if (Integer.parseInt(period) > 600) {
            return "Unable to jail " + target.getName() + " , period must be shorter than 600 seconds!";
        }

        if (instigator.getLocation().distance(target.getLocation()) < 5.00 &&
                playerRoles.getRole(instigator) == ERole.OFFICER && target.hasPotionEffect(PotionEffectType.SLOW)){

            previousLocation.put(target, target.getLocation());
            target.teleport(jail);
            // Add support for period
            return "Successfully jailed " + target.getName() + " for " + Integer.parseInt(period) + " seconds!";
        }
        return "Unable to send " + target.getName() + " to jail!";
    }

    public String unjailPlayer(Player instigator, String targetName) {
        Player target = getPlayerByName(targetName);

        if (target == null) {
            return "Unable to free targeted player as they may not exist";
        }

        if (previousLocation.containsKey(target) && playerRoles.getRole(instigator) == ERole.OFFICER){
            target.teleport(previousLocation.get(target));
            previousLocation.remove(target);
            return "Successfully freed " + target.getName() + "!";
        }
        return "Unable to free " + target.getName() + "! Maybe they are not in jail?";
    }

    // Logger commands
    // Miner commands
    // Entrepreneur commands
    public String invest(){
        return null;
    }
    // Scientist commands
    public Item createChemical() {
        return null;
    }

    // Criminal commands
    // Admin Commands
    public String setJail(Player player) {
        if (jail == null) {
            jail = player.getLocation();
            return "Successfully set jail at current location!";
        }
        return "Unable to set a jail as one already exists! Use /rp remove jail to remove the current one!";
    }

    public String removeJail() {
        jail = null;
        return "Removed the current jail!";
    }
}
