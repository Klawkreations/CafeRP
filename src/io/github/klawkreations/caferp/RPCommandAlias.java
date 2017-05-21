package io.github.klawkreations.caferp;

public class RPCommandAlias {
	private String args[];
	private String name;
	private String commandToCall;
	private String description;

	public RPCommandAlias(String name, String commandToCall, String[] args, String description) {
		this.name = name;
		this.commandToCall = commandToCall;
		this.args = args;
		this.description = description;
	}

	public String[] getArgs() {
		return args;
	}

	public String getName() {
		return name;
	}

	public String getCommandToCall() {
		return commandToCall;
	}
	
	public String getDescription(){
		return description;
	}
}
