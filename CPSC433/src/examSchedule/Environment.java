package examSchedule;

import java.util.TreeSet;
import java.util.Vector;
import java.util.HashMap;

import examSchedule.parser.*;
import examSchedule.parser.Predicate.ParamType;


public class Environment extends PredicateReader implements ExamSchedulePredicates, EnvironmentInterface {

	private static Environment singletonEnv;
	
	private TreeSet<Student> studentList = new TreeSet<Student>();
	private TreeSet<Instructor> instructorList = new TreeSet<Instructor>();
	private TreeSet<Course> courseList = new TreeSet<Course>();
	private TreeSet<Lecture> lectureList = new TreeSet<Lecture>();
	private TreeSet<Session> sessionList = new TreeSet<Session>();
	private TreeSet<Day> dayList = new TreeSet<Day>();
	private TreeSet<Room> roomList = new TreeSet<Room>();
	
	private HashMap<String, Assign> fixedAssignmentMap = new HashMap<String, Assign>();
	
	public Environment(String string) {
		super(string);
		// TODO Auto-generated constructor stub
	}
	
	public Environment() {

	}

	public TreeSet<Student> getStudentList() {
		return studentList;
	}
	
	public TreeSet<Instructor> getInstructorList() {
		return instructorList;
	}
	
	public TreeSet<Course> getCourseList() {
		return courseList;
	}
	
	public TreeSet<Lecture> getLectureList() {
		return lectureList;
	}
	
	public TreeSet<Session> getSessionList() {
		return sessionList;
	}
	
	public TreeSet<Day> getDayList() {
		return dayList;
	}
	
	public TreeSet<Room> getRoomList() {
		return roomList;
	}
	
	public HashMap<String, Assign> getFixedAssignments() {
		return fixedAssignmentMap;
	}
		
	public void wipeFixedAssignments() {
		fixedAssignmentMap = new HashMap<String, Assign>();
	}
	
