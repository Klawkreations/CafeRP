package io.github.klawkreations.caferp.rp;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import io.github.klawkreations.caferp.CafeRP;
import net.milkbowl.vault.economy.Economy;

public class RPCommands {
	private Location jail;
	private Map<RolePlayer, Location> previousLocationOfJailed;
	private Map<String, RPCommand> commands;
	private RPRoles roleManager;

	private Economy econ;

	public RPCommands(RPRoles playerRoles, Economy econ) {
		previousLocationOfJailed = new HashMap<>();

		jail = null;

		this.econ = econ;
		this.roleManager = playerRoles;

		commands = new HashMap<String, RPCommand>();
		commands.put("list", new RPCommand("rp.list", 1, "Lists all current roles") {
			public String run(Player sender, String args[]) {
				check(sender, args);
				return roleManager.listRoles();
			}
		});
		commands.put("join", new RPCommand("rp.join", 2, "Joins a role") {
			public String run(Player sender, String args[]) {
				check(sender, args);
				return roleManager.joinRole(sender, args[1]);
			}
		});
		commands.put("switch", new RPCommand("rp.switch", 2, "Switches roles") {
			public String run(Player sender, String args[]) {
				check(sender, args);
				return roleManager.switchRole(sender, args[1]);
			}
		});
		commands.put("leave", new RPCommand("rp.leave", 1, "Leaves your current role") {
			public String run(Player sender, String args[]) {
				check(sender, args);
				return roleManager.leaveRole(sender.getPlayer());
			}
		});
		commands.put("help", new RPCommand("rp.help", 1, "") {
			public String run(Player sender, String args[]) {
				check(sender, args);
				String help = "";
				for(Entry<String, RPCommand> entry : commands.entrySet()){
					help += entry.getKey() + ": " + entry.getValue().toString() + "\n";
				}
				return help;
			}
		});
		
		// OFFICER COMMANDS
		commands.put("cuff", new RPCommand("rp.cuff", 2, "") {
			public String run(Player sender, String args[]) {
				check(sender, args);
				RolePlayer instigator = new RolePlayer(sender, roleManager.getRole(sender));
				Player other = CafeRP.getPlayerByName(args[1]);
				RolePlayer target = new RolePlayer(other, roleManager.getRole(other));

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
		});
		commands.put("uncuff", new RPCommand("rp.uncuff", 2, "") {
			public String run(Player sender, String args[]) {
				check(sender, args);
				RolePlayer instigator = new RolePlayer(sender, roleManager.getRole(sender));
				Player other = CafeRP.getPlayerByName(args[1]);
				RolePlayer target = new RolePlayer(other, roleManager.getRole(other));

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
		});
		commands.put("jail", new RPCommand("rp.jail", 3, "") {
			public String run(Player sender, String args[]) {
				check(sender, args);
				RolePlayer instigator = new RolePlayer(sender, roleManager.getRole(sender));
				Player other = CafeRP.getPlayerByName(args[1]);
				RolePlayer target = new RolePlayer(other, roleManager.getRole(other));

				int periodInt = 0;
				try {
					periodInt = Integer.parseInt(args[2]);
				} catch (Exception e) {
					return args[2] + " is not a valid time!";
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
					return "Successfully jailed " + target.getName() + " for " + periodInt + " seconds!";
				}
				return "Unable to send " + target.getName() + " to jail!";
			}
		});
		commands.put("release", new RPCommand("rp.release", 2, "") {
			public String run(Player sender, String args[]) {
				check(sender, args);
				RolePlayer instigator = new RolePlayer(sender, roleManager.getRole(sender));
				Player other = CafeRP.getPlayerByName(args[1]);
				RolePlayer target = new RolePlayer(other, roleManager.getRole(other));
				
				if (previousLocationOfJailed.containsKey(target) && instigator.hasCommand("unjail")) {
					if (previousLocationOfJailed.containsKey(target)) {
						target.teleport(previousLocationOfJailed.get(target));
						previousLocationOfJailed.remove(target);
						return "Successfully freed " + target.getName() + "!";
					}
				}
				return "Unable to free " + target.getName() + "! Maybe they are not in jail?";
			}
		});
		// CASTER COMMANDS
		commands.put("leap", new RPCommand("rp.leap", 2, "") {
			public String run(Player sender, String args[]) {
				Role role = roleManager.getRole(sender);
				if(role == null || !role.hasCommand("leap")){
					return "You cannot leap!";
				}
				int multiplier = 0;
				try {
					multiplier = Integer.parseInt(args[1]);
				} catch (Exception e) {
					return args[1] + " is not a valid scalar!";
				}
				sender.setVelocity(sender.getLocation().getDirection().multiply(multiplier));
				return "You leap forward!";
			}
		});
		// ADMIN COMMANDS
		commands.put("setjail", new RPCommand("rp.setjail", 1, "") {
			public String run(Player sender, String args[]) {
				if (jail == null) {
					jail = sender.getLocation();
					return "Successfully set jail at current location!";
				}
				return "Unable to set a jail as one already exists! Use /rp remove jail to remove the current one!";
			}
		});
		commands.put("rmjail", new RPCommand("rp.rmjail	", 1, "") {
			public String run(Player sender, String args[]) {
				jail = null;
				return "Removed the current jail!";
			}
		});
		
	}

	public String command(Player sender, String args[]) {
		if (args.length > 0 && commands.containsKey(args[0])) {
			return commands.get(args[0]).run(sender, args);
		}
		return "Invalid command";
	}
}
