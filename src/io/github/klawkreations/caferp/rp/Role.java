package io.github.klawkreations.caferp.rp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Role {
	public static Role defaultRole = null;

	private double salary;
	private String title;
	private String description;
	private HashSet<String> commands;

	public Role(double salary, String title, String desc, List<String> commands) {
		this.salary = salary;
		this.title = title;
		this.description = desc;

		this.commands = new HashSet<String>(commands);

		if (title.equalsIgnoreCase("default")) {
			defaultRole = this;
		}
	}

	public String getTitle() {
		return title;
	}

	public double getSalary() {
		return salary;
	}

	public String getDescription() {
		return description;
	}

	public boolean hasCommand(String command) {
		return commands.contains(command);
	}

	public List<String> getCommands() {
		ArrayList<String> allCommands = new ArrayList<String>();
		if(!this.equals(defaultRole)){
			allCommands.addAll(defaultRole.getCommands());
		}
		allCommands.addAll(commands);
		return allCommands;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Role) {
			Role o = (Role) other;
			return salary == o.salary && title.equals(o.getTitle());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return title.hashCode();
	}

	@Override
	public String toString() {
		if (description.isEmpty()) {
			return title;
		}
		return title + " - " + description;
	}
}
