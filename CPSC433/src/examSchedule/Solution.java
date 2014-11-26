package examSchedule;

import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;

import examSchedule.Environment;
import examSchedule.parser.Pair;
import examSchedule.parser.SolutionInterface;

public class Solution implements SolutionInterface {
	
	// A pointer to our global environment, necessary for hard and soft constraint checking
	private Environment environment;
	
	private long numLectures;
	private boolean complete;
	private TreeMap<String, Assign> assignmentMap;
	private long penalty;
	private TreeSet<Lecture> unassignedLectures;
	private HashMap<Assign, TreeSet<Assign>> conflictingAssignments;
	private Vector<Assign> rankedAssignments = new Vector<Assign>((int)numLectures);
	private TreeMap<Session,Long> currentRoomCapacities = new TreeMap<Session,Long>();
	private TreeMap<Session, TreeSet<Lecture>> lecturesInSession = new TreeMap<Session, TreeSet<Lecture>>();
	
	// Default constructor
	public Solution(Environment env) {
		
		environment = env;
		assignmentMap = new TreeMap<String, Assign>(environment.getFixedAssignments());
		
		numLectures = environment.getLectureList().size();
		
		TreeSet<Session> sessionList = environment.getSessionList();
		for (Session session : sessionList) {
			currentRoomCapacities.put(session, new Long(session.getRoom().getCapacity()));
			lecturesInSession.put(session, new TreeSet<Lecture>());
		}
		
		complete = numLectures == assignmentMap.size() ? true : false;
		
		// penalty = calculatePenalty();
		
		// Our inital list of unassigned lectures will contain every lecture
		unassignedLectures = new TreeSet<Lecture>(environment.getLectureList());
		// Now we'll remove the lectures which are assigned
		for (Assign assign : assignmentMap.values()) {
			
			Lecture lecture = assign.getLecture();
			if (unassignedLectures.contains(lecture))
				unassignedLectures.remove(lecture);
			Session session = assign.getSession();
			addLectureToSession(session,lecture);
			decreaseSessionCapacity(session,lecture);
		}
	}
		
	
	// Add an assignment to a solution
	public boolean addAssignment(Assign assign) {
		
		// Create a new map of assignment names to assignments
		TreeMap<String, Assign> proposedAssignmentMap = new TreeMap<String, Assign>(assignmentMap);
		proposedAssignmentMap.put(assign.getName(), assign);
				
		TreeSet<Assign> proposedAssignments = (TreeSet<Assign>)proposedAssignmentMap.values();
		
		// If the proposed solution is a partial solution
		if (proposedAssignmentMap.size() < numLectures) {
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
		assignmentMap = proposedAssignmentMap;
		unassignedLectures.remove(assign.getLecture());
		addLectureToSession(assign.getSession(),assign.getLecture());
		decreaseSessionCapacity(assign.getSession(),assign.getLecture());
		complete = assignmentMap.size() == numLectures;
		return true;
	}
	
	// This method does none of the checking required in the above method
	public boolean dumbAddAssign(Assign assign) {
		
		if (assignmentMap.containsKey(assign.getName()))
			return false;
		
		assignmentMap.put(assign.getName(), assign);
		unassignedLectures.remove(assign.getLecture());
		complete = assignmentMap.size() == numLectures;
		decreaseSessionCapacity(assign.getSession(),assign.getLecture());
		addLectureToSession(assign.getSession(),assign.getLecture());
		return true;
	}
	
	
	public void removeAssignment(Assign assign) {
		
		Lecture lecture = assign.getLecture();
		Session session = assign.getSession();
		
		// Now we remove the assignment from our set of assignments
		assignmentMap.remove(assign.getName());
		// Add the lecture back to unassigned lectures
		unassignedLectures.add(assign.getLecture());
		//increase the capacity available in the room again.
		increaseSessionCapacity(session, lecture);
		//we need to remove the link
		removeLectureFromSession(session,lecture);
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
			if (!complete) {
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
		for (Assign assign : proposedAssignments) {
			Session session = assign.getSession();
			TreeSet<Lecture> sessionLectures = lecturesInSession.get(session);
			long numberOfStudents = 0;
			for (Lecture lecture : sessionLectures) {
				numberOfStudents += lecture.getClassSize();
			}
			if (numberOfStudents > session.getRoom().getCapacity()) {
				return false;
			}
			
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
		
	
	public void calculatePenalty() {
		
		// Our map of assignments to conflicting assignments
		HashMap<Assign, TreeSet<Assign>> conflictMap = new HashMap<Assign, TreeSet<Assign>>();
		
		// Initialize it with default values
		for (Assign assign : assignmentMap.values())
			conflictMap.put(assign, new TreeSet<Assign>());
		
		long cumulativePenalty = 0;

		// Soft constraint one requires a set of pairs of assignments to avoid double penalties
		TreeSet<Pair<Student, Pair<Assign, Assign>>> s1Set = new TreeSet<Pair<Student, Pair<Assign, Assign>>>();
		TreeSet<Pair<Assign, Assign>> s2Set = new TreeSet<Pair<Assign, Assign>>();
		
		// Iterate over every assignment
		// No student writes more than one exam in a timeslot - 100
		// No instructor invigulates in more than one room at the same time - 20
		// No student should write exams with no break between them - 50 
		// Every exam in a session should take up the full time of the session - 5
		for (Assign assign : assignmentMap.values()) {
						
			Lecture l1 = assign.getLecture();
			Session s1 = assign.getSession();
			Day d1 = s1.getDay();
			
			// Iterate over all the sessions also on the given day
			for (Session s2 : d1.getSessions()) {
				
				// Iterate over all the lectures scheduled on the given session
				TreeSet<Lecture> s2lectures = lecturesInSession.get(s2);
				for (Lecture l2 : s2lectures) {
					
					// Obviously we don't need to worry about a lecture conflicting with itself
					if (l1.equals(l2))
						continue;
					
					long t1 = s1.getTime();
					long t2 = s2.getTime();
					
					// As per the specifications, we are only interested when t1 < t2
					if (t1 <= t2) {
						
						// Now check if the lectures overlap
						if (t1 + l1.getLength() > t2) {
							
							// Now we know we have a pair of lectures which overlap, check to see if there are students in both lectures
							for (Student st1 : l1.getStudents()) {
								
								for (Student st2 : l2.getStudents()) {
									
									// If they are the same student
									if (st1.equals(st2)) {
//										System.out.println("Soft constraint 1 violation for student " + st1.getName() + " between " + l1.getName() + " and " + l2.getName());
										
										// Update our conflict map
										TreeSet<Assign> conflicts = conflictMap.get(assign);
										Assign newConflict = assignmentMap.get(l2.getName());
										
										// Sanity check
										assert newConflict != null : String.format("Failed to find assignment with name %s", l2.getName());
										
										conflicts.add(newConflict);
										conflictMap.put(assign, conflicts);
										
										// We cannot simply increase the cumulative penalty by 100, as this does not account for lectures starting at the same time
										// Add the pair of conflicting assignments to our s1Set, also checking that the reverse is not already in there
										Pair<Assign, Assign> forward = new Pair<Assign, Assign>(assign, newConflict);
										Pair<Assign, Assign> reverse = new Pair<Assign, Assign>(newConflict, assign);
										Pair<Student, Pair<Assign, Assign>> forwardStudent = new Pair<Student, Pair<Assign, Assign>>(st1, forward);
										Pair<Student, Pair<Assign, Assign>> reverseStudent = new Pair<Student, Pair<Assign, Assign>>(st1, reverse);
										if (!s1Set.contains(forwardStudent) && !s1Set.contains(reverseStudent))
											s1Set.add(forwardStudent);
									}
								}
							}
							
							// Also check if the both courses have the same instructor and the lectures are in different sessions
							Instructor i1 = l1.getInstructor();
							Instructor i2 = l2.getInstructor();
							if (i1.equals(i2) && !s1.equals(s2)) {
//								System.out.println("Soft constraint 2 violation for instructor " + i1.getName() + " between " + l1.getName() + " and " + l2.getName());
								
								// Update our conflict map
								TreeSet<Assign> conflicts = conflictMap.get(assign);
								Assign newConflict = assignmentMap.get(l2.getName());
								
								// Sanity check
								assert newConflict != null : String.format("Failed to find assignment with name %s", l2.getName());
								
								conflicts.add(newConflict);
								conflictMap.put(assign, conflicts);
								
								// Similar to soft constraint 1, we don't want to add the penalty twice
								Pair<Assign, Assign> newPair = new Pair<Assign, Assign>(assign, newConflict);
								Pair<Assign, Assign> reverse = new Pair<Assign, Assign>(newConflict, assign);
								if (!s1Set.contains(newPair) && !s1Set.contains(reverse))
									s2Set.add(newPair);
							}
						}
						
						// Now check if the lectures are back to back
						else if (t1 + l1.getLength() == t2) {
							
							// Check to see if there are students in both lectures
							for (Student st1 : l1.getStudents()) {
								
								for (Student st2 : l2.getStudents()) {
									
									// If they are the same student
									if (st1.equals(st2)) {
										
										// Since we only enter this block is l2 begins when l1 ends, we don't need to worry about the penalty being applied twice
										cumulativePenalty += 50;
//										System.out.println("Soft constraint 5 violation for student " + st1.getName() + " between " + l1.getName() + " and " + l2.getName());
										TreeSet<Assign> conflicts = conflictMap.get(assign);
										Assign newConflict = assignmentMap.get(l2.getName());
										
										// Sanity check
										assert newConflict != null : String.format("Failed to find assignment with name %s", l2.getName());
										
										conflicts.add(newConflict);
										conflictMap.put(assign, conflicts);
									}
								}
							}
						}
					}
				}
			}
			
			// Make sure every assignment is a tight fit
			if (l1.getLength() < s1.getLength()) {
				cumulativePenalty += 5;
//				System.out.println("Soft constraint 7 violation on " + l1.getName());
				TreeSet<Assign> conflicts = conflictMap.get(assign);
				Assign newConflict = assignmentMap.get(l1.getName());
				
				// Sanity check
				assert newConflict != null : String.format("Failed to find assignment with name %s", l1.getName());
				
				conflicts.add(newConflict);
				conflictMap.put(assign, conflicts);
			}
		}
		
		// Iincrease our cumulative penalty by 100 per entry in our s1Set
		cumulativePenalty += 100 * s1Set.size();
		
		// Increase our cumulative penalty by 20 per entry in our s2Set
		cumulativePenalty += 20 * s2Set.size();
				
		// Every lecture for the same course should have the same exam timeslot - 50
		// For every course
		for (Course course : environment.getCourseList()) {
			
			TreeSet<Assign> assigns = new TreeSet<Assign>();
			TreeSet<Pair<Day, Integer>> examTimes = new TreeSet<Pair<Day, Integer>>();
			
			// And for every lecture that course has
			for (Lecture lecture : course.getLectures()) {
				
				
				Assign assign = assignmentMap.get(lecture.getName());
								
				// If there is one for that course lecture pair
				if (assign != null) {
					
					// Add the day/time pair for that session to our set
					Session session = assign.getSession();
					Pair<Day, Integer> dayTimePair = new Pair<Day, Integer>(session.getDay(), (int)session.getLength());
					examTimes.add(dayTimePair);
					
					// Add the assignment to our set of assignments
					assigns.add(assign);
				}
			}
			
			// If our day/time set has more than 1 entry
			int numTimeSlots = examTimes.size();
			if (numTimeSlots > 1) {
				// Increase the penalty by 50
				cumulativePenalty += ((numTimeSlots - 1) * 50);
//				System.out.println("Soft constraint 3 violation for course " + course.getName());
				// Update our conflict map
				for (Assign a1 : assigns) {				
					TreeSet<Assign> conflicts = conflictMap.get(a1);					
					for (Assign a2 : assigns) {
						conflicts.add(a2);
						conflictMap.put(a1, conflicts);
					}
				}
			}			
		}
		
		// No student writes for longer than 5 hours in a single day - 50
		// For every day
		for (Day day : environment.getDayList()) {
			
			Map<Student, Integer> writingTimes = new HashMap<Student, Integer>();
			
			// And every session on that day
			for (Session session : day.getSessions()) {
				
				// And every lecture in that session
				TreeSet<Lecture> sessionLectures = lecturesInSession.get(session);
				for (Lecture lecture : sessionLectures) {
					
					int lectureLength = (int)lecture.getLength();
					
					// And every student in that lecture
					for (Student student : lecture.getStudents()) {
						
						// If the student is already in te map, then we need to get the current value
						int value = writingTimes.containsKey(student) ? writingTimes.get(student) : 0;
						// Now update the map
						writingTimes.put(student, value + lectureLength);
					}
				}
			}
			
			// Now iterate over the map, looking for values greater than 5
			Collection<Integer> toCollection = writingTimes.values();
			Vector<Integer> times = new Vector<Integer>(toCollection);
			for (int time : times) {
				if (time > 5) {
					cumulativePenalty += 50;
//					System.out.println("Soft constraint 4 violation");
				}
			}
		}
				
		// All the exams taking place in a particular session should have the same length - 20
		// Iterate over every session
		for (Session session : environment.getSessionList()) {
			
			TreeSet<Integer> lengths = new TreeSet<Integer>(); 
			TreeSet<Lecture> lectures = new TreeSet<Lecture>();
			
			// Iterate over every lecture assigned to the session
			TreeSet<Lecture> sessionLectures = lecturesInSession.get(session);
			for (Lecture lecture : sessionLectures) {
				
				// Add the length to our set of lengths
				lengths.add((int)lecture.getLength());
				// Add the lecture to our set of lectures
				lectures.add(lecture);
			}
			
			// If there is more than one entry in our set
			int numLengths = lengths.size();
			if (numLengths > 1) {
				
				// Increase the penalty
				cumulativePenalty += ((numLengths - 1) * 20);
				
//				System.out.println("Soft constraint 6 violation for session " + session.getName());
				
				// Update our conflict map
				for (Lecture l1 : lectures) {
					
					Assign assign = assignmentMap.get(l1.getName());
					TreeSet<Assign> conflicts = conflictMap.get(assign);
					
					for (Lecture l2 : lectures) {
						
						Assign newConflict = assignmentMap.get(l2.getName());
						
						// Sanity check
						assert newConflict != null : String.format("Failed to find assignment with name %s", l1.getName());
						
						conflicts.add(newConflict);
						conflictMap.put(assign, conflicts);
					}
				}
			}
		}
				
		// Update our conflicting assignments and penalty
		conflictingAssignments = conflictMap;
		penalty = cumulativePenalty;
	}
		
	
	// This method will use set rankedAssignments, ascending ordering by number of conflicts
	public void rankAssignments() {
				
		// Our map to store how many other assignments a given assignment conflicts with
		HashMap<Assign, Integer> numConflicts = new HashMap<Assign, Integer>();
		for (Assign assign : assignmentMap.values()) {
			numConflicts.put(assign, 0);
		}
		
		// For every set of conflicting assignments...
		for (TreeSet<Assign> set : conflictingAssignments.values()) {
			
			// Iterate over every member of that set
			for (Assign assign : set) {
			 
				// Update our map
				int conflicts = numConflicts.get(assign);
				conflicts++;
				numConflicts.put(assign, conflicts);
			}
		}
						
		// Sort our list of assignments by the number of assignments each one conflicts with
		Vector<Assign> assignments = new Vector<Assign>(assignmentMap.values());
		rankedAssignments = sortAssignments(assignments, numConflicts);
	}

	
	// This method will sort a given list of assignments based on the number of conflicts they have
	// Linear search at the moment...
	public Vector<Assign> sortAssignments(Vector<Assign> assignments, HashMap<Assign, Integer> numConflicts) {
		
		TreeSet<Assign> remainingAssignments = new TreeSet<Assign>(assignments);
		Vector<Assign> orderedAssignments = new Vector<Assign>();
		
		for (int i = 0; i < numLectures; ++i) {
		
			// Grab the first remaining assignment
			Assign best = remainingAssignments.first();
			
			// Iterate over the list of remaining assignments
			for (Assign assign : remainingAssignments) {
				
				// Check if the assignment we're looking at is better than the current best
				int bestConflicts = numConflicts.get(best);
				int newConflicts = numConflicts.get(assign);
				if (newConflicts < bestConflicts)
					best = assign;
			}
			
			// Append this assignment to our vector of orderedAssignments
			orderedAssignments.add(best);
			// Remove the best from the remaining solutions
			remainingAssignments.remove(best);
		}
		
		return orderedAssignments;
	}
	

	// This method is a component of the mutate operation, and will unassign the worst n assignments
	public void unassignWorst(int n) {
		
		// Sanity check
		assert n <= numLectures : "Attempting to unassign more assignments than possible";
		
		HashMap<String, Assign> fixedAssignments = (HashMap<String, Assign>)environment.getFixedAssignments();
				
		// We want to start with the last index, but we'll be decrementing it when we access
		int index = (int)numLectures;
		for (int count = 0; count < n; ++count) {
			
			// Get the next worse assignment (the worst in case this is the first time through the loop)
			Assign worst = rankedAssignments.get(--index);
			
			// Make sure we don't remove a fixed assignment
			while (fixedAssignments.values().contains(worst)) {
				worst = rankedAssignments.get(--index);
			}
			
			System.out.println("Unassigning " + worst.getName());
			
			// Remove the assignment
			removeAssignment(worst);
		}
	}
	
	public TreeSet<Assign> extractBest(int num) {
		
		// Sanity check
		assert num <= numLectures : "Attempting to extract more assignments than possible";
				
		HashMap<String, Assign> fixedAssignments = (HashMap<String, Assign>)environment.getFixedAssignments();
		TreeSet<Assign> bestAssignments = new TreeSet<Assign>();
		
		// We want to start with the first index, but we'll be incrementing it when we access
		int index = -1;
		for (int count = 0; count < num; ++ count) {
			
			Assign best = rankedAssignments.get(++index);
			
			// Make sure we're not grabbing a fixed assignment
			while (fixedAssignments.values().contains(best))
				best = rankedAssignments.get(++index);
			
//			System.out.println("Adding " + best.getName() + "," + best.getSession().getName());
			
			// Add the assignment to our set
			bestAssignments.add(best);
		}
		
		return bestAssignments;
	}
	
	public void decreaseSessionCapacity(Session session, Lecture lecture) {
		long capacity = currentRoomCapacities.get(session).longValue();
		capacity -= lecture.getClassSize();
		currentRoomCapacities.put(session, capacity);
	}
	
	public void increaseSessionCapacity(Session session, Lecture lecture) {
		long capacity = currentRoomCapacities.get(session).longValue();
		capacity += lecture.getClassSize();
		currentRoomCapacities.put(session, capacity);
	}
	
	public boolean canHold(Session session, Lecture lecture) {
		long capacity = currentRoomCapacities.get(session).longValue();
		capacity -= lecture.getClassSize();
		if (capacity < 0) {
			return false;
		}
		return true;
	}
	
	public Vector<Session> weedOutByCapacity(TreeSet<Session> sessionList, long classSize) {
		
		Vector<Session> sessionVector = new Vector<Session>();
		
		for (Session session : sessionList) {
			long capacity = currentRoomCapacities.get(session).longValue();
			if (classSize <= capacity) {
				sessionVector.add(session);
			}
		}
		
		return sessionVector;
	}
	
	public void addLectureToSession(Session session, Lecture lecture) {
		TreeSet<Lecture> lectures = lecturesInSession.get(session);
		lectures.add(lecture);
		lecturesInSession.put(session,lectures);
	}
	
	public void removeLectureFromSession(Session session, Lecture lecture) {
		TreeSet<Lecture> lectures = lecturesInSession.get(session);
		lectures.remove(lecture);
		lecturesInSession.put(session,lectures);
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
		return complete;
	}


	@Override
	public boolean hasViolations() {
		return penalty > 0 ? true : false;
	}


	@Override
	public int getGoodness() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public TreeSet<Lecture> getUnassignedLectures() {
		return unassignedLectures;
	}
	
	public HashMap<Assign, TreeSet<Assign>> getConflictingAssignmentMap() {
		return conflictingAssignments;
	}
	
	public void printAssignments() {
		for (Assign assign : assignmentMap.values()) {
			System.out.println(assign.toString());
		}
	}
	
	public TreeMap<String,Assign> getAssignments() {
		return assignmentMap;
	}
	
	public Vector<Assign> getRankedAssignments() {
		return rankedAssignments;
	}
	
	public String toString() {
		String result = "Solution = {\n";
		for (Assign assign : assignmentMap.values()) {
			result = result + assign.toString() + "\n";
		}
		result = result + "}\n" + "Weight is " + getPenalty(); 
		return result;
	}
	
	public String conflictMapToString() {
		
		String result = "Conflict map = {\n";
		for (Map.Entry<Assign, TreeSet<Assign>> entry : conflictingAssignments.entrySet()) {
			result = result + entry.getKey().toString() + " -> ";
			for (Assign assign : entry.getValue()) {
				result = result + assign.toString() + " ";
			}
			result  = result + "\n";
		}
		result = result + "}";
		return result;
	}	
}
