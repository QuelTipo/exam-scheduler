/*	
 * This class represents a partial or complete solution to the exam scheduling problem
 * Assignments composing the solution are stored in a dictionary, mapping assignment names to pointers to assignments
 * The complete flag is set when the solution has as many assignments as there are lectures
 * Assignments are added one at a time, and we assume that the (partial) solution to which we are adding an assignment to is valid
 * A solution also calculates its penalty, during which it builds a conflicting assignment dictionary
 * This dictionary maps assignments to sets of conflicting assignments, and is used to rank the component assignments based upon how many other assignments an assignment conflicts with
 * 
 */

package examSchedule;

import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;

import examSchedule.Environment;
import examSchedule.parser.Pair;

public class Solution implements SolutionInterface {
	
	// A pointer to our global environment, necessary for hard and soft constraint checking
	private Environment environment;
	
	private long numLectures;
	private boolean complete;
	private TreeMap<String, Assign> assignmentMap;
	private long penalty;
	private TreeSet<Lecture> unassignedLectures;
	private HashMap<Assign, TreeSet<Pair<Assign, Integer>>> conflictingAssignments;
	private Vector<Assign> rankedAssignments = new Vector<Assign>((int)numLectures);
	private TreeMap<Session,Long> currentRoomCapacities = new TreeMap<Session,Long>();
	private TreeMap<Course,Session> sessionsOfCourses = new TreeMap<Course,Session>();
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
		
