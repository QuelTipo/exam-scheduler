package examSchedule;

import examSchedule.parser.Entity;

public class Room extends Entity {

	public Room(String name) {
		super(name);
	}
	
	public String toString() {
		return "room("+getName()+")";
	}
	
}
