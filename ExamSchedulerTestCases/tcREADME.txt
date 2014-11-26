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
This case should give a solution, but will have at least one violation of soft constraint 3

***  tc06.txt  ***
This case should give a solution, but will have at least one violation of soft constraint 2

***  tc06.txt  ***
This case should give a solution, but will have at least one violation of soft constraint 5
