package examSchedule;

import java.util.TreeSet;
import examSchedule.parser.Entity;
import examSchedule.parser.Pair;

public class Student extends Entity {

	private TreeSet<Pair<Course, Lecture>> courses;
	
	public Student(String name) {
		super(name);
		
		courses = new TreeSet<Pair<Course, Lecture>>();
	}
	
	public String toString() {
		return "student("+getName()+")";
	}
	
	public String getEnrolledPredicates() {
		String es = "enrolled(" + getName() + ",[";
		for (Pair<Course, Lecture> pair : this.courses) {
			es = es + pair.getKey().getName() + "," + pair.getValue().getLecture() + ",";
		}
		es = es.substring(0, es.length() - 1) + "])";
		return es;
	}
	
	public boolean checkForCourse(Course course, Lecture lecture) {
		for (Pair<Course, Lecture> pair : this.courses) {
			if ((pair.getKey().getName().equals(course.getName())) && (pair.getValue().getName().equals(lecture.getName()))) {
				return true;
			}
		}
		return false;
	}
	
	public void addCourse(Course course, Lecture lecture) {
		this.courses.add(new Pair<Course, Lecture>(course, lecture));
	}
	
	public TreeSet<Pair<Course, Lecture>> getCourses() {
		return courses;
	}
}
