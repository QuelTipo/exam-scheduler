package examSchedule;

import examSchedule.parser.Entity;

public class Lecture extends Entity {

	private Course course;
	private String lecture;
	private Instructor instructor;
	private long length;
	
	public Lecture(Course c, String lec) {
		super(c.getName()+lec);
				
		this.course = c;
		this.lecture = lec;
		this.instructor = null;
		this.length = 0;		
	}

	public Lecture(Course c, String lec, Instructor ins, long l) {
		super(c.getName()+lec);
		this.course = c;
		this.lecture = lec;
		this.instructor = ins;
		this.length = l;
	}
	
	public void update(Instructor ins, long l) {
		this.instructor = ins;
		this.length = l;
	}

	public Course getCourse() {
		return this.course;
	}
	
	public String getLecture() {
		return this.lecture;
	}
	
	public Instructor getInstructor() {
		return this.instructor;
	}
	
	public long getLength() {
		return this.length;
	}
	
	
	public String toString() {
		return "lecture(" + getName() + ")";
	}
}
