package examSchedule;

import examSchedule.parser.Entity;

public class Student extends Entity {

	public Student(String name) {
		super(name);
	}
	
	public String toString() {
		return "student("+getName()+")";
	}
	
}
