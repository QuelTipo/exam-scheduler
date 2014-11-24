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
This tc is designed to violate hard constraint 1 i.e.
H1:Every lecture is assigned an exam session.
Expected output:  No Solution
This test case has four lectures but only three available sessions, thus the program
should detect a violation of H1.


