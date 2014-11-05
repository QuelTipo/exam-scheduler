package examSchedule;

import examSchedule.parser.Entity;

public class Course extends Entity {

	public Course(String name) {
		super(name);
	}
	
	public String toString() {
		return "course(" + getName() + ")";
	}
}
