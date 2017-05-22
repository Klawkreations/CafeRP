package io.github.klawkreations.caferp;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.klawkreations.caferp.rp.RPCommands;
import io.github.klawkreations.caferp.rp.RPRoles;
import io.github.klawkreations.caferp.rp.Role;
import net.milkbowl.vault.economy.Economy;

public final class CafeRP extends JavaPlugin implements Listener {
	private static final Logger log = Logger.getLogger("Minecraft");
	private static int payoutSeconds;

	private static Economy econ = null;

	private RPRoles roleManager;
	private RPCommands rpCommands;

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);

		if (false && !setupEconomy()) {
			log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		getDataFolder().mkdir();

		ArrayList<RPCommandAlias> aliases = loadConfig();
		
		ArrayList<Role> roles = loadRoles(); 
		log("Created roles " + roles.toString());
		
		roleManager = new RPRoles(roles);
		rpCommands = new RPCommands(roleManager, aliases);
		
	}

	@Override
	public void onDisable() {
		roleManager.disable();
		log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		roleManager.leaveRole(event.getPlayer());
	}

	@EventHandler
	public void onKick(PlayerKickEvent event) {
		roleManager.leaveRole(event.getPlayer());
	}

	private ArrayList<RPCommandAlias> loadConfig() {
		ArrayList<RPCommandAlias> aliases = new ArrayList<RPCommandAlias>();
		try {
			YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
			payoutSeconds = config.getInt("payout-period", 600);

			for (Object item : config.getList("aliases", new ArrayList<String>())) {
				if (item instanceof String) {
					String command = (String) item;

					String[] split = command.split("-");
					if (split.length == 4 || split.length == 3) {
						int index = 0;
						String commandName = split[index++].trim();
						String returnValue = null;
						if (split.length == 4) {
							returnValue = split[index++].trim();
						}
						split[index] = split[index].trim();
						String commandToCall = split[index].split(" ")[0].trim();
						String[] args = split[index++].split(" ");
						for (String arg : args) {
							arg = arg.trim();
						}
						String description = split[index].trim();
						aliases.add(new RPCommandAlias(commandName, returnValue, commandToCall, args, description));

					}

				}
			}

		} catch (Exception e) {
			System.out.println("Could not load the config.yml config!");
			e.printStackTrace();
		}
		return aliases;
	}

	private ArrayList<Role> loadRoles() {
		ArrayList<Role> roles = new ArrayList<Role>();
		try {
			YamlConfiguration rolesConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "roles.yml"));
			HashSet<String> roleNames = new HashSet<String>(rolesConfig.getKeys(false));
			for (String key : roleNames) {
				if (key.equals("default")) {
					System.out.println("Making default role");
					Role.defaultRole = setupRole(rolesConfig, key);
				} else {
					roles.add(setupRole(rolesConfig, key));
				}
			}
			if (Role.defaultRole == null) {
				System.out.println("Making default role");
				String[] defaults = { "list", "join", "switch", "leave", "help", "payday" };
				ArrayList<String> commands = new ArrayList<String>(Arrays.asList(defaults));
				Role.defaultRole = new Role(0, "Citizen", "", commands);
			}
		} catch (Exception e) {
			System.out.println("Could not load the roles.yml config!");
			e.printStackTrace();
		}
		return roles;
	}

	private Role setupRole(YamlConfiguration config, String key) {
		String title = config.getString(key + ".title",
				Character.toUpperCase(key.charAt(0)) + key.substring(1).toLowerCase());
		String desc = config.getString(key + ".description", "");
		double salary = config.getDouble(key + ".salary", 0);
		ArrayList<String> commands = new ArrayList<String>();
		for (Object item : config.getList(key + ".commands", new ArrayList<String>())) {
			if (item instanceof String) {
				String command = (String) item;
				commands.add(command);
			}
		}
		return new Role(salary, title, desc, commands);
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	public static Economy getEcononomy() {
		return econ;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("rp") && args.length > 0) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				sender.sendMessage(rpCommands.command(player, args));
				return true;
			}
		}
		return false;
	}

	public static Player getPlayerByName(String playerName) {
		for (Player players : Bukkit.getOnlinePlayers()) {
			if (players.getName().equalsIgnoreCase(playerName)) {
				return players;
			}
		}
		return null;
	}

	public static void log(String s) {
		log.info(s);
	}

	public static int getPayoutSeconds() {
		return payoutSeconds;
	}
}
