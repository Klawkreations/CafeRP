package io.github.klawkreations.caferp.rp.roles;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import io.github.klawkreations.caferp.rp.ERole;

public class RolePlayer {
	private ERole role;
	private Player player;
	
	public RolePlayer(Player player, ERole role){
		this.role = role;
		this.player = player;
	}

	public ERole getRole() {
		return role;
	}

	public void assignRole(ERole role){
        this.role = role;
    }

	public Player getPlayer() {
		return player;
	}

	public Location getLocation() {
		return player.getLocation();
	}

	public String getName() {
		return player.getName();
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
