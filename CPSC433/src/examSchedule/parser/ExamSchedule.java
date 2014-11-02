package examSchedule.parser;

import examSchedule.Environment;

/**
 * This class can function as the main() for your assignment program.  Use it in conjunction
 * with the PredicateReader class.  You will have to define an environment, which here is
 * represented by the class Environment (you will have to define your own Environment
 * class).  Usually, it would be a subclass of PredicateReader.
 * 
 * <p>Copyright: Copyright (c) 2003-2007, Department of Computer Science, University 
 * of Calgary.  Permission to use, copy, modify, distribute and sell this 
 * software and its documentation for any purpose is hereby granted without 
 * fee, provided that the above copyright notice appear in all copies and that
 * both that copyright notice and this permission notice appear in supporting 
 * documentation.  The Department of Computer Science makes no representations
 * about the suitability of this software for any purpose.  It is provided
 * "as is" without express or implied warranty.</p>
 *
 * @author <a href="http://www.cpsc.ucalgary.ca/~kremer/">Rob Kremer</a>
 *
 */
public class ExamSchedule {

	/**
	 * Interpret the command line, and either run in command mode, perform a search, or evaluate 
	 * a solution.  There are four acceptable forms of the command line:<p>
	 * <ol>
	 * <li>&lt;no params&gt;<blockquote>operates in "{@link #commandMode(EnvironmentInterface) command mode}"</blockquote>
	 * <li><em>problemFilename</em> <blockquote>reads problemFilename as a data file and then enters "{@link #commandMode(EnvironmentInterface) command mode}"</blockquote>
	 * <li><em>problemFilename long</em> <blockquote>reads problemFilename and then attempts to perform a {@link #doSearch(EnvironmentInterface, String, long) search} for up to <em>long</em> milliseconds</blockquote>
	 * <li><em>problemFilename solutionFilename</em> <blockquote>reads problemFilename and solutionFilename and prints out an evaluation of the solution</blockquote>
	 * </ol>
	 * Form 3 is the one need to use for the assessment of the assignment.  The other forms are usually useful for development and testing.<p>
	 * The specified input file (in forms 2-4) is read by calling {@link EnvironmentInterface#fromFile(String)}.
	 * @param args the arguments from the command line
	 */
	public static void main(String[] args) {

		final EnvironmentInterface env = Environment.get();

		//long startTime = System.currentTimeMillis();

		String fromFile = null;

		//intialize the the environment from the filename in the first argument on the command line if it's there
		if (args.length>0) {
			fromFile = args[0];
			env.fromFile(fromFile);
		}
		else {
			System.out.println("Synopsis: ExamSchedule <env-file> [<solution-file>|<time-in-ms>]");
		}


    // if there's a second argument on the command line, it's either 
		// a solution file name or a time in milliseconds to limit our run to...
		if (args.length>1) {
			// let's assume it's a time in milliseconds: we'll do a search on it.
			try {
				long timeLimit = new Long(args[1]).longValue();
        doSearch(env, fromFile+".out", timeLimit);
			}
			// not a time, so it must be a filename to read a solution to evaluate from...
			catch (NumberFormatException ex) {
				env.setCurrentSolution(new TempSolution(args[1]));
			}

			// if we did something usefull above, print the results...
			if (env.getCurrentSolution()!=null) {
				//System.out.println(currentSolution.toString());
				System.out.println(((Solution)env.getCurrentSolution()).getName()+": isSolved()    -> "+env.getCurrentSolution().isSolved());
				System.out.println(((Solution)env.getCurrentSolution()).getName()+": getGoodness() -> "+env.getCurrentSolution().getGoodness());
			}

		}
		// The command line had either no arguments or just a input data file (from which we've
		// already initialized (or attempted), so we'll enter command mode...
		else {
			commandMode(env);
		}
	}

	/**
	 * Do a search given the current environment (containing the problem data), writing it out to <em>outFileName</em>,
	 * and limiting ourselves to <em>timeLimit</em> milliseconds of search.  This method uses the arguably clever ploy
	 * of using a shutdown hook to save the results if we terminate abnormally or are killed 
	 * before we get around to shutting down ourselves.
	 * @param env The environment object that contains all the info about the current situation
	 * @param outFileName The name of the file to output the solution to.
	 * @param timeLimit The number of milliseconds to limit he search to.
	 */
	public static void doSearch(final EnvironmentInterface env, final String outFileName, final long timeLimit) {
		//To be filled in as part of the assignment...
		System.out.println("No search currently implemented.");
	}
	
	/**
	 * Run in "command mode" -- continuously reading predicates from the command line.  If a predicate
	 * begins with a "!", then we will attempt to execute it with a {@link PredicateReader#eval(String)},
	 * otherwise with a {@link PredicateReader#assert_(String)}.  Special tokens handled anomalously are:
	 * <ul>
	 * <li>exit // exits the program
	 * <li>? // executes the prediate "!help()"
	 * <li>help // executes the prediate "!help()"
	 * </ul>
	 * @param env
	 */
	public static void commandMode(final EnvironmentInterface env) {
		final int maxBuf = 200;
		byte[] buf = new byte[maxBuf];
		int length;
		try {
			System.out.print("\nTAallocation: query using predicates, assert using \"!\" prefixing predicates;\n !exit() to quit; !help() for help.\n\n> ");
			while ((length=System.in.read(buf))!=-1) {
				String s = new String(buf,0,length);
				s = s.trim();
				if (s.equals("exit")) break;
				if (s.equals("?")||s.equals("help")) {
					s = "!help()";
					System.out.println("> !help()");
				}
				if (s.length()>0) {
					if (s.charAt(0)=='!') 
						env.assert_(s.substring(1));
					else 
						System.out.print(" --> "+env.eval(s));
				}
				System.out.print("\n> ");
			}
		} catch (Exception e) {
			System.err.println("exiting: "+e.toString());
		}
	}

}
