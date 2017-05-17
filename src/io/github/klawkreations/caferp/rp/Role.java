package io.github.klawkreations.caferp.rp;

import java.util.HashSet;
import java.util.List;

public class Role {

	private double salary;
	private String title;
	private HashSet<String> commands;
		
	public Role(double salary, String title, List<String> commands){
		this.salary = salary;
		this.title = title;
		
		this.commands = new HashSet<String>(commands);
	}
	
	public String getTitle() {
		return title;
	}

	public double getSalary() {
		return salary;
	}
	
	public boolean hasCommand(String command){
		return commands.contains(command);
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof Role){
			Role o = (Role)other;
			return salary == o.salary && title.equals(o.getTitle());
		}
		return false;
	}
	
	@Override 
	public int hashCode(){
		return title.hashCode();
	}
	
	@Override
	public String toString(){
		return title;
	}
}
