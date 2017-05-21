package io.github.klawkreations.caferp.rp;

import org.bukkit.entity.Player;

public class RPCommand  {
	private String permission;
	private String description;
	private int numArgs;
	
	public RPCommand(String permission, int numArgs, String description){
		this.permission = permission;
		this.numArgs = numArgs;
		this.description = description;
	}
	
	public boolean check(Player p, String args[]){
		return p.getPlayer().hasPermission(permission)  && args.length == numArgs;
	}
	
	public String run(Player sender, String args[]){
		return "";
	}
	
	@Override
	public String toString(){
		return description;
	}
}
