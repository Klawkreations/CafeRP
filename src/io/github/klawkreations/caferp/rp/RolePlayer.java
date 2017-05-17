package io.github.klawkreations.caferp.rp;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RolePlayer {
	private Role role;
	private Player player;
	
	public RolePlayer(Player player, Role role){
		this.role = role;
		this.player = player;
	}

	public Role getRole() {
		return role;
	}

	public void assignRole(Role role){
        this.role = role;
    }

	public Player getPlayer() {
		return player;
	}

	public Location getLocation() {
		return player.getLocation();
	}
	
	public boolean teleport(Location location){
		return player.teleport(location);
	}

	public String getName() {
		return player.getName();
	}
	
	public boolean hasCommand(String command){
		return role.hasCommand(command);
	}

	@Override
	public boolean equals(Object other){
		if(other instanceof RolePlayer){
			RolePlayer o = (RolePlayer) other;
			return role.equals(o.getRole()) && player.equals(o.getPlayer());
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return player.hashCode();
	}
}
