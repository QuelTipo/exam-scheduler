package examSchedule;

import java.util.TreeSet;
import examSchedule.Environment;

public class Solution {
	
	// A pointer to our global environment, necessary for hard and soft constraint checking
	private Environment environment;
	
	private long numLectures;
	private boolean complete;
	private TreeSet<Assign> assignments;
	
	// Default constructor
	public Solution(Environment env) {
		assignments = new TreeSet<Assign>(environment.getFixedAssignments());
		
		numLectures = environment.getLectureList().size();
		
		complete = numLectures == assignments.size() ? true : false;
	}
		
	// Add an assignment to a solution
	public boolean addAssignment(Assign assign) {
		
		// Create a new tree set of assignments
		TreeSet<Assign> proposedAssignments = assignments;
		proposedAssignments.add(assign);
		
		// If it is not a valid partial solution, return false
		if (isValidPartialSolution(proposedAssignments) == false) 
			return false;
		
		// If it is of size numLectures 
		if (proposedAssignments.size() == numLectures) {
				
			// AND is not a valid complete solution, return false
			if (isValidSolution(proposedAssignments) == false) 
				return false;
		}

		// If we've gotten to here, we know we're dealing with at the very least, a valid partial solution
		// Update our tree set of assignments and return; 
		assignments = proposedAssignments;
		complete = numLectures == assignments.size() ? true : false;
		return true;
	}
	
	
	// Ensure the solution is a valid partial solution
	public boolean isValidPartialSolution(TreeSet<Assign> proposedAssignments) {
		
		// Ensure no lecture is assigned to more than one exam session
		for (Lecture lecture : environment.getLectureList()) {
			int numAssigns = 0;
			for (Assign assign : proposedAssignments) {
				if (assign.getLecture().equals(lecture))
					numAssigns++;
			}
			if (numAssigns > 1)
				return false;
		}
		
		// Ensure that the number of students writing an exam in any room is less than or equal to the capacity of that room
		for (Room room : environment.getRoomList()) {
			long numStudents = 0;
			for (Assign assign : proposedAssignments) {
				if (assign.getSession().getRoom() == room) {
					// This can be optimized - we'll need to maintain a count of the number of students taking a given lecture
					Lecture lecture = assign.getLecture();
					for (Student student : environment.getStudentList()) {
						if (student.checkForCourse(lecture.getCourse(), lecture))
							numStudents++;
					}
				}
			}
			if (numStudents > room.getCapacity())
				return false;
		}
		
		// Ensure that every lecture's required time is less than or equal to the length of the session it is assigned to
		for (Assign assign : proposedAssignments) {
			Lecture lecture = assign.getLecture();
			Session session = assign.getSession();
			if (lecture.getLength() > session.getLength())
				return false;
		}
				
		// If we reach here, we're dealing with a valid partial solution
		return true;
	}
	
	public boolean isValidSolution(TreeSet<Assign> proposedAssignments) {
		
		// Ensure that every lecture is assigned to one exam session
		for (Lecture lecture : environment.getLectureList()) {
			int numAssigns = 0;			
			for (Assign assign : proposedAssignments) {
				if (assign.getLecture() .equals(lecture))
					numAssigns++;
			}
			if (numAssigns > 1)
				return false;
		}
		
		// If we reach here, we've got a valid, complete solution
		return true;
	}
	
	
	// Return the completeness of a solution
	public boolean isComplete() {
		return complete;
	}
	
	
}
