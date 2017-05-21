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
		loadRoles(roles);
		log("Created roles " + roles.toString());
		roleManager = new RPRoles(econ, roles);
		rpCommands = new RPCommands(roleManager, econ);
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

	private boolean loadRoles(ArrayList<Role> roles) {
		try {
			YamlConfiguration rolesConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "roles.yml"));
			HashSet<String> roleNames = new HashSet<String>(rolesConfig.getKeys(false));
			for (String key : roleNames) {
				if (key.equals("default")) {
					String title = rolesConfig.getString(key + ".title",
							Character.toUpperCase(key.charAt(0)) + key.substring(1).toLowerCase());
					String desc = rolesConfig.getString(key + ".description", "");
					double salary = rolesConfig.getDouble(key + ".salary", 0);
					ArrayList<String> commands = (ArrayList<String>) rolesConfig.getList(key + ".commands",
							new ArrayList<String>());
					Role.defaultRole = new Role(salary, title, desc, commands);
				} else {
					String title = rolesConfig.getString(key + ".title",
							Character.toUpperCase(key.charAt(0)) + key.substring(1).toLowerCase());
					String desc = rolesConfig.getString(key + ".description", "");
					double salary = rolesConfig.getDouble(key + ".salary", 0);
					ArrayList<String> commands = (ArrayList<String>) rolesConfig.getList(key + ".commands",
							new ArrayList<String>());
					roles.add(new Role(salary, title, desc, commands));
				}
			}
			if(Role.defaultRole == null){
				String[] defaults = {"list", "join", "switch", "leave", "help", "payday"};
				ArrayList<String> commands = new ArrayList<String>(Arrays.asList(defaults));
				Role.defaultRole = new Role(0, "Citizen", "", commands);
			}
			return true;
		} catch (Exception e) {
			System.out.println("Could not load the config!");
		}

		return false;
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

	/**
	 * TODO: Add more cases for the messages commands send back to the player
	 * i.e. if they typed the wrong name of a player! TODO: Think of a better
	 * way to implement the null checks -.-
	 * 
	 * @param sender
	 * @param cmd
	 * @param label
	 * @param args
	 * @return
	 */
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
