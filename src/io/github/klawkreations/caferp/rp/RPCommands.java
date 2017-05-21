package io.github.klawkreations.caferp.rp;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
		add(new RPCommand("list", "rp.list", 1, "Lists all current roles") {
			public String run(RolePlayer sender, String args[]) {
				return roleManager.listRoles();
			}
		});
		add(new RPCommand("join", "rp.join", 2, "Joins a role") {
			public String run(RolePlayer sender, String args[]) {
				return roleManager.joinRole(sender.getPlayer(), args[1]);
			}
		});
		add(new RPCommand("switch", "rp.switch", 2, "Switches roles") {
			public String run(RolePlayer sender, String args[]) {
				return roleManager.switchRole(sender.getPlayer(), args[1]);
			}
		});
		add(new RPCommand("leave", "rp.leave", 1, "Leaves your current role") {
			public String run(RolePlayer sender, String args[]) {
				return roleManager.leaveRole(sender.getPlayer());
			}
		});
		add(new RPCommand("help", "rp.help", 1, "") {
			public String run(RolePlayer sender, String args[]) {
				return sender.getRole().getCommands();
			}
		});
		add(new RPCommand("payday", "rp.payday", 1, "") {
			public String run(RolePlayer sender, String args[]) {
				return roleManager.timeToPayday();
			}
		});
		// OFFICER COMMANDS
		add(new RPCommand("cuff", "rp.cuff", 2, "") {
			public String run(RolePlayer sender, String args[]) {
				RolePlayer target = roleManager.getRolePlayer(CafeRP.getPlayerByName(args[1]));

				if (sender.getLocation().distance(target.getLocation()) < 5.00) {
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
		add(new RPCommand("uncuff", "rp.uncuff", 2, "") {
			public String run(RolePlayer sender, String args[]) {
				RolePlayer target = roleManager.getRolePlayer(CafeRP.getPlayerByName(args[1]));

				if (sender.getLocation().distance(target.getLocation()) < 5.00) {
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
		add(new RPCommand("jail", "rp.jail", 3, "") {
			public String run(RolePlayer sender, String args[]) {
				RolePlayer target = roleManager.getRolePlayer(CafeRP.getPlayerByName(args[1]));

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

				if (sender.getLocation().distance(target.getLocation()) < 5.00
						&& previousLocationOfJailed.containsKey(target)) {

					previousLocationOfJailed.put(target, target.getLocation());
					target.teleport(jail);
					// Add support for period
					return "Successfully jailed " + target.getName() + " for " + periodInt + " seconds!";
				}
				return "Unable to send " + target.getName() + " to jail!";
			}
		});
		add(new RPCommand("release", "rp.release", 2, "") {
			public String run(RolePlayer sender, String args[]) {
				RolePlayer target = roleManager.getRolePlayer(CafeRP.getPlayerByName(args[1]));

				if (previousLocationOfJailed.containsKey(target)) {
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
		add(new RPCommand("leap", "rp.leap", 2, "") {
			public String run(RolePlayer sender, String args[]) {
				int multiplier = 0;
				try {
					multiplier = Integer.parseInt(args[1]);
				} catch (Exception e) {
					return args[1] + " is not a valid scalar!";
				}
				sender.getPlayer().setVelocity(sender.getLocation().getDirection().multiply(multiplier));
				return "You leap forward!";
			}
		});
		// ADMIN COMMANDS
		add(new RPCommand("setjail", "rp.setjail", 1, "") {
			public String run(RolePlayer sender, String args[]) {
				if (jail == null) {
					jail = sender.getLocation();
					return "Successfully set jail at current location!";
				}
				return "Unable to set a jail as one already exists! Use /rp remove jail to remove the current one!";
			}
		});
		add(new RPCommand("rmjail", "rp.rmjail	", 1, "") {
			public String run(RolePlayer sender, String args[]) {
				jail = null;
				return "Removed the current jail!";
			}
		});

	}

	public RPCommand get(String name) {
		return commands.get(name);
	}

	public void add(RPCommand command) {
		commands.put(command.getName(), command);
	}

	public String command(Player sender, String args[]) {
		if (args.length > 0 && commands.containsKey(args[0])) {
			RolePlayer player = roleManager.getRolePlayer(sender);
			return commands.get(args[0]).call(player, args);
		}
		return "Invalid command";
	}
}
