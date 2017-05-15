package io.github.klawkreations.caferp.rp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.Timer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import io.github.klawkreations.caferp.rp.roles.RolePlayer;
import net.milkbowl.vault.economy.Economy;

public class RPRoles implements ActionListener {
    private HashMap<Player, RolePlayer> playerRoles;
    private HashMap<ERole, Team> teams;
    private ScoreboardManager manager;
    private Scoreboard board;
    //private Team officerTeam, loggerTeam, minerTeam, entrepreneurTeam, scientistTeam, criminalTeam;
    

    private final int PAYOUT_PERIOD = (1000*60*10);
    private Timer payoutTimer;

    private Economy econ;

    public RPRoles(Economy econ){
        playerRoles = new HashMap<Player, RolePlayer>();
        
        manager = Bukkit.getScoreboardManager();
        board = manager.getNewScoreboard();

        Objective objective = board.registerNewObjective("showhealth", "health");
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        objective.setDisplayName("/ 20");

        teams = new HashMap<ERole, Team>();
        for(ERole role : ERole.values()){
        	Team team = board.registerNewTeam(role.toString());
        	team.setPrefix("["+role.toString()+"] ");
        	teams.put(role, team);
        }

        for (Player players : Bukkit.getOnlinePlayers()) {
            players.setScoreboard(board);
        }

        this.econ = econ;
        payoutTimer = new Timer(PAYOUT_PERIOD, this);
        payoutTimer.start();
    }

    public String listRoles() {
        ERole[] roles = ERole.values();
        String titles = "Available roles: \n";

        for (ERole role : roles){
            titles += role.toString() + " \n";
        }
        return titles;
    }

    public String joinRole(Player player, String roleID) {
    	// Canonical name of role
    	roleID = Character.toUpperCase(roleID.charAt(0)) + roleID.substring(1).toLowerCase();
    	
        if (playerRoles.containsKey(player)) {
            switchRole(player, roleID);
        }

        try{
        	ERole role = ERole.fromName(roleID);
        	if(role == null){
        		throw new Exception("Role " +roleID + " does not exist");
        	}
        	playerRoles.put(player, new RolePlayer(player, role));
        	teams.get(role).addPlayer(player);
        	return "You have joined the " + role.toString() + " role!";
        } catch (Exception e){
        	System.out.println(e.getMessage());
        	return "The role you attempted to join does not exist!";
        }
    }

    public ERole getRole(Player player) {
    	if(playerRoles.containsKey(player)){
    		return playerRoles.get(player).getRole();
    	}
    	return ERole.CITIZEN;
    }

    //TODO: Add descriptions for all roles
    public String getRoleDescription() {
        return null;
    }

    public String switchRole(Player player, String roleID) {
        leaveRole(player);
        return joinRole(player, roleID);
    }

    public String leaveRole(Player player) {
    	if(playerRoles.containsKey(player)){
    		teams.get(playerRoles.get(player)).removePlayer(player);
    		playerRoles.remove(player);
    		return "You left your role!";
    	}
    	return "You do not have a role!";
    }

    public void payOutSalary(RolePlayer rp) {
    	econ.depositPlayer(rp.getPlayer(), rp.getRole().getSalary());
    	rp.getPlayer().sendMessage("$"+rp.getRole()+" has been deposited into your account.");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Bukkit.broadcastMessage("It's payday!");
        for (RolePlayer rp : playerRoles.values()) {
            payOutSalary(rp);
        }
    }
}
