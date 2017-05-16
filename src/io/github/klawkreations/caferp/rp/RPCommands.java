package io.github.klawkreations.caferp.rp;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.github.klawkreations.caferp.rp.roles.RolePlayer;
import net.milkbowl.vault.economy.Economy;

public class RPCommands {
	private Location jail;
	private Map<RolePlayer, Location> previousLocationOfJailed;

	private Economy econ;

	public RPCommands(RPRoles playerRoles, Economy econ) {
		previousLocationOfJailed = new HashMap<>();

		jail = null;

		this.econ = econ;
	}

	// Officer commands
	public String cuff(RolePlayer instigator, RolePlayer target) {
		if (instigator.getLocation().distance(target.getLocation()) < 5.00 && instigator.hasCommand("cuff")) {
			if (target.getPlayer() == null) {
				return "Unable to cuff " + target.getName();
			} else {
				target.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10000, 100));
				return "Successfully cuffed " + target.getName() + "!";
			}
		}
		return "Unable to cuff " + target.getName() + "!";
	}

	public String unCuff(RolePlayer instigator, RolePlayer target) {
		if (instigator.getLocation().distance(target.getLocation()) < 5.00 && instigator.hasCommand("uncuff")) {
			if (target.getPlayer() == null) {
				return "Unable to uncuff targeted player as they may not exist";
			} else {
				target.getPlayer().removePotionEffect(PotionEffectType.SLOW);
				return "Successfully uncuffed " + target.getName() + "!";
			}
		}
		return "Unable to uncuff " + target.getName() + "!";
	}

	public String jailPlayer(RolePlayer instigator, RolePlayer target, String period) {
		int periodInt = 0;
		try {
			periodInt = Integer.parseInt(period);
		} catch (Exception e) {
			return period + " is not a valid time!";
		}

		if (jail == null) {
			return "Unable to jail " + target.getName() + " since a jail has not been set by an admin!";
		} else if (periodInt > 600) {
			return "Unable to jail " + target.getName() + " , period must be shorter than 600 seconds!";
		}

		if (instigator.getLocation().distance(target.getLocation()) < 5.00 && instigator.hasCommand("jail")
				&& previousLocationOfJailed.containsKey(target)) {

			previousLocationOfJailed.put(target, target.getLocation());
			target.teleport(jail);
			// Add support for period
			return "Successfully jailed " + target.getName() + " for " + Integer.parseInt(period) + " seconds!";
		}
		return "Unable to send " + target.getName() + " to jail!";
	}

	public String unjailPlayer(RolePlayer instigator, RolePlayer target) {
		if (previousLocationOfJailed.containsKey(target) && instigator.hasCommand("unjail")) {
			if (previousLocationOfJailed.containsKey(target)) {
				target.teleport(previousLocationOfJailed.get(target));
				previousLocationOfJailed.remove(target);
				return "Successfully freed " + target.getName() + "!";
			}
		}
		return "Unable to free " + target.getName() + "! Maybe they are not in jail?";
	}

	// Entrepreneur commands
	public String invest() {
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

	// Helper methods

}
