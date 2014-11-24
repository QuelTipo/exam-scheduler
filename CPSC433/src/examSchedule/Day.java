package examSchedule;

import examSchedule.parser.Entity;

import java.util.Vector;

public class Day extends Entity {

	private Vector<Session> sessions = new Vector<Session>();
	
	public Day(String name) {
		super(name);
	}
	
	public void addSession(Session session) {
		sessions.add(session);
	}
	
	public Vector<Session> getSessions() {
		return this.sessions;
	}
	
	public String toString() {
		return "day("+getName()+")";
	}
	
}
