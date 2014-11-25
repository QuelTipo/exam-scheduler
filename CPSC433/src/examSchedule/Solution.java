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
			currentRoomCapacities.put(session, session.getRoom().getCapacity());
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
									
									// If they are the same student, increase the penalty by 100 and update our conflict map

									if (st1.equals(st2)) {
										cumulativePenalty += 100;
										System.out.println("Student problem");
										TreeSet<Assign> conflicts = conflictMap.get(assign);
										Assign newConflict = assignmentMap.get(l2.getName() + " " + s2.getName());
										
										// Sanity check
										assert newConflict != null : String.format("Failed to find assignment with name %s", l2.getName() + s2.getName());
										
										conflicts.add(newConflict);
										conflictMap.put(assign, conflicts);
									}
								}
							}
							
							// Also check if the both courses have the same instructor and the lectures are in different sessions
							Instructor i1 = l1.getInstructor();
							Instructor i2 = l2.getInstructor();
							if (i1.equals(i2) && !s1.equals(s2)) {
								cumulativePenalty += 20;
								System.out.println("Instructor problem");
								TreeSet<Assign> conflicts = conflictMap.get(assign);
								Assign newConflict = assignmentMap.get(l2.getName() + " " + s2.getName());
								
								// Sanity check
								// Sanity check
								assert newConflict != null : String.format("Failed to find assignment with name %s", l2.getName() + s2.getName());
								
								conflicts.add(newConflict);
								conflictMap.put(assign, conflicts);
							}
						}
						
						// Now check if the lectures are back to back
						else if (t1 + l1.getLength() == t2) {
							
							// Check to see if there are students in both lectures
							for (Student st1 : l1.getStudents()) {
								
								for (Student st2 : l2.getStudents()) {
									
									// If they are the same student, increase the penalty accordingly
									if (st1.equals(st2)) {
										cumulativePenalty += 50;
										TreeSet<Assign> conflicts = conflictMap.get(assign);
										Assign newConflict = assignmentMap.get(l2.getName() + " " + s2.getName());
										
										// Sanity check
										assert newConflict != null : String.format("Failed to find assignment with name %s", l2.getName() + s2.getName());
										
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
				System.out.println(l1.getLength());
				System.out.println(s1.getLength());
				cumulativePenalty += 5;
				System.out.println("Tight fit problem");
				TreeSet<Assign> conflicts = conflictMap.get(assign);
				Assign newConflict = assignmentMap.get(l1.getName() + " " + s1.getName());
				
				// Sanity check
				assert newConflict != null : String.format("Failed to find assignment with name %s", l1.getName() + s1.getName());
				
				conflicts.add(newConflict);
				conflictMap.put(assign, conflicts);
			}
		}
		
		// Every lecture for the same course should have the same exam timeslot - 50
		// For every course
		for (Course course : environment.getCourseList()) {
			
			TreeSet<Assign> assigns = new TreeSet<Assign>();
			TreeSet<Pair<Day, Integer>> examTimes = new TreeSet<Pair<Day, Integer>>();
			
			// And for every lecture that course has
			for (Lecture l1 : course.getLectures()) {
				
				// Search the list of assignment names
				for (String name : assignmentMap.keySet()) {
					
					// When we find the name corresponding to the lecture
					String[] components = name.split(" ");
					if (components[1].equals(l1.getName())) {
						
						// Add the day/time pair for that session to our set
						Assign assign = assignmentMap.get(name);
						Session session = assign.getSession();
						Pair<Day, Integer> dayTimePair = new Pair<Day, Integer>(session.getDay(), (int)session.getLength());
						examTimes.add(dayTimePair);
						
						// Add the assignment to our set of assignments
						assigns.add(assign);
					}
				}
			}
			
			// If there our day/time set has more than 1 entry
			int numTimeSlots = examTimes.size();
			if (numTimeSlots > 1) {
				// Increase the penalty by 50
				cumulativePenalty += ((numTimeSlots - 1) * 50);
				System.out.println("Time slot problem");
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
				if (time > 5)
					cumulativePenalty += 50;
					System.out.println("5 hour problem");
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
				// Update our conflict map
				for (Lecture l1 : lectures) {
					
					Assign assign = assignmentMap.get(l1.getName() + " " + session.getName());
					TreeSet<Assign> conflicts = conflictMap.get(assign);
					for (Lecture l2 : lectures) {
						
						Assign newConflict = assignmentMap.get(l2.getName() + " " + session.getName());
						
						// Sanity check
						assert newConflict != null : String.format("Failed to find assignment with name %s", l1.getName() + session.getName());
						
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
		// Linear sort for the moment...
		// Clone because we don't want to be removing variables
		HashMap<String, Assign> clone = new HashMap<String, Assign>(assignmentMap);
		TreeSet<Assign> remainingAssignments = (TreeSet<Assign>)clone.values();
		for (int index = 0; index < numLectures; ++index) {
			
			// Initially we don't have an assignment selected
			Assign selected = null;
			
			// Iterate over the list of remaining assignments
			for (Assign assign : remainingAssignments) {
				
				// If we haven't selected on yet, do so
				if (selected == null)
					selected = assign;
				// Otherwise, see if what we're looking at is better than what we have
				else {
					int oldConflicts = numConflicts.get(selected);
					int newConflicts = numConflicts.get(assign);
					if (newConflicts < oldConflicts)
						selected = assign;
				}
			}
			// Remove the assignment we're going to use
			remainingAssignments.remove(selected);
			// Place it in our vector
			rankedAssignments.set(index, selected);
		}
		
	}


	// This method is a component of the mutate operation, and will unassign the worst n assignments
	public void unassignWorst() {
		
		TreeSet<Assign> fixedAssignments = (TreeSet<Assign>)environment.getFixedAssignments().values();
		
		int index = (int)numLectures - 1;
		for (int count = 0; count < 5; ++count) {
			
			Assign worst = rankedAssignments.get(index);
			
			// Make sure we don't remove a fixed assignment
			while (fixedAssignments.contains(worst))
				worst = rankedAssignments.get(++index);
			
			removeAssignment(worst);
		}
	}
	
	public TreeSet<Assign> extractBest(int num) {
		
		TreeSet<Assign> fixedAssignments = (TreeSet<Assign>)environment.getFixedAssignments().values();

		TreeSet<Assign> bestAssignments = new TreeSet<Assign>();
		
		int index = 0;
		for (int count = 0; count < num; ++ count) {
			
			Assign best = rankedAssignments.get(index);
			
			// Make sure we're not grabbing a fixed assignment
			while (fixedAssignments.contains(best))
				best = rankedAssignments.get(++index);
			
			bestAssignments.add(best);
		}
		
		return bestAssignments;
	}
	
	public void decreaseSessionCapacity(Session session, Lecture lecture) {
		long capacity = currentRoomCapacities.get(session);
		capacity -= lecture.getClassSize();
		currentRoomCapacities.put(session, capacity);
	}
	
	public void increaseSessionCapacity(Session session, Lecture lecture) {
		long capacity = currentRoomCapacities.get(session);
		capacity += lecture.getClassSize();
		currentRoomCapacities.put(session, capacity);
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
}
