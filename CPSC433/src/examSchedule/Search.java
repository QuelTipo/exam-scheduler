package examSchedule;

import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Random;
import java.util.Vector;

public class Search {

	private Environment environment;
	private SolutionGenerator generator;
	private Vector<Solution> solutions;
	private Solution bestSolution;
	
	// Default constructor
	public Search(Environment env) {
		
		this.environment = env;
		this.generator = new SolutionGenerator(environment);
		this.solutions = new Vector<Solution>();
	}
	
	// This method will do the necessary setup for our set based search
	public void setup() {
		
		// Begin by populating our solution set with random, valid solutions
		for (int i = 0; i < 40; ++i) {
			Solution solution = generator.buildSolution();
			if (solution != null) {
				solutions.add(solution);
			}
		}
		
		// Now set our initial best solution
		for (Solution solution : solutions) {
			if (bestSolution == null || solution.getPenalty() < bestSolution.getPenalty())
				bestSolution = solution;
		}
	}
	
	public void Kontrol() {
		//Determine which extension rule to do: Crossover or Mutation
		
		/*Define currentFact, bestFact and worstFact initialized to the first entry of the fact set*/
		Solution currentFact = solutions.firstElement();
		Solution worstFact = currentFact;
		
		int ratio = 2; //The worst fact must be more than ratio times worse than the best fact in order to crossover.
						//Change this if the ratio must be lowered. 
		
		for (int i = 1; i <= solutions.size(); i++) { 
			if (bestSolution.getPenalty() > currentFact.getPenalty()) { //If this fact is better than the old one: 
				bestSolution = currentFact; //Update bestSolution since we have a better one. 
			}
			if (worstFact.getPenalty() < currentFact.getPenalty()) {
				worstFact = currentFact;
			}
			currentFact = solutions.get(i);
		}
		
		if (ratio * bestSolution.getPenalty() < worstFact.getPenalty()) { //if the worst fact is at least ratio times worse than the best
			dumbCrossover(bestSolution, worstFact); 
		} else {
			Random rnd = new Random();
			dumbMutation(solutions.get(rnd.nextInt(solutions.size()))); //Else, dumb mutate a random entry of solutions. 
		}
	}
	
	public Vector<Solution> getCurrentSolutions() {
		return solutions;
	}
	
	// This method will mutate a particular solution
	public void dumbMutation(Solution solution) {
		
		// Unassign the worst n assignments in the solution
		solution.unassignWorst(2);
		
		// Use our SolutionGenerator to complete the solution
		generator.buildDown(solution, new Random());
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
		TreeSet<Assign> bestAssignments = better.extractBest(numBetter);
		
		// Add them to our new solution
		// Every member of this set should be added without problems
		for (Assign assign : bestAssignments) {
			if  (combination.dumbAddAssign(assign) == false) {
				assert false : "Error - Failed to add " + assign.toString() + " during crossever";
			}
		}
		
		// Now get the best m from the worse solution that aren't fixed assignments
		bestAssignments = worse.extractBest(numWorse);
		
		// Attempt to add them to our new solution
		for (Assign assign : bestAssignments) {
			if (combination.dumbAddAssign(assign) == false) {
				System.out.println("Failed to add " + assign.toString() + " during crossever");
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
		
		return combination;
	}

	
	public Solution getBestSolution() {
		
		return bestSolution;
	}
	
}
