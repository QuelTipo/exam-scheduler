package examSchedule.parser;

import examSchedule.SolutionInterface;

public abstract class Solution extends Entity implements SolutionInterface {

	public Solution(String filename) {
		// you figure it out...
	}

	public abstract boolean isSolved();

	public abstract boolean isComplete();

	public abstract boolean hasViolations();

	public abstract int getGoodness();

	public abstract String toString();

}
