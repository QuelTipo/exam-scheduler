package examSchedule;

import java.util.TreeSet;

import examSchedule.Environment;
import examSchedule.parser.Pair;
import examSchedule.parser.SolutionInterface;

public class Solution implements SolutionInterface {
	
	// A pointer to our global environment, necessary for hard and soft constraint checking
	private Environment environment;
	
	private long numLectures;
	private boolean complete;
	private TreeSet<Assign> assignments;
	private long penalty;
	private TreeSet<Lecture> unassignedLectures;
	
	// Default constructor
	public Solution(Environment env) {
		assignments = new TreeSet<Assign>(environment.getFixedAssignments());
		
		numLectures = environment.getLectureList().size();
		
		complete = numLectures == assignments.size() ? true : false;
		
		penalty = calculatePenalty();
		
		// Our inital list of unassigned lectures will contain every lecture
		unassignedLectures = environment.getLectureList();
		// Now we'll remove the lectures which are assigned
		for (Assign assign : assignments) {
			Lecture lecture = assign.getLecture();
			if (unassignedLectures.contains(lecture))
				unassignedLectures.remove(lecture);
		}
		
	}
		
	
	// Add an assignment to a solution
	public boolean addAssignment(Assign assign) {
		
		// Create a new tree set of assignments
		TreeSet<Assign> proposedAssignments = assignments;
		proposedAssignments.add(assign);
				
		// If the proposed solution is a partial solution
		if (proposedAssignments.size() < numLectures) {
			// Then return false if it is invalid
			if (isValidSolution(proposedAssignments, false) == false)
				return false;
		}
		// Otherwise it should be a complete solution
		else {
			// Return false is it is invalid
			if (isValidSolution(proposedAssignments, true) == false)
				return false;
		}

		// If we've gotten to here, we know we're dealing with at least a valid partial solution
		// Update our tree set of assignments and return; 
		assignments = proposedAssignments;
		complete = assignments.size() == numLectures;
		unassignedLectures.remove(assign.getLecture());
		return true;
	}
	
	
	// Ensure the solution is a valid solution, partial or complete
	public boolean isValidSolution(TreeSet<Assign> proposedAssignments, boolean complete) {
		
		for (Lecture lecture : environment.getLectureList()) {
			int numAssigns = 0;
			for (Assign assign : proposedAssignments) {
				if (assign.getLecture().equals(lecture))
					numAssigns++;
			}
			// In the case of a partial solution, ensure no lecture is assigned to more than one exam session
			if (complete) {
				if (numAssigns != 1)
					return false;
			}
			// In the case of a complete solution, ensure that every lecture is assigned exactly once
			else {
				if (numAssigns > 1)
					return false;
			}
		}
		
		// Ensure that the number of students writing an exam in any room is less than or equal to the capacity of that room
		for (Room room : environment.getRoomList()) {
			long numStudents = 0;
			for (Assign assign : proposedAssignments) {
				if (assign.getSession().getRoom() == room) {
					numStudents += assign.getLecture().getStudents().size();
				}
			}
			if (!room.canHold(numStudents))
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
			cumulativePenalty += (collisions * 20);
		}
		
		// Every lecture for the same course should have the same exam timeslot - 50
		// For every course
		for (Course course : environment.getCourseList()) {
			
			TreeSet<Pair<Day, Integer>> set = new TreeSet<Pair<Day, Integer>>();
			
			// And for every lecture that course has
			for (Lecture lecture : course.getLectures()) {
				
				// Search the list of assignment for the matching one
				for (Assign assign : assignments) {
					
					// When we find the assignment
					if (assign.getName() == lecture.getName()) {
						
						// Add the day/time pair for that session to our set
						Session session = assign.getSession();
						Pair<Day, Integer> dayTimePair = new Pair<Day, Integer>(session.getDay(), (int)session.getLength());
						set.add(dayTimePair);
					}
				}
			}
			
			// Now we have a set of day/time pairs for the lectures of this course, increment the penalty accordingly
			cumulativePenalty += ((set.size() - 1) * 50);
			
		}
		
		// No student writes for longer than 5 hours in a single day - 50
		// For every day
		for (Day day : environment.getDayList()) {
			
			// For every student
			for (Student student : environment.getStudentList()) {
				
				int totalExamTime = 0;
				// For every course/lecture pair that student is enrolled in
				for (Pair<Course, Lecture> pair : student.getCourses()) {
					
					Lecture lecture = pair.getValue();					
					// Now look for the correct assignment
					for (Assign assign : assignments) {
						
						if (assign.equals(lecture.getName()))
							totalExamTime += lecture.getLength();
					}
				}
				
				// Check the total exam time for that student on that day
				if (totalExamTime > 5)
					cumulativePenalty += 50;
			}
		}
		
		
		// No student should write exams with no break between them - 50
		// For every student
		for (Student student : environment.getStudentList()) {
			
			TreeSet<Assign> set = new TreeSet<Assign>();
			
			// For every course/lecture pair they are enrolled in
			for (Pair<Course, Lecture> pair : student.getCourses()) {
				
				Lecture lecture = pair.getValue();					
				// Find the right assignment
				for (Assign assign : assignments) {
					
					// And add it to the set
					if (assign.equals(lecture.getName()))
						set.add(assign);
				}
			}
			
			// At this point we have the assignments for all the course/lecture pairs
			// Iterate over the set, making sure an exam doesn't start when another finishes
			for (Assign a1 : set) {
				
				for (Assign a2 : set) {
				
					// We don't need to worry if its the same exam
					if (a1.equals(a2))
						continue;
					
					Lecture l1 = a1.getLecture();
					Session s1 = a1.getSession();
					Session s2 = a2.getSession(); 
					
					// If the 2 sessions are on the same day
					if (s1.getDay().equals(s2.getDay()))
						
						// If s2 starts when l1 finishes, increase the penalty counter
						if (s2.getTime() == (s1.getTime() + l1.getLength()))
							cumulativePenalty += 50;
				}
			}
		}
		
		// All the exams taking place in a particular session should have the same length - 20
		// For every session
		for (Session session : environment.getSessionList()) {
			
			TreeSet<Pair<Session, Integer>> set = new TreeSet<Pair<Session, Integer>>();
			
			for (Assign assign : assignments) {
				
				// If this is the right assignment
				if (assign.getSession().equals(session)) {
					
					int length = (int)assign.getLecture().getLength();
					Pair<Session, Integer> pair = new Pair<Session, Integer>(session, length);
					// Attempt to add the session/length pair to our set
					// If we fail, it was already in there, no worries
					set.add(pair);
				}
			}
			
			// Note - they should all be the same length, hence the -1
			cumulativePenalty += ((set.size() - 1) * 20);
		}
		
		// Every exam in a session should take up the full time of the session - 5
		// For every assignment
		for (Assign assign : assignments) {
			
			// If the exam is shorter than the session, increase the penalty by 5
			if (assign.getLecture().getLength() < assign.getSession().getLength())
				cumulativePenalty += 5;
		}
		
		return cumulativePenalty;
	}
	
	
	// Return the completeness of a solution
	public boolean isComplete() {
		return complete;
	}
	
	
	public long getPenalty() {
		return penalty;
	}


	@Override
	public boolean isSolved() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean hasViolations() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public int getGoodness() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public TreeSet<Lecture> getUnassignedLectures() {
		return unassignedLectures;
	}
	
}
