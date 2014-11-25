package examSchedule;

import examSchedule.parser.Entity;
import java.util.TreeSet;

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
		this.length = 0;
		
	}

	public Session(String s, Room r) {
		super(s);
		
		this.room = r;
	}
	
	public Session(String s, Day d) {
		super(s);
		
		this.day = d;
	}

	public Session(String s, long v, boolean x) {
		super(s);
		
		if (x)
			this.time = v;
		else
			this.length = v;
	}


	public Session(String s, Day d, long t, long l) {
		super(s);
		
		this.day = d;
		this.time = t;
		this.length = l;
	}
	
	public Session(String s, Room r, Day d, long t, long l) {
		super(s);
		
		this.room = r;
		this.day = d;
		this.time = t;
		this.length = l;
	}

	public void update(Room r) {
		this.room = r;
	}
	
	public void update(Day d) {
		this.day = d;
	}

	public void update(long v, boolean x) {
		if (x)
			this.time = v;
		else
			this.length = v;
	}
	
	public void update(Day d, long t, long l) {
		this.day = d;
		this.time = t;
		this.length = l;
	}
	
	public void update(Room r, Day d, long t, long l) {
		this.room = r;
		this.day = d;
		this.time = t;
		this.length = l;
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
		
	public String getAtPredicate() {
		return "at("+getName()+","+day.getName()+","+time+","+length+")";
	}
	
	public String toString() {
		return "session(" + getName() + "," + this.room.getName() + "," + this.day.getName() + "," + this.time + "," + this.length + ")";
	}
	
}
