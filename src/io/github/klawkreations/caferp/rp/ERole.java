package io.github.klawkreations.caferp.rp;

public enum ERole {
	OFFICER("Officer", 50), LOGGER("Logger", 25), MINER("Miner", 25), ENTREPRENEUR("Entrepreneur",125), SCIENTIST(
			"Scientist", 75), CRIMINAL("Criminal", 15), CITIZEN("Citizen", 0);

	private String title;
	private double salary;

	ERole(String title, double salary) {
		this.title = title;
		this.salary = salary;
	}

	@Override
	public String toString() {
		return title;
	}

	public double getSalary() {
		return salary;
	}

	public static ERole fromName(String name) {
		for (ERole e : ERole.values()) {
			if (name.equals(e.title)){
				return e;
			}
		}
		return null;
	}
}