		for (Course course : environment.getCourseList()) {

			sessionsOfCourses.put(course, null);

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
		
	
	// This method does none of the checking required in the above method
	public boolean dumbAddAssign(Assign assign) {
		
		if (assignmentMap.containsKey(assign.getName()))
			return false;
		
		if (assign.getLecture().getClassSize() <= currentRoomCapacities.get(assign.getSession())) {
		
			assignmentMap.put(assign.getName(), assign);
			unassignedLectures.remove(assign.getLecture());
			complete = assignmentMap.size() == numLectures;
			if (sessionsOfCourses.get(assign.getLecture().getCourse()) == null) {
				sessionsOfCourses.put(assign.getLecture().getCourse(),assign.getSession());
			}
			decreaseSessionCapacity(assign.getSession(),assign.getLecture());
			addLectureToSession(assign.getSession(),assign.getLecture());
			return true;
		} else {
			return false;
		}
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
	public boolean isValidSolution() {
		
		for (Lecture lecture : environment.getLectureList()) {
			int numAssigns = 0;
			for (Assign assign : assignmentMap.values()) {
				if (assign.getLecture().equals(lecture))
					numAssigns++;
			}
			// Ensure that every lecture is assigned exactly once
			if (numAssigns > 1) {
				System.out.println(lecture.toString() + "assigned " + numAssigns + " times");
				return false;
			}
		}
		
		// Ensure that the number of students writing an exam in any room is less than or equal to the capacity of that room
		for (Assign assign : assignmentMap.values()) {
			Session session = assign.getSession();
			TreeSet<Lecture> sessionLectures = lecturesInSession.get(session);
			long numberOfStudents = 0;
			for (Lecture lecture : sessionLectures) {
				numberOfStudents += lecture.getClassSize();
			}
			if (numberOfStudents > session.getRoom().getCapacity()) {
				System.out.println(session.toString() + " has capacity of " + session.getRoom().getCapacity() + " and has " + numberOfStudents + " students");
				return false;
			}
			
		}

		
		// Ensure that every lecture's required time is less than or equal to the length of the session it is assigned to
		for (Assign assign : assignmentMap.values()) {
			Lecture lecture = assign.getLecture();
			Session session = assign.getSession();
			if (lecture.getLength() > session.getLength()) {
				System.out.println("length mismatch between " + lecture.toString() + " and " + session.toString());
				return false;
			}
		}
				
		// If we reach here, we're dealing with a valid partial solution
		return true;
	}
		
	
	public void calculatePenalty() {
		
		// Our map of assignments to conflicting assignments
		HashMap<Assign, TreeSet<Pair<Assign, Integer>>> conflictMap = new HashMap<Assign, TreeSet<Pair<Assign, Integer>>>();
		
		// Initialize it with default values
		for (Assign assign : assignmentMap.values())
			conflictMap.put(assign, new TreeSet<Pair<Assign, Integer>>());
		
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
										TreeSet<Pair<Assign, Integer>> conflicts = conflictMap.get(assign);
										Pair<Assign, Integer> newConflict = new Pair<Assign, Integer>(assignmentMap.get(l2.getName()), 1);
										
										// Sanity check
										assert newConflict != null : String.format("Failed to find assignment with name %s", l2.getName());
										
										conflicts.add(newConflict);
										conflictMap.put(assign, conflicts);
										
										// We cannot simply increase the cumulative penalty by 100, as this does not account for lectures starting at the same time
										// Add the pair of conflicting assignments to our s1Set, also checking that the reverse is not already in there
										Pair<Assign, Assign> forward = new Pair<Assign, Assign>(assign, newConflict.getKey());
										Pair<Assign, Assign> reverse = new Pair<Assign, Assign>(newConflict.getKey(), assign);
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
								TreeSet<Pair<Assign, Integer>> conflicts = conflictMap.get(assign);
								Pair<Assign, Integer> newConflict = new Pair<Assign, Integer>(assignmentMap.get(l2.getName()), 2);
								
								// Sanity check
								assert newConflict != null : String.format("Failed to find assignment with name %s", l2.getName());
								
								conflicts.add(newConflict);
								conflictMap.put(assign, conflicts);
								
								// Similar to soft constraint 1, we don't want to add the penalty twice
								Pair<Assign, Assign> newPair = new Pair<Assign, Assign>(assign, newConflict.getKey());
								Pair<Assign, Assign> reverse = new Pair<Assign, Assign>(newConflict.getKey(), assign);
								if (!s2Set.contains(newPair) && !s2Set.contains(reverse))
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
										TreeSet<Pair<Assign, Integer>> conflicts = conflictMap.get(assign);
										Pair<Assign, Integer> newConflict = new Pair<Assign, Integer>(assignmentMap.get(l2.getName()), 5);
										
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
				TreeSet<Pair<Assign, Integer>> conflicts = conflictMap.get(assign);
				Pair<Assign, Integer> newConflict = new Pair<Assign, Integer>(assignmentMap.get(l1.getName()), 7);
				
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
					TreeSet<Pair<Assign, Integer>> conflicts = conflictMap.get(a1);					
					for (Assign a2 : assigns) {
						conflicts.add(new Pair<Assign, Integer>(a2, 3));
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
					TreeSet<Pair<Assign, Integer>> conflicts = conflictMap.get(assign);
					
					for (Lecture l2 : lectures) {
						
						Pair<Assign, Integer> newConflict = new Pair<Assign, Integer>(assignmentMap.get(l2.getName()), 6);
						
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
		HashMap<Assign, Integer> penaltyMap = new HashMap<Assign, Integer>();
		for (Assign assign : assignmentMap.values()) {
			penaltyMap.put(assign, 0);
		}
		
		// For every set of conflicting assignments...
		for (TreeSet<Pair<Assign, Integer>> set : conflictingAssignments.values()) {
			
			// Iterate over every member of that set
			for (Pair<Assign, Integer> pair : set) {
			 
				Assign assign = pair.getKey();
				int constraint = pair.getValue();
				
				// Get the old penalty
				int penalty = penaltyMap.get(assign);
				
				// Increment it accordingly
				switch (constraint) {
				case 1 : 
					penalty += 100;
					break;
				case 2 : 
					penalty += 20;
					break;
				case 3 : 
					penalty += 50;
					break;
				case 4 : 
					penalty += 50;
					break;
				case 5 : 
					penalty += 50;
					break;
				case 6 : 
					penalty += 20;
					break;
				case 7 : 
					penalty += 5;
					break;
				}
				// Put the new penalty in the map
				penaltyMap.put(assign, penalty);
			}
		}
						
		// Sort our list of assignments by the total weight of the conflicts they are involved in
		Vector<Assign> assignments = new Vector<Assign>(assignmentMap.values());
		rankedAssignments = sortAssignments(assignments, penaltyMap);
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
	
	
	public TreeSet<Assign> extractBest(int num, TreeSet<Assign> used) {
		
		// Sanity check
		assert num <= numLectures : "Attempting to extract more assignments than possible";
				
		HashMap<String, Assign> fixedAssignments = (HashMap<String, Assign>)environment.getFixedAssignments();
		TreeSet<Assign> bestAssignments = new TreeSet<Assign>();
				
		// We want to start with the first assignment, i.e. the best one
		for (int count = 0, index = 0; count < num && index < numLectures; ++ count) {
			
			Assign best = rankedAssignments.get(index);
			
			// Make sure we're not grabbing a fixed assignment or an assignment for a lecture which has already been assigned, and also that we don't go out of bounds
			while (fixedAssignments.values().contains(best) && used.contains(best) && index < numLectures) {
				best = rankedAssignments.get(index);
				++index;
			}
						
//			System.out.println("Adding " + best.getName() + "," + best.getSession().getName());
			
			// Add the assignment to our set
			bestAssignments.add(best);
			
			// Increment our index
			++index;
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
	
	public long getCapacityOfSession(Session session) {
		return currentRoomCapacities.get(session);
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
	
	public Session getSessionOfCourse(Course course) {
		return sessionsOfCourses.get(course);
	}
	
	
	// Return the completeness of a solution
	public boolean isComplete() {
		complete = assignmentMap.size() == numLectures;
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
	
	public HashMap<Assign, TreeSet<Pair<Assign, Integer>>> getConflictingAssignmentMap() {
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
		String result = "// Solution penalty is " + getPenalty() + "\n";
		for (Assign assign : assignmentMap.values()) {
			result = result + assign.toString() + "\n";
		}
		return result;
	}
	
	public String conflictMapToString() {
		
		String result = "Conflict map = {\n";
		for (Map.Entry<Assign, TreeSet<Pair<Assign, Integer>>> entry : conflictingAssignments.entrySet()) {
			result = result + entry.getKey().toString() + " -> ";
			for (Pair<Assign, Integer> pair : entry.getValue()) {
				result = result + pair.getKey().toString() + " ";
			}
			result  = result + "\n";
		}
		result = result + "}";
		return result;
	}	
}
