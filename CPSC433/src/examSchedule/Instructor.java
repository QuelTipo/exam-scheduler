package examSchedule;

import java.util.TreeSet;

import examSchedule.parser.Entity;
import examSchedule.parser.Pair;

public class Instructor extends Entity {

	private TreeSet<Pair<Course, Lecture>> courses;
	
	public Instructor(String name) {
		super(name);
		
		courses = new TreeSet<Pair<Course,Lecture>>();
	}
	
	public boolean checkForCourse(Course course, Lecture lecture) {
		for (Pair<Course, Lecture> pair : this.courses) {
			if ((pair.getKey().getName().equals(course.getName())) && (pair.getValue().getName().equals(lecture.getName()))) {
				return true;
			}
		}
		return false;
	}
	
	public String getInstructsPredicates() {
		String is = "";
		for (Pair<Course, Lecture> pair : this.courses) {
			is = is + "instructs("+getName()+","+pair.getKey().getName()+","+pair.getValue().getLecture()+")\n";
		}
		return is;
	}
	
	public void addCourse(Course course, Lecture lecture) {
		this.courses.add(new Pair<Course, Lecture>(course, lecture));
	}
	
	public String toString() {
		return "instructor(" + getName() + ")";
	}
}
