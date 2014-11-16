package examSchedule;

import examSchedule.parser.Entity;
import java.util.TreeSet;

public class Course extends Entity {

	private TreeSet<Lecture> lectures;
	
	public Course(String name) {
		super(name);
		
		lectures = new TreeSet<Lecture>();
		
	}
				
	public void addLecture(Lecture lecture) {
		lectures.add(lecture);
	}
	
	public TreeSet<Lecture> getLectures() {
		return lectures;
	}
	
	public String toString() {
		return "course(" + getName() + ")";
	}
}
