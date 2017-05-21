package io.github.klawkreations.caferp.rp;

public class RPCommand {
	private String name;
	private String permission;
	private String description;
	private int numArgs;

	public RPCommand(String name, String permission, int numArgs, String description) {
		this.name = name;
		this.permission = permission;
		this.numArgs = numArgs;
		this.description = description;
	}

	public boolean check(RolePlayer p, String args[]) {
		return p.getPlayer().hasPermission(permission) && (p.hasCommand(name) || Role.defaultRole.hasCommand(name)) && args.length == numArgs;
	}

	public String getCheckMessage(RolePlayer p, String args[]) {
		if (!p.getPlayer().hasPermission(permission)) {
			return "You don't have permission";
		}
		if (!(p.hasCommand(name) || Role.defaultRole.hasCommand(name))) {
			return "Your role doesn't have that command";
		}
		if (args.length != numArgs) {
			return "Incorrect usage";
		}
		return "";
	}

	public String call(RolePlayer sender, String args[]) {
		if (!check(sender, args)) {
			return getCheckMessage(sender, args);
		}
		return run(sender, args);
	}

	public String run(RolePlayer sender, String args[]) {
		return "";
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		if (description.isEmpty()) {
			return description;
		}
		return name + " - " + description;
	}
}
