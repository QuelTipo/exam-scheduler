package examSchedule;

import examSchedule.parser.Entity;

public class Lecture extends Entity {

	private Course course;
	private String lecture;
	private Instructor instructor;
	private long length;
	private long numStudents;
	
	public Lecture(Course c, String lec) {
		super(c.getName()+lec);
				
		this.course = c;
		this.lecture = lec;
		this.instructor = null;
		this.length = 0;	
		this.numStudents = 0;
	}

	public Lecture(Course c, String lec, Instructor ins) {
		super(c.getName()+lec);
		this.course = c;
		this.lecture = lec;
		this.instructor = ins;
		this.numStudents = 0;
	}
	
	public Lecture(Course c, String lec, long l) {
		super(c.getName()+lec);
		this.course = c;
		this.lecture = lec;
		this.length = l;
		this.numStudents = 0;
	}

	
	public Lecture(Course c, String lec, Instructor ins, long l) {
		super(c.getName()+lec);
		this.course = c;
		this.lecture = lec;
		this.instructor = ins;
		this.length = l;
		this.numStudents = 0;
	}
	
	public void update(Instructor ins) {
		this.instructor = ins;
	}
	
	public void update(long l) {
		this.length = l;
	}
	
	public void update(Instructor ins, long l) {
		this.instructor = ins;
		this.length = l;
	}

	public void addStudent() {
		this.numStudents++;
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
	
	public long getNumStudents() {
		return this.numStudents;
	}
	
	public String getExamLengthPredicate() {
		return "examLength("+this.course.getName()+","+getLecture()+","+getLength()+")";
	}
	
	
	public String toString() {
		if (course != null) {
			if (instructor != null) {
			
				return "lecture(" + this.course.getName() + "," + getLecture() + "," + this.instructor.getName() + "," + this.length + ")";
	
			}
		}
		return "";
		
	}
}
	
