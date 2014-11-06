package examSchedule.parser;

public interface PredicateReaderInterface {

	/**
	 * Attempts to assert a predicate, created from <code>line</code>
	 * using {@link PredicateReader#makePredicate(String)}.  If the line is empty,
	 * just contains an comment, or the predicate is is malformed,
	 * no attempt is made to call {@link #assert_(Predicate)}.
	 * @param line the line to make a predicate from.
	 */
	public void assert_(String line);

	/**
	 * Attempts to evaluate a predicate, created from <code>line</code>
	 * using {@link PredicateReader#makePredicate(String)}.  If the line is empty,
	 * just contains an comment, or the predicate is is malformed,
	 * not attempt is made to call {@link #eval(Predicate)}, and false
	 * is returned.
	 * @param line the line to make a predicate from.
	 * @return false if there's problems (above) or there is no such predicate; 
	 * the return value of the predicate interpreter method otherwise.
	 */

	public boolean eval(String line);
	/**
	 * Attempts to assert the predicate by searching for and executing
	 * a method named "a_[PredicateName]" (where [PredicateName] is the
	 * name of the Predicate object <code>pred</code>) and that has
	 * a parameter type list that matches the predicate's parameter type
	 * list. 
	 * @param pred a predicate object to assert.
	 */
	public abstract void assert_(Predicate pred);

	/**
	 * Attempts to evaluate the predicate by searching for and executing
	 * a method named "a_[PredicateName]" (where [PredicateName] is the
	 * name of the Predicate object <code>pred</code>) and that has
	 * a parameter type list that matches the predicate's parameter type
	 * list. 
	 * @param pred a predicate object to evaluate.
	 * @return the value returned by the method described above; false if
	 * something when wrong.
	 */
	public abstract boolean eval(Predicate pred);

}