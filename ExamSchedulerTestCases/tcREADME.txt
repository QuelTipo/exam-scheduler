Notes re. exam-scheduler tcXX.txt files ( where XX = 00 to NN)

***  tc01.txt ***
A basic scenario, 20 students all taking the same four courses.  Only one
exam room is used for each of four sessions.  Output for this case is of
the form:
assign(MATH211,L01,SESSION)
assign(MATH265,L01,SESSION)
assign(CPSC231,L01,SESSION)
assign(PHIL279,L01,SESSION)
The following SESSION names should appear once in the above set of assign
predicates (any permutation is acceptable).
M10-2-MS217
T10-2-MS217
W10-2-MS217
W14-2-MS217


***  tc02.txt  ***
Expected output:  No Solution
This tc is designed to violate hard constraint 1 i.e.
H1:Every lecture is assigned an exam session.
This test case has four lectures but only three available sessions, thus the program
should detect a violation of H1.


***  tc03.txt  ***
Expected o/p is No Solution.
Tests H1 and H3.
Test case has 26 students, each taking the same 4 courses.  Two rooms and four
sessions used.  Three of four sessions use a room with sufficient capacity for
26 students.  The room specified for the fourth session only has a capacity of 25.

***  tc04.txt  ***
Expected o/p is no solution.
Tests H1 and H4.
26 students all taking the same four courses.  The examLength for the course MATH265
is set at 3, but the session length is 2 for each of the four available sessions.

***  tc05.txt  ***
This case should give a solution [penalty of 0 per Jeff]

***  tc06.txt  ***
This case should give a solution [penalty of 0 per Jeff]

***  tc07.txt  ***
This case should give a solution, but will have at least one violation of soft constraint 5

***  tc08.txt  ***
This case should give a solution, but will have at least one violation of soft constraint 6 and 7

***  tc09.txt  ***
Expected o/p is an assignment that generates a total penalty of 300.
Test designed to violate soft constraint 1 i.e. no student writes more than
one exam in a timeslot.
tc01 was modified by adding a 5th course that is only taken by 3 of 20 students.
The expectation is that course ASTR207 taken by three students will be assigned
to one of the two overlapping sessions W14-2-MS217 and W14-2-MS205 to generate a
minimal penalty.

***  tc10.txt  ***
Solution expected without penalty.  2 pairs of overlapping sessions.
For each overlapping session, a MATH course should be in assigned to
one session and either CPSC or PHIL to the other.

***  tc11.txt  ***
Solution expected with Penalty of 40.
Changed tc10 so now only 1 instructor for all four courses.
There are 2 pairs of overlapping sessions.


***  tc12.txt  ***
Solution expected without penalty.
Re. S4 - Students are writing for 5 hours in two W sessions.

***  tc13.txt  ***
Solution expected with penalty of 1000.
Test case for S4.  20 students writing for 6 hours on a single day.

***  tc14.txt  ***
Solution expected with penalty of 2000. (Modified tc13)
Test case for S4 and S5.  20 students writing for 6 hours on a 
single day without a break between the two exams.

***  tc15.txt  ***
Solution expected without penalties.
4 courses, 4 sessions.
One fixed assignment.   assign(MATH211,L01,T10-2-ST057)

***  tc16.txt  ***
Solution expected without penalties.
4 courses, 4 sessions.  Two fixed assignments.  
assign(MATH211,L01,T10-2-ST057)    assign(PHIL279,L01,W10-2-MS205)

***  tc17.txt  ***
Solution expected without penalties.
4 courses, 4 sessions.  3 of 4 courses are fixed assignments.

***  tc18.txt  ***
Solution expected without penalties.
4 courses, 4 sessions.  Fixed assignments specified for all four courses.
assign(MATH211,L01,T10-2-ST057)     assign(CPSC231,L01,T10-2-MS205)
assign(MATH265,L01,W10-2-ST057)     assign(PHIL279,L01,W10-2-MS205)

***  tc19.txt  ***
Solution expected without penalty.
40 students taking the same four courses.  20 students in L01 and the other 20
in L02 for each course.  Test is to ensure that different lectures are
assigned to the same exam session (a single room with adequate space is used for
each of the four available sessions).

***  tc20.txt  ***
Solution expected without penalty.
Similar to tc19 except some sessions with smaller rooms are available.  The
expectation is that different lectures of the same course have the same time
slot (but not necessarily the same room).

***  tc21.txt  ***
Solution expected with penalty of 50.
tc is for soft constraints 6 and 7.
Two sessions.  One of two 3 hr MATH exams expected in each session.  One of CPSC and PHIL
also assigned to each of the two sessions.  S6 penalty of 20 for each session
expected because all exams are not the same duration.  S7 penalty of 5 expected
for each session because there's an exam that does not take up the full time of the
session.

***  tc22bigV1.txt  *** 
20 courses, 20 rooms, 20 sessions, 1050 students.  Each student is enrolled in a single course.
The one and only solution needs to match:
20 lectures with 5, 10, 15, ..., 95, 100 students and
20 sessions with 5, 10, 15, ..., 95, 100 seats available capacity in the session.
Sessions designed so that result has the following form:
200, 300, 400, 500 level courses have exam times of 8, 11, 14, 17, respectively.
ART, CPSC, ENGL, LAW, MATH exam days are M, T, W, Th, F, respectively.

***  tc23bigV2.txt  *** 
Solution should be possible without penalty.
There seems to be an issue generating a solution for the previous test case,
tc22bigV1.txt.  This new version uses only a single room with a capacity of
100 for all 20 sessions.

***  tc24bigV3.txt   ***
add description later


***  tc25.txt  ***
Solution without penalty expected.
This test case is the same as tc01.txt, except that the test is to ensure the
program can handle a missing instructor predicate, instructor(I00000002), and
a missing course predicate, course(MATH211).
