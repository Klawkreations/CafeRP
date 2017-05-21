package io.github.klawkreations.caferp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Logger;
import java.util.Arrays;

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
	private RPRoles roleManager;
	private RPCommands rpCommands;

	private static final Logger log = Logger.getLogger("Minecraft");
	private static Economy econ = null;

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);

		if (false && !setupEconomy()) {
			log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		getDataFolder().mkdir();

		ArrayList<Role> roles = new ArrayList<Role>();
		ArrayList<RPCommandAlias> aliases = loadRoles(roles); 
		log("Created roles " + roles.toString());
		
		roleManager = new RPRoles(econ, roles);
		rpCommands = new RPCommands(roleManager, econ, aliases);
		
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

	private ArrayList<RPCommandAlias> loadRoles(ArrayList<Role> roles) {
		ArrayList<RPCommandAlias> aliases = new ArrayList<RPCommandAlias>();
		try {
			YamlConfiguration rolesConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "roles.yml"));
			HashSet<String> roleNames = new HashSet<String>(rolesConfig.getKeys(false));
			for (String key : roleNames) {
				if (key.equals("default")) {
					Role.defaultRole = setupRole(rolesConfig, key, aliases);
				} else {
					roles.add(setupRole(rolesConfig, key, aliases));
				}
			}
			if (Role.defaultRole == null) {
				String[] defaults = { "list", "join", "switch", "leave", "help", "payday" };
				ArrayList<String> commands = new ArrayList<String>(Arrays.asList(defaults));
				Role.defaultRole = new Role(0, "Citizen", "", commands);
			}
		} catch (Exception e) {
			System.out.println("Could not load the config!");
		}

		return aliases;
	}

	private Role setupRole(YamlConfiguration config, String key, ArrayList<RPCommandAlias> aliases) {
		String title = config.getString(key + ".title",
				Character.toUpperCase(key.charAt(0)) + key.substring(1).toLowerCase());
		String desc = config.getString(key + ".description", "");
		double salary = config.getDouble(key + ".salary", 0);
		ArrayList<String> commands = new ArrayList<String>();
		for (Object item : config.getList(key + ".commands", new ArrayList<String>())) {
			if (item instanceof String) {
				String command = (String) item;
				if (command.contains("-")) { // If it is an alias
					String[] split = command.split("-", 3);
					if(split.length == 3){
						String commandName = split[0].trim();
						split[1] = split[1].trim();
						String commandToCall = split[1].split(" ")[0].trim();
						String[] args = split[1].split(" ");
						for(String arg : args){
							arg = arg.trim();
						}
						String description = split[2].trim();
						aliases.add(new RPCommandAlias(commandName, commandToCall, args, description));
						commands.add(commandName);
					}
				} else {
					commands.add(command);
				}
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
}
