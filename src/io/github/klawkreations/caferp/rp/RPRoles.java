package io.github.klawkreations.caferp.rp;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class RPRoles implements ActionListener {
    private ArrayList<RPWrapper> playerRoleList;
    private ScoreboardManager manager;
    private Scoreboard board;
    private Team officerTeam, loggerTeam, minerTeam, entrepreneurTeam, scientistTeam, criminalTeam;

    private final int PAYOUT_PERIOD = 600000;
    private Timer payoutTimer;

    private Economy econ;

    public RPRoles(Economy econ){
        playerRoleList = new ArrayList<>();
        manager = Bukkit.getScoreboardManager();
        board = manager.getNewScoreboard();

        Objective objective = board.registerNewObjective("showhealth", "health");
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        objective.setDisplayName("/ 20");

        officerTeam = board.registerNewTeam("Officer");
        officerTeam.setPrefix("[Officer] ");

        loggerTeam = board.registerNewTeam("Logger");
        loggerTeam.setPrefix("[Logger] ");

        minerTeam = board.registerNewTeam("Miner");
        minerTeam.setPrefix("[Miner] ");

        entrepreneurTeam = board.registerNewTeam("Entrepreneur");
        entrepreneurTeam.setPrefix("[Entre] ");

        scientistTeam = board.registerNewTeam("Scientist");
        scientistTeam.setPrefix("[Scientist] ");

        criminalTeam = board.registerNewTeam("Criminal");
        criminalTeam.setPrefix("[Criminal] ");

        for (Player players : Bukkit.getOnlinePlayers()) {
            players.setScoreboard(board);
        }

        this.econ = econ;
        payoutTimer = new Timer(PAYOUT_PERIOD, this);
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
        if (getRole(player) != null) {
            switchRole(player, roleID);
        }

        switch (roleID.toUpperCase()) {
            case "OFFICER":
                playerRoleList.add(new RPWrapper(player, ERole.OFFICER));
                officerTeam.addPlayer(player);
                return "You joined the officer role!";
            case "LOGGER":
                playerRoleList.add(new RPWrapper(player, ERole.LOGGER));
                loggerTeam.addPlayer(player);
                return "You joined the logger role!";
            case "MINER":
                playerRoleList.add(new RPWrapper(player, ERole.MINER));
                minerTeam.addPlayer(player);
                return "You joined the miner role!";
            case "ENTREPRENEUR":
                playerRoleList.add(new RPWrapper(player, ERole.ENTREPRENEUR));
                entrepreneurTeam.addPlayer(player);
                return "You joined the entrepreneur role!";
            case "SCIENTIST":
                playerRoleList.add(new RPWrapper(player, ERole.SCIENTIST));
                scientistTeam.addPlayer(player);
                return "You joined the scientist role!";
            case "CRIMINAL":
                playerRoleList.add(new RPWrapper(player, ERole.CRIMINAL));
                criminalTeam.addPlayer(player);
                return "You joined the criminal role!";
        }
        return "The role you attempted to join does not exist!";
    }

    public ERole getRole(Player player) {
        for (RPWrapper rp : playerRoleList) {
            if (rp.getPlayer() == player) {
                return rp.getRole();
            }
        }
        return null;
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
        for (RPWrapper rp : playerRoleList) {
            if (rp.getPlayer() == player) {
                removeFromTeam(rp);
                playerRoleList.remove(rp);
                return "You left your role!";
            }
        }
        return "Unable to leave role... maybe you are not currently in one?";
    }

    public void removeFromTeam(RPWrapper rp) {
        switch (rp.getRole()) {
            case OFFICER:
                officerTeam.removePlayer(rp.getPlayer());
                break;
            case LOGGER:
                loggerTeam.removePlayer(rp.getPlayer());
                break;
            case MINER:
                minerTeam.removePlayer(rp.getPlayer());
                break;
            case ENTREPRENEUR:
                entrepreneurTeam.removePlayer(rp.getPlayer());
                break;
            case SCIENTIST:
                scientistTeam.removePlayer(rp.getPlayer());
                break;
            case CRIMINAL:
                criminalTeam.removePlayer(rp.getPlayer());
                break;
        }
    }

    public void payOutSalary(RPWrapper rp) {
        switch (rp.getRole()) {
            case OFFICER:
                econ.depositPlayer(rp.getPlayer(), 50.00);
                break;
            case LOGGER:
                econ.depositPlayer(rp.getPlayer(), 25.00);
                break;
            case MINER:
                econ.depositPlayer(rp.getPlayer(), 25.00);
                break;
            case ENTREPRENEUR:
                econ.depositPlayer(rp.getPlayer(), 125.00);
                break;
            case SCIENTIST:
                econ.depositPlayer(rp.getPlayer(), 75.00);
                break;
            case CRIMINAL:
                econ.depositPlayer(rp.getPlayer(), 15.00);
                break;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (playerRoleList != null) {
            for (RPWrapper rp : playerRoleList) {
                payOutSalary(rp);
            }
        }
    }
}
