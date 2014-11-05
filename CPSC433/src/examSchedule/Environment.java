package examSchedule;

import java.util.TreeSet;
import java.util.Vector;

import examSchedule.parser.*;
import examSchedule.parser.Predicate.ParamType;

public class Environment extends PredicateReader implements ExamSchedulePredicates, EnvironmentInterface {

	private static EnvironmentInterface singletonEnv;
	
	private TreeSet<Student> studentList = new TreeSet<Student>();
	private TreeSet<Instructor> instructorList = new TreeSet<Instructor>();
	private TreeSet<Course> courseList = new TreeSet<Course>();
	private TreeSet<Lecture> lectureList = new TreeSet<Lecture>();
	private TreeSet<Session> sessionList = new TreeSet<Session>();
	private TreeSet<Day> dayList = new TreeSet<Day>();
	private TreeSet<Room> roomList = new TreeSet<Room>();
	
	public Environment(String string) {
		super(string);
		// TODO Auto-generated constructor stub
	}
	
	public Environment() {

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
		
		Student s = new Student(p);
		int alreadyIn = findStudent(s);
		if (alreadyIn > 0) {
			studentList.add(s);
		}
		
		
	}

	@Override
	public boolean e_student(String p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void a_instructor(String p) {
		
		Instructor instructor = new Instructor(p);
		int alreadyIn = findInstructor(instructor);
		if (alreadyIn > 0) {
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
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
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

	// Ensure the session is there
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
		
		// ...
		Day day = f_day(d);
		if (day == null) {
			day = new Day(d);
			dayList.add(day);
		}
		
		// Ensure the session is there
		Session session = f_session(s);
		if (session == null) {
			session = new Session(s);
			sessionList.add(session);
		}
		else {
			if (session.getRoom() == null) {
				session.update(room, day, t, l);
			}
		}
		
		
	}

	@Override
	public boolean e_session(String session, String room, String day, Long time, Long length) {
		return e_session(session);
	}

	
	@Override
	public void a_day(String p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean e_day(String p) {
		// TODO Auto-generated method stub
		return false;
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
	}

	public Lecture f_lecture(String c, String lec) {
		for (Lecture lecture : lectureList) {
			if (lecture.getName().equals(c+lec)) {
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
			lecture = new Lecture(course, lec);
			lectureList.add(lecture);
		}
		// So at this point there is a lecture, which we may have to overwrite
		else {
			if ((lecture.getInstructor() == null) && (lecture.getLength() == 0)) {
				lecture.update(instructor, length);
			}
		}
	}

	@Override
	public void a_instructs(String p, String c, String l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean e_instructs(String p, String c, String l) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void a_examLength(String c, String lec, Long hours) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean e_examLength(String c, String lec, Long hours) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void a_roomAssign(String p, String room) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public boolean e_roomAssign(String p, String room) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void a_dayAssign(String p, String day) {
		// TODO Auto-generated method stub
		
	}

	public Day f_day(String d) {
		for (Day day : dayList) {
			if (day.getName().equals(d)) {
				return day;
			}
		}
		return null;
	}
	
	@Override
	public boolean e_dayAssign(String p, String day) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void a_time(String p, Long time) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean e_time(String p, Long time) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void a_length(String p, Long length) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean e_length(String p, Long length) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void a_at(String session, String day, Long time, Long length) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean e_at(String session, String day, Long time, Long length) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void a_enrolled(String student, String c, String l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean e_enrolled(String student, String c, String l) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void a_enrolled(String student, Vector<Pair<ParamType, Object>> list) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void a_capacity(String r, Long cap) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean e_capacity(String r, Long cap) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void a_assign(String c, String lec, String session) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean e_assign(String c, String lec, String session) {
		// TODO Auto-generated method stub
		return false;
	}
	
	//calls the fromFile() declared in PredicateReader.java
	public int fromFile(String fromFile){
		return super.fromFile(fromFile);
	}
	
	public static EnvironmentInterface get(){
		if(singletonEnv==null){
			singletonEnv = new Environment();
		}
		return singletonEnv;
	}
	
	private int findStudent(Student s) {
		
		for (Student nextStudent : studentList) {
			if (nextStudent.equals(s)) {
				return 0;
			}
		}
		
		return 1;
	}
	
	private int findInstructor(Instructor instructor) {
		
		for (Instructor nextInstructor : instructorList) {
			if (nextInstructor.equals(instructor)) {
				return 0;
			}
		}
		return 1;
	}
	
	public void printDetails() {
		
		for (Student s : studentList) {
			System.out.println(s.toString());
		}
		
		System.out.println();
		
		for (Instructor instructor : instructorList) {
			System.out.println(instructor.toString());
		}
	}

}
