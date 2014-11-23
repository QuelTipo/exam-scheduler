package examSchedule;

import java.util.TreeSet;

public class SolutionGenerator {
	
	private Environment environment;
	private TreeSet<Solution> solutions = new TreeSet<Solution>();
	private TreeSet<Session> sessions;
	
	public SolutionGenerator(Environment environment) {
		
		this.environment = environment;
		sessions = environment.getSessionList();
	}
	
	public Solution buildSolution() {
	
		Solution newSolution = new Solution(environment);
		TreeSet<Lecture> remainingLectures = new TreeSet<Lecture>(newSolution.getUnassignedLectures());
		
		
		
		return newSolution;

	}
	
	
	

}
