package examSchedule;

import java.util.TreeSet;

public class Search {

	private Environment environment;
	private SolutionGenerator generator;
	private TreeSet<Solution> solutions;
	private Solution bestSolution;
	
	// Default constructor
	public Search(Environment env) {
		
		this.environment = env;
		this.generator = new SolutionGenerator(environment);
	}
	
	// This method will do the necessary setup for our set based search
	public void setup() {
		
		// Begin by populating our solution set with random, valid solutions
		for (int i = 0; i < 5; ++i) {
			Solution solution = generator.buildSolution();
			solutions.add(solution);
		}
		
		// Now set our initial best solution
		for (Solution solution : solutions) {
			if (bestSolution == null || solution.getPenalty() < bestSolution.getPenalty())
				bestSolution = solution;
		}
	}
	

	
}
