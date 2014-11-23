package examSchedule;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

public class SolutionGenerator {
	
	private Environment environment;
	private TreeSet<Solution> solutions = new TreeSet<Solution>();
	private TreeMap<Long,Session> sessionLengths = new TreeMap<Long,Session>();
	private TreeMap<Session,Long> sessionCapacities = new TreeMap<Session,Long>();
	
	public SolutionGenerator(Environment environment) {
		
		this.environment = environment;
		
		TreeSet<Session> sessions = environment.getSessionList();
		
		for (Session session : sessions) {
			sessionLengths.put(new Long(session.getLength()), session);
			sessionCapacities.put(session, new Long(session.getRoom().getCurrentCapacity()));
		}
		
	}
	
	public Solution buildSolution() {
	
		Solution newSolution = new Solution(environment);
		Vector<Lecture> remainingLectures = new Vector<Lecture>(newSolution.getUnassignedLectures());
		
		Random random = new Random();
		
		
		
		return newSolution;

	}
	
	private Solution buildDown(Solution tempSolution, Vector<Lecture> remainingLectures, Random random) {
		
		if (remainingLectures.size() == 0) {
			return tempSolution;
		}
		
		int randIndex = random.nextInt(remainingLectures.size());
		Lecture tryLecture = remainingLectures.get(randIndex);
		
		//get list of lengths equal to or greater than session length
		NavigableMap<Long,Session> lengthsToTry = sessionLengths.tailMap(tryLecture.getLength(), true);
		
		return tempSolution;
			

	}
	
	
	

}
