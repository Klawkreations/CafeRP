package io.github.klawkreations.caferp.rp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import net.milkbowl.vault.economy.Economy;

public class RPRoles {
	private ArrayList<Role> roles;
	private HashMap<Player, RolePlayer> playerRoles;
	private HashMap<Role, Team> teams;
	private ScoreboardManager manager;
	private Scoreboard board;

	private final int PAYOUT_PERIOD = (60 * 10);
	private RPTimer payoutTimer;

	private Economy econ;

	public RPRoles(Economy econ, ArrayList<Role> roles) {
		this.roles = new ArrayList<Role>(roles);
		playerRoles = new HashMap<Player, RolePlayer>();

		manager = Bukkit.getScoreboardManager();
		board = manager.getNewScoreboard();

		Objective objective = board.registerNewObjective("showhealth", "health");
		objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
		objective.setDisplayName("/ 20");

		teams = new HashMap<Role, Team>();
		for (Role role : roles) {
			Team team = board.registerNewTeam(role.getTitle());
			team.setPrefix("[" + role.getTitle() + "] ");
			teams.put(role, team);
		}

		for (Player players : Bukkit.getOnlinePlayers()) {
			players.setScoreboard(board);
		}

		this.econ = econ;
		
		payoutTimer = new RPTimer(PAYOUT_PERIOD, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Bukkit.broadcastMessage("It's payday!");
				for (RolePlayer rp : playerRoles.values()) {
					payOutSalary(rp);
				}
			}
		});
		payoutTimer.start();
	}

	public String listRoles() {
		String titles = "Available roles:\n";
		for (Role role : roles) {
			titles += role.toString() + "\n";
		}
		return titles;
	}

	public String joinRole(Player player, String roleID) {
		if (playerRoles.containsKey(player)) {
			switchRole(player, roleID);
		}

		try {
			Role role = getRoleByName(roleID);
			if (role == null) {
				throw new Exception("Role " + roleID + " does not exist");
			}
			playerRoles.put(player, new RolePlayer(player, role));
			teams.get(role).addPlayer(player);
			return "You have joined the " + role.getTitle() + " role!";
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return "The role you attempted to join does not exist!";
		}
	}

	public Role getRole(Player player) {
		if (playerRoles.containsKey(player)) {
			return playerRoles.get(player).getRole();
		}
		return Role.defaultRole;
	}

	public String getRoleDescription(String role) {
		return getRoleByName(role).getDescription();
	}
	
	public RolePlayer getRolePlayer(Player p){
		return new RolePlayer(p, getRole(p));
	}

	public String switchRole(Player player, String roleID) {
		leaveRole(player);
		return joinRole(player, roleID);
	}

	public String leaveRole(Player player) {
		if (playerRoles.containsKey(player)) {
			teams.get(playerRoles.get(player).getRole()).removePlayer(player);
			playerRoles.remove(player);
			return "You left your role!";
		}
		return "You do not have a role!";
	}

	public void payOutSalary(RolePlayer rp) {
		econ.depositPlayer(rp.getPlayer(), rp.getRole().getSalary());
		rp.getPlayer().sendMessage("$" + rp.getRole() + " has been deposited into your account.");
	}

	public void disable() {
		payoutTimer.stop();
	}

	public Role getRoleByName(String name) {
		name = Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
		for (Role role : roles) {
			if (role.getTitle().equals(name)) {
				return role;
			}
		}
		return null;
	}	
	
	public String timeToPayday(){
		return payoutTimer.timeLeft();
	}
}
