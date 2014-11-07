package examSchedule;

import java.util.TreeSet;
import examSchedule.parser.Entity;

public class Course extends Entity {

	private TreeSet<String> lectures;
	
	public Course(String name) {
		super(name);
		
		lectures = new TreeSet<String>();
	}
	
	public Course(String name, String lec) {
		super(name);
		
		lectures = new TreeSet<String>();
		lectures.add(lec);
	}
	
	public boolean checkForLecture(String l) {
		for (String lec : lectures) {
			if (l == lec) {
				return false;
			}
		}
		return true;
	}
	
	public void update(String l) {
		if (checkForLecture(l))
			lectures.add(l);
	}
	
	public String toString() {
		String ls = "";
		for (String l : lectures) {
			ls = ls + "," + l;
		}
		return "course(" + getName() + ls + ")";
	}
}
