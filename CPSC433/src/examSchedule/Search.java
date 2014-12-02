/*
 * This class implements the the actual search functionality in the project
 * Search setup is done by generating 40 random solutions from which to start
 * While there is remaining time, we repeatedly call kontrol, and either mutate a solution, or combine 2 solutions
 * 
 */

package examSchedule;

import java.util.TreeSet;
import java.util.Random;
import java.util.Vector;

public class Search {

	private Environment environment;
	private SolutionGenerator generator;
	private Vector<Solution> solutions;
	private Solution bestSolution;
	private long endTime;
	
	// Default constructor
	public Search(Environment env, long endTime) {
		
		this.environment = env;
		this.generator = new SolutionGenerator(environment, endTime);
		this.solutions = new Vector<Solution>();
		this.endTime = endTime;
	}
	
	// This method will do the necessary setup for our set based search
	public void setup() {
		
		// We want to continue searching for starting solutions as long as we don't have any solutions and there is time left on the clock
		while (System.currentTimeMillis() < endTime && solutions.size() == 0) {
			
			// Begin by populating our solution set with random, valid solutions
			for (int i = 0; i < 40; ++i) {
				
				// Generate a new solution
				Solution solution = generator.buildSolution();
				if (solution != null) {
					
					// If we succeed, rank the assignments and add it to our list of solutions
					solution.rankAssignments();
					solutions.add(solution);
				}
			}
	
		}
			
		// If we got at least one solution, set bestSolution
		if (solutions.size() > 0) {
			for (Solution solution : solutions) {
				if (bestSolution == null || solution.getPenalty() < bestSolution.getPenalty())
					bestSolution = solution;
			}
		}
	}
	
	
	// This version of Kontrol is very lazy and will just randomly do things 
	private void randomKontrol() {
		
		Random random = new Random();
		float trigger = random.nextFloat();
		
		
		if (trigger < 0.1) { //mutate a solution
			Solution solution = solutions.get(random.nextInt(solutions.size()));
			Solution mutation = dumbMutation(solution);
			solutions.add(mutation);
		} else if (trigger < 0.55) { //crossover the best solution and random solution
			Solution sol1 = solutions.get(random.nextInt(solutions.size()));
			Solution combination = dumbCrossover(getBestSolution(), sol1);
			solutions.add(combination);
		} 
		else { //crossover two random solutions
			Solution sol1 = solutions.get(random.nextInt(solutions.size()));
			Solution sol2 = solutions.get(random.nextInt(solutions.size()));
			Solution combination = dumbCrossover(sol1, sol2);
			solutions.add(combination);
		}
	}
	
	// This method will continually call kontrol while there is still time left on the clock
	public void letsSearching() {
		
		int counter = 1;
		while (System.currentTimeMillis() < endTime) {
			if (counter % 100 == 0) {
				System.out.println("Assuming direct kontrol! Time #" + counter);
			}
			randomKontrol();
			++counter;
		}
	}
	
	public Vector<Solution> getCurrentSolutions() {
		return solutions;
	}
	
	// This method will mutate a particular solution
	public Solution dumbMutation(Solution solution) {
		
		// Create a new solution
		Solution mutation = new Solution(environment);
		int numAssignments = solution.getAssignments().size();
		
		// Extract the best 2/3 assignments from the solution
		TreeSet<Assign> best = solution.extractBest((2 * numAssignments) / 3, new TreeSet<Assign>());
		
		// Add the assignments we want to keep to our mutation
		for (Assign assign : best) {
			if (mutation.dumbAddAssign(assign) == false) {
				//System.out.println("Failed to add " + assign.toString() + " during crossever");
			}
		}
		
		// Use our SolutionGenerator to complete the solution
		generator.buildDown(mutation, new Random());
		mutation.calculatePenalty();
		mutation.rankAssignments();
		
		if (mutation.getPenalty() < bestSolution.getPenalty()) {
			bestSolution = mutation;
		}
		
		return mutation;
	}
	
	
	// This method will combine 2 solutions
	public Solution dumbCrossover(Solution s1, Solution s2) {
		
		int numLectures = environment.getLectureList().size();
		int numFixed = environment.getFixedAssignments().size();
		
		// Determine which solution is better
		Solution better, worse;
		if (s1.getPenalty() < s2.getPenalty()) {
			better = s1;
			worse = s2;
		}
		else {
			better = s2;
			worse = s1;
		}
		
		// Create a new solution 
		Solution combination = new Solution(environment);
				
		long total = worse.getPenalty() + better.getPenalty();
		int numWorse = (int)(((float)better.getPenalty() / (float)total) * (numLectures - numFixed));
		int numBetter = (numLectures - numFixed) - numWorse;
	
		// Get the best n from the better solution that aren't fixed assignments
		TreeSet<Assign> bestAssignments = better.extractBest(numBetter, new TreeSet<Assign>());
		
		// Add them to our new solution
		// Every member of this set should be added without problems
		for (Assign assign : bestAssignments) {
			if  (combination.dumbAddAssign(assign) == false) {
				//assert false : "Error - Failed to add " + assign.toString() + " during crossever";
			}
		}
		
		// Now get the best m from the worse solution that aren't fixed assignments
		bestAssignments = worse.extractBest(numWorse, bestAssignments);
		
		// Attempt to add them to our new solution
		for (Assign assign : bestAssignments) {
			if (combination.dumbAddAssign(assign) == false) {
				//System.out.println("Failed to add " + assign.toString() + " during crossever");
			}
		}
		
		// Attempt to complete any incomplete solutions, return false if we are unable to do so
		if (!combination.isComplete()) {
			combination = generator.buildDown(combination, new Random());
			if (combination == null)
				return null;
		}
		
		// Calculate the new solution's penalty
		combination.calculatePenalty();
		combination.rankAssignments();
		
		if (combination.getPenalty() < bestSolution.getPenalty()) {
			bestSolution = combination;
		}
		
		solutions.remove(worse);
		
		return combination;
	}

	
	public Solution getBestSolution() {
		
		return bestSolution;
	}
	
}