	@Override
	public void a_search(String search, String control, Long maxTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SolutionInterface getCurrentSolution() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCurrentSolution(SolutionInterface currentSolution) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void a_student(String p) {
		Student student = f_student(p);
		if (student == null) {
			student = new Student(p);
			studentList.add(student);
		}
	}

	public Student f_student(String p) {
		for (Student student : studentList) {
			if (student.getName().equals(p)) {
				return student;
			}
		}
		return null;
	}
	
	@Override
	public boolean e_student(String p) {
		return f_student(p) != null ? true : false;
	}

	@Override
	public void a_instructor(String p) {
		Instructor instructor = f_instructor(p);
		if (instructor == null) {
			instructor = new Instructor(p);
			instructorList.add(instructor);
		}
	}

	public Instructor f_instructor(String ins) {
		for (Instructor instructor : instructorList) {
			if (instructor.getName().equals(ins)) {
				return instructor;
			}
		}
		return null;
	}
	
	@Override
	public boolean e_instructor(String p) {
		return f_instructor(p) != null ? true : false;
	}

	@Override
	public void a_room(String p) {
		Room room = f_room(p);
		if (room == null) {
			room = new Room(p);
			roomList.add(room);
		}
	}

	public Room f_room(String r) {
		for (Room room : roomList) {
			if (room.getName().equals(r)) {
				return room;
			}
		}
		return null;
	}
	
	@Override
	public boolean e_room(String p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void a_course(String p) {
		Course course = f_course(p);
		if (course == null) {
			course = new Course(p);
			courseList.add(course);
		}
	}

	public Course f_course(String p) {
		for (Course course : courseList) {
			if (course.getName().equals(p)) {
				return course;
			}
		}
		return null;
	}
	
	@Override
	public boolean e_course(String p) {
		return f_course(p) != null ? true : false;
	}

	@Override
	public void a_session(String p) {
		Session session = f_session(p);
		if (session == null) {
			session = new Session(p);
			sessionList.add(session);
		}
	}

	public Session f_session(String p) {
		for (Session session : sessionList) {
			if (session.getName().equals(p)) {
				return session;
			}
		}
		return null;
	}
	
	@Override
	public boolean e_session(String p) {
		return f_session(p) != null ? true : false;
	}

	@Override
	public void a_session(String s, String r, String d, Long t, Long l) {
		
		// Ensure the room exists
		Room room = f_room(r);
		if (room ==  null) {
			room = new Room(r);
			roomList.add(room);
		}
		
		// Ensure the day exists
		Day day = f_day(d);
		if (day == null) {
			day = new Day(d);
			dayList.add(day);
		}
		
		// Ensure the session exists
		Session session = f_session(s);
		if (session == null) {
			session = new Session(s,room,day,t,l);
			sessionList.add(session);
		}
		else {
			session.update(room, day, t.longValue(), l.longValue());
		}
	}

	@Override
	public boolean e_session(String session, String room, String day, Long time, Long length) {
		return e_session(session);
	}

	
	@Override
	public void a_day(String p) {
		Day day = f_day(p);
		if (day == null) {
			day = new Day(p);
			dayList.add(day);
		}
	}

	public Day f_day(String p) {
		for (Day day : dayList) {
			if (day.getName().equals(p)) {
				return day;
			}
		}
		return null;
	}
	
	@Override
	public boolean e_day(String p) {
		return f_day(p) != null ? true : false;
	}

	@Override
	public void a_lecture(String c, String lec) {
		
		// Ensure the course object is there
		Course course = f_course(c);
		if (course == null) {
			course = new Course(c);
			courseList.add(course);
		}
		
		// Ensure the lecture object is there
		Lecture lecture = f_lecture(c, lec);
		if (lecture == null) {
			lecture = new Lecture(course, lec);
			lectureList.add(lecture);
		}
		
		// Add a pointer to the lecture to the course
		course.addLecture(lecture);
	}

	public Lecture f_lecture(String c, String lec) {
		for (Lecture lecture : lectureList) {
			if (lecture.getName().equals(c + lec)) {
				return lecture;
			}
		}
		return null;
	}
	
	@Override
	public boolean e_lecture(String c, String lec) {
		return f_lecture(c, lec) != null ? true : false;
	}

	@Override
	public void a_lecture(String c, String lec, String ins, Long length) {
		
		// Ensure the course object is there
		Course course = f_course(c);
		if (course == null) {
			course = new Course(c);
			courseList.add(course);
		}
		
		// Ensure the instructor exists
		Instructor instructor = f_instructor(ins);
		if (instructor == null) {
			instructor = new Instructor(ins);
			instructorList.add(instructor);
		}
		
		// Ensure the lecture object is there
		Lecture lecture = f_lecture(c, lec);
		if (lecture == null) {
			lecture = new Lecture(course, lec, instructor, length);
			lectureList.add(lecture);
		}
		// So at this point there is a lecture, which we may have to overwrite
		else {
			lecture.update(instructor, length);
		}
		
		// Add a pointer to the lecture to the course
		course.addLecture(lecture);
	}

	@Override
	public void a_instructs(String p, String c, String l) {
		// Ensure the course exists
		Course course = f_course(c);
		if (course == null) {
			course = new Course(c);
			courseList.add(course);
		}
				
		// Ensure the lecture exists
		Lecture lecture = f_lecture(c, l);
		if (lecture == null) {
			lecture = new Lecture(course, l);
			lectureList.add(lecture);
		}
				
		// Ensure the instructor exists
		Instructor instructor = f_instructor(p);
		if (instructor == null) {
			instructor = new Instructor(p);
			instructorList.add(instructor);
			instructor.addCourse(course, lecture);
			
		} 
		else {
			// Add the specified course and lecture to the student
			if (instructor.checkForCourse(course, lecture) == false) {
				instructor.addCourse(course, lecture);
			}
		}
		
		lecture.update(instructor);
		
		// Add a pointer to the lecture to the course
		course.addLecture(lecture);
	}
	
	@Override
	public boolean e_instructs(String p, String c, String l) {
		return f_lecture(c, l) != null ? true : false;
	}

	@Override
	public void a_examLength(String c, String lec, Long hours) {
		// Ensure the course exists
		Course course = f_course(c);
		if (course == null) {
			course = new Course(c);
			courseList.add(course);
		}
		
		// Ensure the lecture exists
		Lecture lecture = f_lecture(c, lec);
		if (lecture == null) {
			lecture = new Lecture(course, lec, hours);
			lectureList.add(lecture);
		}
		else {
			lecture.update(hours);
		}
		
		// Add a pointer to the lecture to the course
		course.addLecture(lecture);
	}

	@Override
	public boolean e_examLength(String c, String lec, Long hours) {
		return f_lecture(c, lec) != null ? true : false;
	}

	@Override
	public void a_roomAssign(String p, String r) {
		// Ensure the room exists
		Room room = f_room(r);
		if (room == null) {
			room = new Room(r);
			roomList.add(room);
		}
		
		// Ensure the session exists
		Session session = f_session(p);
		if (session == null) {
			session = new Session(p, room);
			sessionList.add(session);
		}
		else {
			session.update(room);
		}
	}

	
	@Override
	public boolean e_roomAssign(String p, String r) {
		return f_session(p) != null ? true : false;
	}

	@Override
	public void a_dayAssign(String p, String d) {
		// Ensure the day exists
		Day day = f_day(d);
		if (day == null) {
			day = new Day(d);
			dayList.add(day);
		}
		
		// Ensure the session exists
		Session session = f_session(p);
		if (session == null) {
			session = new Session(p, day);
			sessionList.add(session);
		}
		else {
			session.update(day);
		}
	}
	
	@Override
	public boolean e_dayAssign(String p, String day) {
		return f_session(p) != null ? true : false;
	}

	@Override
	public void a_time(String p, Long time) {
		// Ensure the session exists
		Session session = f_session(p);
		if (session == null) {
			session = new Session(p, time, true);
			sessionList.add(session);
		}
		else {
			session.update(time, true);
		}
		
	}

	@Override
	public boolean e_time(String p, Long time) {
		return f_session(p) != null ? true : false;
	}

	@Override
	public void a_length(String p, Long length) {
		// Ensure the session exists
		Session session = f_session(p);
		if (session == null) {
			session = new Session(p, length, false);
			sessionList.add(session);
		}
		else {
			session.update(length, false);
		}
		
	}

	@Override
	public boolean e_length(String p, Long length) {
		return f_session(p) != null ? true : false;
	}

	@Override
	public void a_at(String s, String d, Long t, Long l) {
		// Ensure the day exists
		Day day = f_day(d);
		if (day == null) {
			day = new Day(d);
			dayList.add(day);
		}
		
		// Ensure the session exists
		Session session = f_session(s);
		if (session == null) {
			session = new Session(s, day, t, l);
			sessionList.add(session);
		}
		else {
			session.update(day, t, l);
		}
		
		// Add a pointer to the session to the day
		day.addSession(session);
	}

	@Override
	public boolean e_at(String s, String d, Long t, Long l) {
		return f_session(s) != null ? true : false;
	}

	@Override
	public void a_enrolled(String s, String c, String l) {
		// Ensure the course exists
		Course course = f_course(c);
		if (course == null) {
			course = new Course(c);
			courseList.add(course);
		}
		
		// Ensure the lecture exists
		Lecture lecture = f_lecture(c, l);
		if (lecture == null) {
			lecture = new Lecture(course, l);
			lectureList.add(lecture);
		}
		
		// Add a pointer to the lecture to the course
		course.addLecture(lecture);
		
		// Ensure the student exists
		Student student = f_student(s);
		if (student == null) {
			student = new Student(s);
			studentList.add(student);
			student.addCourse(course, lecture);
		}
		else {
			// Add the specified course and lecture to the student
			if (student.checkForCourse(course, lecture) == false) {
				student.addCourse(course, lecture);
			}
		}
		
		// Increment the course's enrolled student count
		lecture.addStudent(student);
	}

	@Override
	public boolean e_enrolled(String s, String c, String l) {
		return e_student(s);
	}

	@Override
	public void a_enrolled(String s, Vector<Pair<ParamType, Object>> list) {
		for (int i = 0; i < list.size(); i=i+2) {
			Pair<ParamType,Object> coursePair = list.get(i);
			Pair<ParamType,Object> lecturePair = list.get(i+1);
			
			String course = coursePair.getValue().toString();
			String lecture = lecturePair.getValue().toString();
			
			a_enrolled(s,course,lecture);
		}
	}
	

	@Override
	public void a_capacity(String r, Long cap) {
		Room room = f_room(r);
		if (room == null) {
			room = new Room(r, cap);
			roomList.add(room);
		}
		else {
			room.update(cap);
		}
	}

	@Override
	public boolean e_capacity(String r, Long cap) {
		return e_room(r);
	}
	

	@Override
	public void a_assign(String c, String lec, String s) {
		// Ensure the course exists
		Course course = f_course(c);
		if (course == null) {
			course = new Course(c);
			courseList.add(course);
		}
		
		// Ensure the lecture exists
		Lecture lecture = f_lecture(c, lec);
		if (lecture == null) {
			lecture = new Lecture(course, lec);
			lectureList.add(lecture);
		}
		
		// Add a pointer to the lecture to the course
		course.addLecture(lecture);
		
		// Ensure the session exists
		Session session = f_session(s);
		if (session == null) {
			session = new Session(s);
			sessionList.add(session);
		}
		
		// Ensure the assignment exists
		Assign assign = f_assign(c, lec, s);
		if (assign == null) {
			assign = new Assign(lecture, session);
			fixedAssignmentMap.put(c+lec, assign);
		}
	}

	public Assign f_assign(String c, String lec, String s) {
		if (fixedAssignmentMap.containsKey(c+lec))
			return fixedAssignmentMap.get(c+lec);
		return null;
	}
	
	@Override
	public boolean e_assign(String c, String lec, String s) {
		return f_assign(c, lec, s) != null ? true : false;
	}
	
	//calls the fromFile() declared in PredicateReader.java
	public int fromFile(String fromFile){
		return super.fromFile(fromFile);
	}
	
	public static Environment get(){
		if(singletonEnv==null){
			singletonEnv = new Environment();
		}
		return singletonEnv;
	}
			
	
	public String getDetails() {
		
		String details = "";
		
		for (Student s : studentList) {
			details = details + s.toString() + "\n";
		}
		
		details = details + "\n";
		
		for (Instructor instructor : instructorList) {
			details = details + instructor.toString() + "\n";
		}
		
		details = details + "\n";
		
		for (Day day : dayList) {
			details = details + day.toString() + "\n";
		}
		
		details = details + "\n";
		
		for (Course course : courseList) {
			details = details + course.toString() + "\n";
		}
		
		details = details + "\n";
		
		for (Room room : roomList) {
			details = details + room.toString() + "\n";
		}
		
		details = details + "\n";
				
		for (Lecture lecture : lectureList) {
			details = details + lecture.toString() + "\n";
		}
		
		details = details + "\n";
		
		for (Student student : studentList) {
			details = details + student.getEnrolledPredicates() + "\n";
		}
		
		details = details + "\n";
		
		for (Session session : sessionList) {
			details = details + session.toString() + "\n";
		}
		
		details = details + "\n";
		
		for (Room room : roomList) {
			details = details + room.getCapacityPredicate() + "\n";
		}
		
		details = details + "\n";
		
		for (Instructor instructor : instructorList) {
			details = details + instructor.getInstructsPredicates();
		}
		
		details = details + "\n";
		
		for (Lecture lecture : lectureList) {
			details = details + lecture.getExamLengthPredicate() + "\n";
		}
		
		details = details + "\n";
		
		for (Session session : sessionList) {
			details = details + session.getAtPredicate() + "\n";
		}
		
		return details;
	}	

}
