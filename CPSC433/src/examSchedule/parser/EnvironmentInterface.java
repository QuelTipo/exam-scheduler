package examSchedule.parser;


public interface EnvironmentInterface extends PredicateReaderInterface {

	public abstract void a_search(String search, String control, Long maxTime);
	
	public int fromFile(String fileName);

	/**
	 * @return the currentSolution
	 */
	public SolutionInterface getCurrentSolution();
	
	/**
	 * @param currentSolution the currentSolution to set
	 */
	public void setCurrentSolution(SolutionInterface currentSolution);
	
	public void printDetails();
	
}