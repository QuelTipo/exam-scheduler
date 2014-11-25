package examSchedule;

import java.util.Collection;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

public class SolutionGenerator {
	
	private Environment environment;
	private TreeMap<Long,TreeSet<Session>> sessionLengths = new TreeMap<Long,TreeSet<Session>>();
	
	public SolutionGenerator(Environment environment) {
		
		this.environment = environment;
		
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
		
		while(remainingLectures.size() > 0) {
		
		//otherwise pick a random lecture from the remaining ones
		
			int randIndex = random.nextInt(remainingLectures.size());
			Lecture tryLecture = remainingLectures.remove(randIndex);
			
			//get list of lengths equal to or greater than session length
			NavigableMap<Long,TreeSet<Session>> validLengths = sessionLengths.tailMap(tryLecture.getLength(), true);
			//throw them in a vector to ease randomized access
			Collection<TreeSet<Session>> toCollection = validLengths.values();
			TreeSet<Session> goodSessions = new TreeSet<Session>();
			for (TreeSet<Session> sessionList : toCollection) {
				goodSessions.addAll(sessionList);				
			}
			
			Vector<Session> sessionsToTry = tempSolution.weedOutByCapacity(goodSessions, tryLecture.getClassSize());
			
			while (sessionsToTry.size() > 0) {
			
				//get the session and remove it from the list
				randIndex = random.nextInt(sessionsToTry.size());
				Session tempSession = sessionsToTry.remove(randIndex);
				

				Assign tryAssign = new Assign(tryLecture, tempSession);
				tempSolution.dumbAddAssign(tryAssign);
						
				//recursively call to add another assignment
				//if we get something back, we have a winner.
					
				if (buildDown(tempSolution, random) != null) {
					return tempSolution;
				}
					
				//if nothing was passed back up, then let's try a different session
					
				tempSolution.removeAssignment(tryAssign);
					
					
			}
			
			
		}
			
		//We have no options left, let's go back and try another route.
		
		return null;
			

	}
	
	
	

}
