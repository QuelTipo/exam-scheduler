package examSchedule;

import examSchedule.parser.Entity;

public class Instructor extends Entity {

	public Instructor(String name) {
		super(name);
	}
	
	public String toString() {
		return "instructor(" + getName() + ")";
	}
}
