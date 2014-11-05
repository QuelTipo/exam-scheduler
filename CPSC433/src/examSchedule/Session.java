package examSchedule;

import examSchedule.parser.Entity;

public class Session extends Entity {

	private Room room;
	private Day day;
	private long time;
	private long length;
	
	public Session(String s) {
		super(s);
				
		this.room = null;
		this.day = null;
		this.time = 0;
		this.time = 0;
	}

	public Session(String s, Room r, Day d, long t, long l) {
		super(s);
		
		this.room = r;
		this.day = d;
		this.time = t;
		this.time = l;
	}

	public void update(Room r, Day d, long t, long l) {
		this.room = r;
		this.day = d;
		this.time = t;
		this.time = l;
	}
	
	public Room getRoom() {
		return this.room;
	}
	
	public Day getDay() {
		return this.day;
	}
	
	public long getTime() {
		return this.time;
	}
	
	public long getLength() {
		return this.length;
	}
	
	public String toString() {
		return "lecture(" + getName() + ")";
	}
}
