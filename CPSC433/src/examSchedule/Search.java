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
		solution.unassignWorst();
		
		// Use our SolutionGenerator to complete the solution
		generator.buildDown(solution, new Random());
	}
	
	
	// This method will combine 2 solutions
	public boolean dumbCrossover(Solution better, Solution worse) {
		
		int numLectures = environment.getLectureList().size();
		
		// Create a new solution 
		Solution combination = new Solution(environment);
		
		// Get the best n from the better solution that aren't fixed assignments
		TreeSet<Assign> bestAssignments = better.extractBest(numLectures / 2);

		// Add them to our new solution
		// Every member of this set should be added without problems
		for (Assign assign : bestAssignments) {
			assert combination.dumbAddAssign(assign) : "Error - Failed to add assignment during crossover";
		}
		
		// Now get the best n from the worse solution that aren't fixed solutions
		bestAssignments = worse.extractBest(numLectures / 2);
		
		// Attempt to add them to our new solution
		for (Assign assign : bestAssignments) {
			if (!combination.dumbAddAssign(assign))
				System.out.println("Failed to add assignment during crossever");
		}
		
		// Attempt to complete any incomplete solutions, return false if we are unable to do so
		if (!combination.isComplete()) {
			combination = generator.buildDown(combination, new Random());
			if (combination == null)
				return false;
		}
		
		// Replace the less good solution with the new one
		worse = combination;
		
		return true;
	}

	
	public Solution getBestSolution() {
		
		return bestSolution;
	}
	
}
