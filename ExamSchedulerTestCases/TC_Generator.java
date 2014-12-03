// The beginnings of a program to generate test cases for the exam-scheduler search problem

import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileWriter;

public class TC_Generator {

    public static void main(String[] args) throws IOException {

	int studentNum = 0;
	final int FIRST_NUM = 1;
	final int LAST_NUM = 1050;
	String student_id = "";
	String course = "";

	PrintWriter outs = new PrintWriter(new FileWriter("TC_Generator_Output.txt"));
	
	outs.println("\n\n\n");

	for (studentNum = FIRST_NUM; studentNum < LAST_NUM + 1; studentNum++) {
	    if (studentNum < 10 ) student_id = "S0000000" + studentNum;
	    else if (studentNum < 100) student_id = "S000000" + studentNum;
	    else if (studentNum < 1000) student_id = "S00000" + studentNum;
	    else if (studentNum < 10000) student_id = "S0000" + studentNum;

	    if ((951 <= studentNum) && (studentNum <= 1050)) course = "ART200";
	    else if ((856 <= studentNum) && (studentNum <= 950)) course = "CPSC200";
	    else if ((766 <= studentNum) && (studentNum <= 855)) course = "ENGL200";
	    else if ((681 <= studentNum) && (studentNum <= 765)) course = "LAW200";
	    else if ((601 <= studentNum) && (studentNum <= 680)) course = "MATH200";
	    else if ((526 <= studentNum) && (studentNum <= 600)) course = "ART300";
	    else if ((456 <= studentNum) && (studentNum <= 525)) course = "CPSC300";
	    else if ((391 <= studentNum) && (studentNum <= 455)) course = "ENGL300";
	    else if ((331 <= studentNum) && (studentNum <= 390)) course = "LAW300";
	    else if ((276 <= studentNum) && (studentNum <= 330)) course = "MATH300";
	    else if ((226 <= studentNum) && (studentNum <= 275)) course = "ART400";
	    else if ((181 <= studentNum) && (studentNum <= 225)) course = "CPSC400";
	    else if ((141 <= studentNum) && (studentNum <= 180)) course = "ENGL400";
	    else if ((106 <= studentNum) && (studentNum <= 140)) course = "LAW400";
	    else if ((76 <= studentNum) && (studentNum <= 105)) course = "MATH400";
	    else if ((51 <= studentNum) && (studentNum <= 75)) course = "ART500";
	    else if ((31 <= studentNum) && (studentNum <= 50)) course = "CPSC500";
	    else if ((16 <= studentNum) && (studentNum <= 30)) course = "ENGL500";
	    else if ((6 <= studentNum) && (studentNum <= 15)) course = "LAW500";
	    else if ((1 <= studentNum) && (studentNum <= 5)) course = "MATH500";

	    outs.println("student(" + student_id + ")");
	    outs.println("enrolled(" + student_id + "," + course + "," + "L01)\n");	    
	}
	outs.println("\n\n");
	outs.close();

    }
}

