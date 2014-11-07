package examSchedule;

import java.util.TreeSet;
import examSchedule.parser.Entity;

public class Course extends Entity {

	public Course(String name) {
		super(name);
		
		lectures = new TreeSet<String>();
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
		return "course(" + getName() + ")";
	}
}
