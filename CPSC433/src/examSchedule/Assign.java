package examSchedule;

import examSchedule.parser.Entity;

public class Assign extends Entity {

	private Lecture lecture;
	private Session session;
	
	public Assign(Lecture l, Session s) {
		super(l.getName()+s.getName());
		
		this.lecture = l;
		this.session = s;
	}
	
	
	
	public String toString() {
		return "assign(" + this.lecture.getCourse().getName() + "," + this.lecture.getLecture() + "," + this.session.getName() + ")";
	}
}
