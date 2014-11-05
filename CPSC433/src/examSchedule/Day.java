package examSchedule;

import examSchedule.parser.Entity;

public class Day extends Entity {

	public Day(String name) {
		super(name);
	}
	
	public String toString() {
		return "day("+getName()+")";
	}
	
}
