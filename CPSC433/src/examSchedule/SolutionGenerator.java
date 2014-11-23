package examSchedule;

import java.util.TreeMap;
import java.util.TreeSet;

public class SolutionGenerator {
	
	private Environment environment;
	private TreeSet<Solution> solutions = new TreeSet<Solution>();
	private TreeMap<Long,Session> sessionLengths = new TreeMap<Long,Session>();
	private TreeMap<Long,Session> sessionCapacities = new TreeMap<Long,Session>();
	
	public SolutionGenerator(Environment environment) {
		
		this.environment = environment;
		
		TreeSet<Session> sessions = environment.getSessionList();
		
		for (Session session : sessions) {
			sessionLengths.put(new Long(session.getLength()), session);
			sessionCapacities.put(new Long(session.getRoom().getCurrentCapacity()), session);
		}
		
	}
	
	public Solution buildSolution() {
	
		Solution newSolution = new Solution(environment);
		TreeSet<Lecture> remainingLectures = new TreeSet<Lecture>(newSolution.getUnassignedLectures());
		
		
		
		
		return newSolution;

	}
	
	
	

}
