package examSchedule;

import examSchedule.parser.Entity;
import java.util.Vector;

public class Lecture extends Entity {

	private Course course;
	private String lecture;
	private Instructor instructor;
	private long length;
	
	private Vector<Student> students;
	private long classSize = 0;
	
	public Lecture(Course c, String lec) {
		super(c.getName()+ " " + lec);
		this.course = c;
		this.lecture = lec;
		this.instructor = null;
		this.length = 0;	
		
		this.students = new Vector<Student>();
	}

	public Lecture(Course c, String lec, Instructor ins) {
		super(c.getName()+ " " + lec);
		this.course = c;
		this.lecture = lec;
		this.instructor = ins;
		
		this.students = new Vector<Student>();
	}
	
	public Lecture(Course c, String lec, long l) {
		super(c.getName()+ " " + lec);
		this.course = c;
		this.lecture = lec;
		this.length = l;
		
		this.students = new Vector<Student>();
	}

	
	public Lecture(Course c, String lec, Instructor ins, long l) {
		super(c.getName()+ " " + lec);
		this.course = c;
		this.lecture = lec;
		this.instructor = ins;
		this.length = l;
		
		this.students = new Vector<Student>();
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
		System.out.println("Nope");
	}

	public void addStudent(Student student) {
		this.students.add(student);
		classSize += 1;
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
	
	public long getClassSize() {
		return this.classSize;
	}
	
	public Vector<Student> getStudents() {
		return this.students;
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
	
