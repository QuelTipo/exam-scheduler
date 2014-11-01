/**
 * 
 */
package examSchedule.parser;

import java.util.Vector;

import examSchedule.parser.Predicate.ParamType;



/**
 * This interface should be <code>implement</code>ed by any class that reads Student allocation 
 * predicates.  The implementing class should also <code>extend</code> the
 * class {@link examSchedule.parser.PredicateReader}.  
 * <p>
 * The method declarations here form the
 * stubs you will need to implement all the predicates for the assignments.
 * The static String definitions here don't need to be overridden: they will
 * automatically work with {@link examSchedule.parser.PredicateReader} to yield help 
 * information when the predicate "!help()" is interpreted. 
 * <p>
 * For more information on the semantics of these predicates, see the 
 * CPSC 433 F06 assignment specification.
 * 
 * <p>Copyright: Copyright (c) 2005-2007, Department of Computer Science, University 
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
public interface ExamSchedulePredicates {
	public static String h_student = "query or assert <id> is a Student";
	public void a_student(String p);
	public boolean e_student(String p);
	
	public static String h_instructor = "query or assert <id> is an instructor";
	public void a_instructor(String p) ;
	public boolean e_instructor(String p);
	
	public static String h_room = "query or assert <id> is a room";
	public void a_room(String p) ;
	public boolean e_room(String p);
	
	public static String h_course = "query or assert <id> is a course";
	public void a_course(String p);
	public boolean e_course(String p);
	
	//public static String h_session = "query or assert <id> is an session";
	public void a_session(String p) ;
	public boolean e_session(String p);
	
	public static String h_day = "query or assert <id> is an day";
	public void a_day(String p) ;
	public boolean e_day(String p);
	
	public static String h_lecture = "query or assert course <id1> has a lecture <id2> [optional: with instructor <id3>]";
	public void a_lecture(String c, String lec);
	public boolean e_lecture(String c, String lec);
	public void a_lecture(String c, String lec, String instructor, Long length);
	
	public static String h_instructs = "query or assert intructor <id1> instructs couse <id2>, lecture <id3>";
	public void a_instructs(String p, String c, String l);
	public boolean e_instructs(String p, String c, String l);
	
	public static String h_examLength = "query or assert course <id1> and lecture <id2> has an exam length of <id3> hours";
	public void a_examLength(String c, String lec, Long hours);
	public boolean e_examLength(String c, String lec, Long hours);
	
	public static String h_roomAssign = "query or assert session <id1> is in room <id2>";
	public void a_roomAssign(String p, String room) ;
	public boolean e_roomAssign(String p, String room);
	
	public static String h_dayAssign = "query or assert session <id1> is on day <id2>";
	public void a_dayAssign(String p, String day) ;
	public boolean e_dayAssign(String p, String day);
	
	public static String h_time = "query or assert session <id1> is at time <id2>";
	public void a_time(String p, Long time) ;
	public boolean e_time(String p, Long time);
	
	public static String h_length = "query or assert session <id1> is <id2> hours long";
	public void a_length(String p, Long length) ;
	public boolean e_length(String p, Long length);
	
	public static String h_at = "query or assert a session <id1> on a particular day <id2> at time <id3>, for <id4> hours";
	public void a_at(String session, String day, Long time, Long length);
	public boolean e_at(String session, String day, Long time, Long length);
	
	public static String h_session = "query or assert a session <id1>\n\t\t[optional: in room <id2> on day <id3> at time <integer4>, for <integer5> hours]";
	public void a_session(String session, String room, String day, Long time, Long length);
	public boolean e_session(String session, String room, String day, Long time, Long length);
	
	public static String h_enrolled = "query or assert Student <id1> is taking course <id2>, lecture <id3>";
	public void a_enrolled(String student, String c, String l);
	public boolean e_enrolled(String student, String c, String l);
	
	//public static String h_enrolled = "query or assert Student <id1> is taking course <id2>, lecture <id3>";
	public void a_enrolled(String student, Vector<Pair<ParamType,Object>> list);
	
	public static String h_capacity = "query or assert room <id1> has a capacity of <id2> students";
	public void a_capacity(String r, Long cap) ;
	public boolean e_capacity(String r, Long cap);
	
	public static String h_assign = "query or assert course <id1> and lecture <id2> is assigned to session <id3>";
	public void a_assign(String c, String lec, String session);
	public boolean e_assign(String c, String lec, String session);
	
}
