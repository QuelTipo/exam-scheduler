package examSchedule;

import java.util.TreeSet;

import examSchedule.Environment;
import examSchedule.parser.Pair;

public class Solution {
	
	// A pointer to our global environment, necessary for hard and soft constraint checking
	private Environment environment;
	
	private long numLectures;
	private boolean complete;
	private TreeSet<Assign> assignments;
	private long penalty;
	
	
	// Default constructor
	public Solution(Environment env) {
		assignments = new TreeSet<Assign>(environment.getFixedAssignments());
		
		numLectures = environment.getLectureList().size();
		
		complete = numLectures == assignments.size() ? true : false;
		
		penalty = calculatePenalty();
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
	
	
	public long calculatePenalty() {
		
		long cumulativePenalty = 0;
		
		// No student writes more than one exam in a timeslot - 100
		// For every student
		for (Student student : environment.getStudentList()) {

			TreeSet<Pair<Day, Integer>> set = new TreeSet<Pair<Day, Integer>>();
			int collisions = 0;
						
			// And for every course that that student is enrolled in
			for (Pair<Course, Lecture> pair : student.getCourses()) {
				
				// Search our list of assignments for the course/lecture pair
				String name = pair.getValue().getName();
				for (Assign assign : assignments) {
					
					// When we find the assignment
					if (assign.getName() == name) {
						
						Session session = assign.getSession();
						Pair<Day, Integer> dayTimePair = new Pair<Day, Integer>(session.getDay(), (int)session.getLength());
						
						// Attempt to add the pair to our set
						// If we fail to add it, it was already in there, so we increment the collision counter
						if (set.add(dayTimePair) == false)
							collisions++;
					}
				}
			}
			
			// Now we have the number of collisions for the student, so we increment the penalty accordingly
			cumulativePenalty += (collisions * 100);
		}
		
		// No instructor invigulates in more than one room at the same time - 20
		// For every instructor
		for (Instructor instructor : environment.getInstructorList()) {
			
			TreeSet<Pair<Day, Integer>> set = new TreeSet<Pair<Day, Integer>>();
			int collisions = 0;
			
			// And for every course that an instructor is registered to teach
			for (Pair<Course, Lecture> pair : instructor.getCourses()) {
				
				// Search our list of assignment for the course/lecture pair
				String name = pair.getValue().getName();
				for (Assign assign : assignments) {
					
					// When we find the assignment
					if (assign.getName() == name) {
						
						Session session = assign.getSession();
						Pair<Day, Integer> dayTimePair = new Pair<Day, Integer>(session.getDay(), (int)session.getLength());
						
						// Attempt to add the pair to our set
						// If we fail to add it, it was already in there, so we increment the collision counter
						if (set.add(dayTimePair) == false)
							collisions++;
					}
				}
			}
			
			// Now we have the number of collisions for the student, so we increment the penalty accordingly
			cumulativePenalty += (collisions * 20);
		}
		
		
		// Every lecture for the same course should have the same exam timeslot - 50
		
		// No student writes for longer than 5 hours in a single day - 50
		
		// No student should write exams with no break between them - 50
		
		// All the exams taking place in a particular session should have the same length - 20
		
		// Every exam in a session should take up the full time of the session - 5
		
		return cumulativePenalty;
	}
	
	
	// Return the completeness of a solution
	public boolean isComplete() {
		return complete;
	}
	
	
	public long getPenalty() {
		return penalty;
	}
	
}
