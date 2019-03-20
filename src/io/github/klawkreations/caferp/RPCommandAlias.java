package io.github.klawkreations.caferp;

public class RPCommandAlias {
	private String args[];
	private String name;
	private String commandToCall;
	private String description;
	private String returnValue;

	public RPCommandAlias(String name, String returnValue, String commandToCall, String[] args, String description) {
		this.name = name;
		this.commandToCall = commandToCall;
		this.args = args;
		this.description = description;
		this.returnValue = returnValue;
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
	
	public String getReturnValue(){
		return returnValue;
	}
	
	public String getDescription(){
		return description;
	}
}
