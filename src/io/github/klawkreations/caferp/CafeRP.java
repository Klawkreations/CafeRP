package io.github.klawkreations.caferp;

import java.io.File;
import java.util.ArrayList;
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
import io.github.klawkreations.caferp.rp.roles.RolePlayer;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public final class CafeRP extends JavaPlugin implements Listener {
	private RPRoles roleManager;
	private RPCommands rpCommands;

	private static final Logger log = Logger.getLogger("Minecraft");
	private static Economy econ = null;
	private static Permission perms = null;
	private static Chat chat = null;

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);

		if (false && !setupEconomy()) {
			log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		// setupPermissions();
		// setupChat();

		getDataFolder().mkdir(); // Makes the resouece folder

		ArrayList<Role> roles = new ArrayList<Role>();
		loadRoles(roles);
		getLogger().info("Created roles " + roles.toString());
		roleManager = new RPRoles(econ, roles);
		rpCommands = new RPCommands(roleManager, econ);

		// getLogger().info("onEnable has been invoked!");

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
				
				String title = rolesConfig.getString(key + ".title",
						Character.toUpperCase(key.charAt(0)) + key.substring(1).toLowerCase());
				double salary = rolesConfig.getDouble(key + ".salary", 0);
				ArrayList<String> commands = (ArrayList<String>) rolesConfig.getList(key + ".commands",
						new ArrayList<String>());
				roles.add(new Role(salary, title, commands));
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

	// private boolean setupChat() {
	// RegisteredServiceProvider<Chat> rsp =
	// getServer().getServicesManager().getRegistration(Chat.class);
	// chat = rsp.getProvider();
	// return chat != null;
	// }

	// private boolean setupPermissions() {
	// RegisteredServiceProvider<Permission> rsp =
	// getServer().getServicesManager().getRegistration(Permission.class);
	// perms = rsp.getProvider();
	// return perms != null;
	// }

	public static Economy getEcononomy() {
		return econ;
	}

	// public static Permission getPermissions() {
	// return perms;
	// }
	//
	// public static Chat getChat() {
	// return chat;
	// }

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
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (cmd.getName().equalsIgnoreCase("rp")) {
				if (args.length > 0) {
					if (args[0].equalsIgnoreCase("list") && player.hasPermission("rp.list") && args.length == 1) {
						sender.sendMessage(roleManager.listRoles());
						return true;
					} else if (args[0].equalsIgnoreCase("join") && player.hasPermission("rp.join")
							&& args.length == 2) {
						sender.sendMessage(roleManager.joinRole(player, args[1]));
						return true;
					} else if (args[0].equalsIgnoreCase("switch") && player.hasPermission("rp.switch")
							&& args.length == 2) {
						sender.sendMessage(roleManager.switchRole(player, args[1]));
						return true;
					} else if (args[0].equalsIgnoreCase("leave") && player.hasPermission("rp.leave")
							&& args.length == 1) {
						sender.sendMessage(roleManager.leaveRole(player.getPlayer()));
						return true;
					} else if (args[0].equalsIgnoreCase("cuff") && player.hasPermission("rp.cuff")
							&& args.length == 2) {
						RolePlayer rplayer = new RolePlayer(player, roleManager.getRole(player));
						Player other = getPlayerByName(args[1]);
						RolePlayer rother = new RolePlayer(other, roleManager.getRole(other));
						sender.sendMessage(rpCommands.cuff(rplayer, rother));
						return true;
					} else if (args[0].equalsIgnoreCase("uncuff") && player.hasPermission("rp.uncuff")
							&& args.length == 2) {
						RolePlayer rplayer = new RolePlayer(player, roleManager.getRole(player));
						Player other = getPlayerByName(args[1]);
						RolePlayer rother = new RolePlayer(other, roleManager.getRole(other));
						sender.sendMessage(rpCommands.unCuff(rplayer, rother));
						return true;
					} else if (args[0].equalsIgnoreCase("jail") && player.hasPermission("rp.jail")
							&& args.length == 3) {
						RolePlayer rplayer = new RolePlayer(player, roleManager.getRole(player));
						Player other = getPlayerByName(args[1]);
						RolePlayer rother = new RolePlayer(other, roleManager.getRole(other));
						sender.sendMessage(rpCommands.jailPlayer(rplayer, rother, args[2]));
						return true;
					} else if (args[0].equalsIgnoreCase("release") && player.hasPermission("rp.release")
							&& args.length == 2) {
						RolePlayer rplayer = new RolePlayer(player, roleManager.getRole(player));
						Player other = getPlayerByName(args[1]);
						RolePlayer rother = new RolePlayer(other, roleManager.getRole(other));
						sender.sendMessage(rpCommands.unjailPlayer(rplayer, rother));
						return true;
					} else if (args[0].equalsIgnoreCase("setjail") && player.hasPermission("rp.setjail")
							&& args.length == 1) {
						sender.sendMessage(rpCommands.setJail(player));
						return true;
					} else if (args[0].equalsIgnoreCase("rmjail") && player.hasPermission("rp.rmjail")
							&& args.length == 1) {
						sender.sendMessage(rpCommands.removeJail());
						return true;
					}
				}
				// TODO: Add colors
				sender.sendMessage("Possible commands are:\n" + "/rp list\n" + "/rp join [role]\n"
						+ "/rp switch [role]\n" + "/rp leave\n" + "/rp cuff [playername]\n"
						+ "/rp uncuff [playername]\n" + "/rp jail [playername] [periodinseconds]\n"
						+ "/rp release [playername]\n" + "/rp setjail\n" + "/rp rmjail");
				return true;
			}
			return false;
		}
		return false;
	}

	public Player getPlayerByName(String playerName) {
		for (Player players : Bukkit.getOnlinePlayers()) {
			if (players.getName().equalsIgnoreCase(playerName)) {
				return players;
			}
		}
		return null;
	}
}
