package examSchedule;

public interface SolutionInterface {
	
	public abstract boolean isSolved();

	public abstract boolean isComplete();

	public abstract boolean hasViolations();

	public abstract int getGoodness();

	public abstract String toString();

}