package examSchedule;

import examSchedule.parser.Entity;

public class Room extends Entity {

	public long capacity;
	
	public Room(String name) {
		super(name);
	}
	
	public Room(String name, long capacity) {
		super(name);
		
		this.capacity = capacity;
	}
	
	public void update(long capacity) {
		this.capacity = capacity;
	}
	
	public String getCapacityPredicate() {
		return "capacity("+getName()+","+capacity+")";
	}
	
	public String toString() {
		return "room("+getName()+")";
	}
	
}
