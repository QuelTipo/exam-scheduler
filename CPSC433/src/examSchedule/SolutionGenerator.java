/*
 * This class is used to generate random solutions
 * The main functionality is a recursive method, buildDown, which assigns one random lecture to one random, valid session, and recurses, returning the complete solution or null
 * 
 */

package examSchedule;

import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

public class SolutionGenerator {
	
	private Environment environment;
	private TreeMap<Long,TreeSet<Session>> sessionLengths = new TreeMap<Long,TreeSet<Session>>();
	private long endTime = 0;
	
	public SolutionGenerator(Environment environment, Long endTime) {
		
		this.environment = environment;
		this.endTime = endTime;
		
		TreeSet<Session> sessions = new TreeSet<Session>(environment.getSessionList());
		
		
		// initialize the TreeSets in the hashmap
		for (Session session : sessions) {
			sessionLengths.put(new Long(session.getLength()), new TreeSet<Session>());
		}

		
		// put the sessions into the initialized TreeSets
		
		for (Session session : sessions) {
			TreeSet<Session> sameSizeSessions = sessionLengths.get(session.getLength());
			sameSizeSessions.add(session);
		}
		
	}
		
	public Solution buildSolution() {
	
		Solution newSolution = new Solution(environment);
				
		Random random = new Random();
		
		newSolution = buildDown(newSolution, random);
		
		if (newSolution != null)
			newSolution.calculatePenalty();
		
		return newSolution;

	}
	
	public Solution buildDown(Solution tempSolution, Random random) {
	
		//if there's no more lectures to add we have found a solution
		Vector<Lecture> remainingLectures = new Vector<Lecture>(tempSolution.getUnassignedLectures());
		
		if (remainingLectures.size() == 0) {
			return tempSolution;
		}
		
		while(remainingLectures.size() > 0 && System.currentTimeMillis() < endTime) {
		
		//otherwise pick a random lecture from the remaining ones
		
			int randIndex = random.nextInt(remainingLectures.size());
			Lecture tryLecture = remainingLectures.remove(randIndex);
		
			//first session to try is the session already assigned to a particular course.
			//if it doesn't succeed, continue business as usual.
			
			Session hailMarySession = tempSolution.getSessionOfCourse(tryLecture.getCourse());
			if (hailMarySession != null) {
				if (tryLecture.getClassSize() <= tempSolution.getCapacityOfSession(hailMarySession)) {
					Assign hailMaryAssign = new Assign(tryLecture, hailMarySession);
				
					boolean ret = tempSolution.dumbAddAssign(hailMaryAssign);
					if (ret) {
						if (buildDown(tempSolution, random) != null) {
							return tempSolution;
						}
						tempSolution.removeAssignment(hailMaryAssign);
					}
					
				}				
			}
			
			
			//get list of lengths equal to or greater than session length
			TreeMap<Long,TreeSet<Session>> validLengths = new TreeMap<Long, TreeSet<Session>>(sessionLengths.tailMap(tryLecture.getLength(), true));
			
			while (validLengths.size() > 0) {
			
				TreeSet<Session> goodSessions = validLengths.pollFirstEntry().getValue();
			
				Vector<Session> sessionsToTry = tempSolution.weedOutByCapacity(goodSessions, tryLecture.getClassSize());
			
				while (sessionsToTry.size() > 0) {
			
					//get the session and remove it from the list
					randIndex = random.nextInt(sessionsToTry.size());
					Session tempSession = sessionsToTry.remove(randIndex);
				
					Assign tryAssign = new Assign(tryLecture, tempSession);
					boolean ret = tempSolution.dumbAddAssign(tryAssign);
				
					//recursively call to add another assignment
					//if we get something back, we have a winner.
					
					if (ret) {
						if (buildDown(tempSolution, random) != null) {
							return tempSolution;
						}
						tempSolution.removeAssignment(tryAssign);
					}
					//if nothing was passed back up, then let's try a different session						
				}			
			}
		}
			
		//We have no options left, let's go back and try another route.
		return null;
			

	}
	
	
	

}
