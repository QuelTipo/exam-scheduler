package examSchedule;

import java.util.Vector;

import examSchedule.parser.Entity;
import examSchedule.parser.Pair;

public class Instructor extends Entity {

	private Vector<Pair<Course, Lecture>> courses;
	
	public Instructor(String name) {
		super(name);
		
		courses = new Vector<Pair<Course,Lecture>>();
	}
	
	public boolean checkForCourse(Course course, Lecture lecture) {
		for (Pair<Course, Lecture> pair : this.courses) {
			if ((pair.getKey().getName().equals(course.getName())) && (pair.getValue().getName().equals(lecture.getName()))) {
				return true;
			}
		}
		return false;
	}
	
	public void getInstructsPredicates() {
		for (Pair<Course, Lecture> pair : this.courses) {
			System.out.println("instructs("+getName()+","+pair.getKey().getName()+","+pair.getValue().getLecture()+")");
		}
	}
	
	public void addCourse(Course course, Lecture lecture) {
		this.courses.add(new Pair<Course, Lecture>(course, lecture));
	}
	
	public String toString() {
		return "instructor(" + getName() + ")";
	}
}
