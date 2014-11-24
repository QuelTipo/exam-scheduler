package examSchedule;

import examSchedule.parser.Entity;

public class Room extends Entity {

	public long capacity;
	public long currentCapacity = 0; //reflects how many students currently booked in room
	
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
	
	public long getCapacity() {
		return capacity;
	}
	
	public long getCurrentCapacity() {
		return currentCapacity;
	}
	
	public void addToCurrentCapacity(long numberOfStudents) {
		currentCapacity -= numberOfStudents;
	}
	
	public void subtractFromCurrentCapacity(long numberOfStudents) {
		currentCapacity -= numberOfStudents;
	}
	
	public boolean canHold(long numberOfStudents) {
		long newTotal = currentCapacity + numberOfStudents;
		return (newTotal <= capacity) ? true : false;
	}
	
	public String getCapacityPredicate() {
		return "capacity("+getName()+","+capacity+")";
	}
	
	public String toString() {
		return "room("+getName()+")";
	}
	
}
