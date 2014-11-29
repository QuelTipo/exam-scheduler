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
				
				Solution solution = generator.buildSolution();
				if (solution != null) {
					
					solutions.add(solution);
					solution.rankAssignments();
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
	
	public void KontrolCaller(long timeLimit) {
		//calls Kontrol repeatedly until the time runs out. 
		//Compare with letsSearching. 
		
		long currentTime = System.currentTimeMillis();
		long endTime = currentTime + timeLimit;
		for (; currentTime < endTime; currentTime = System.currentTimeMillis()) {
			System.out.println("<Dial Up Noises>: Time #" + (timeLimit-(endTime-currentTime)));
			Kontrol();
		}
	}
	
	private void Kontrol() {
		//Determine which extension rule to do: Crossover or Mutation
		
		/*
		 * CURRENT PROBLEMS: 
		 * 
		 * tc02.txt causes index out of bounds error because solutions is empty. 
		 * 
		 * */
		
		/*Define currentFact, bestFact and worstFact initialized to the first entry of the fact set*/
		Solution currentFact = solutions.firstElement();
		Solution worstFact = currentFact;
		
		int ratio = 2; //The worst fact must be more than ratio times worse than the best fact in order to crossover.
						//Change this if the ratio must be lowered. 
		
		for (int i = 1; i <= solutions.size()-1; i++) { //i is 1 because we already got the first element when we got currentFact.
			if (worstFact.getPenalty() < currentFact.getPenalty()) {
				worstFact = currentFact;
			}
			currentFact = solutions.get(i);
		}
		
		if (ratio * bestSolution.getPenalty() < worstFact.getPenalty()) { //if the worst fact is at least ratio times worse than the best
			Solution crossover = dumbCrossover(bestSolution, worstFact);
			solutions.add(crossover);
		} else {
			Random rnd = new Random();
			Solution mutation = dumbMutation(solutions.get(rnd.nextInt(solutions.size()))); //Else, dumb mutate a random entry of solutions.
			solutions.add(mutation);
		}
	}
	
	//This version of Kontrol is very lazy and will just randomly do things. Once things are working
	//with lazy Kontrol we will switch to George's smarter Kontrol 
	private void lazyKontrol() {
		
		Random random = new Random();
		float trigger = random.nextFloat();
		
		if (trigger < 0.7) {
			Solution sol1 = solutions.get(random.nextInt(solutions.size()));
			
			Solution combination = dumbCrossover(getBestSolution(), sol1);
			solutions.add(combination);
		
		} else {
			Solution mutation = dumbMutation(solutions.get(random.nextInt(solutions.size())));
			solutions.add(mutation);
		}
	}
	
	// This method will continually call kontrol while there is still time left on the clock
	public void letsSearching() {
		
		int counter = 1;
		while (System.currentTimeMillis() < endTime) {
			System.out.println("Assuming direct kontrol! Time #" + counter);
			lazyKontrol();
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
		
		// Extract the best (total - n) assignments from the solution
		TreeSet<Assign> best = solution.extractBest(numAssignments / 3);
		
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
		TreeSet<Assign> bestAssignments = better.extractBest(numBetter);
		
		// Add them to our new solution
		// Every member of this set should be added without problems
		for (Assign assign : bestAssignments) {
			if  (combination.dumbAddAssign(assign) == false) {
				//assert false : "Error - Failed to add " + assign.toString() + " during crossever";
			}
		}
		
		// Now get the best m from the worse solution that aren't fixed assignments
		bestAssignments = worse.extractBest(numWorse);
		
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
		
		return combination;
	}

	
	public Solution getBestSolution() {
		
		return bestSolution;
	}
	
}
