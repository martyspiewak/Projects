solve :- 
    abolish(capacity/2),
    abolish(initial/1),
    abolish(goal/1),
    write('Please enter 6 integers, 1 at a time. \nThe first 3 will be the capacities, the second 3 will be the final state.'), nl,
    read(B1),
    read(B2),
    read(B3),
    read(F1),
    read(F2),
    read(F3),
    setCapacities(B1, B2, B3),
    asserta(goal(state(F1, F2, F3))),
    Max is max(B1, B2),
    Max2 is max(Max, B3),
    setInitials(Max2, B1, B2, B3),
    initial(Start),
    breadthfirst( [ [Start] ], Solution),
    % Solution is a path (in reverse order) from initial to a goal
    printsol(Solution).

setCapacities(B1, B2, B3) :-
  asserta(capacity(buck1, B1)),
  asserta(capacity(buck2, B2)),
  asserta(capacity(buck3, B3)).

setInitials(Max, B1, B2, B3) :-
  Max =:= B1, asserta(initial(state(Max, 0, 0)));
  Max =:= B2, asserta(initial(state(0, Max, 0)));
  Max =:= B3, asserta(initial(state(0, 0, Max))).

goalpath([Node | _]) :- goal(Node).

%move from 1 -> 2
move( state( A1, B1, C1), state( A2, B2, C1) ) :- 
  (capacity(buck2, X), A1 + B1 =< X, B2 is B1 + A1, A2 is 0) ;
  (capacity(buck2, X), Y is (X - B1), Z is (A1 - Y), Z >= 0, A2 is Z, B2 is (B1 + Y)).

%move from 1 -> 3
move( state( A1, B1, C1), state( A2, B1, C2) ) :- 
  (capacity(buck3, X), A1 + C1 =< X, C2 is C1 + A1, A2 is 0) ;
  (capacity(buck3, X), Y is (X - C1), Z is (A1 - Y), Z >= 0, A2 is Z, C2 is (C1 + Y)).

%move from 2 -> 1
move( state( A1, B1, C1), state( A2, B2, C1) ) :- 
  (capacity(buck1, X), A1 + B1 =< X, A2 is B1 + A1, B2 is 0) ;
  (capacity(buck1, X), Y is (X - A1), Z is (B1 - Y), Z >= 0, B2 is Z, A2 is (A1 + Y)).

%move from 2 -> 3
move( state( A1, B1, C1), state( A1, B2, C2) ) :- 
  (capacity(buck3, X), B1 + C1 =< X, C2 is B1 + C1, B2 is 0) ;
  (capacity(buck3, X), Y is (X - C1), Z is (B1 - Y), Z >= 0, B2 is Z, C2 is (C1 + Y)).
   
%move from 3 -> 1
move( state( A1, B1, C1), state( A2, B1, C2) ) :- 
  (capacity(buck1, X), A1 + C1 =< X, A2 is A1 + C1, C2 is 0) ;
  (capacity(buck1, X), Y is (X - A1), Z is (C1 - Y), Z >= 0, C2 is Z, A2 is (A1 + Y)).

%move from 3 -> 2
move( state( A1, B1, C1), state( A1, B2, C2) ) :- 
  (capacity(buck2, X), B1 + C1 =< X, B2 is B1 + C1, C2 is 0) ;
  (capacity(buck2, X), Y is (X - B1), Z is (C1 - Y), Z >= 0, C2 is Z, B2 is (B1 + Y)).


printsol([X]) :- write(X), write(': initial state'), nl.
printsol([X,Y|Z]) :- printsol([Y | Z]), write(X), nl.

breadthfirst( [ Path | _], Path)  :-
    goalpath( Path ).  % if Path is a goal-path, then it is a solution.

breadthfirst( [Path | Paths], Solution)  :-
  extend( Path, NewPaths),
  append( Paths, NewPaths, Paths1),
  breadthfirst( Paths1, Solution).

% setof(X, Condition, Set) is a built-in function: it collects all X satisfying Condition into Set.
extend( [Node | Path], NewPaths)  :-
  setof( [NewNode, Node | Path],
         ( move( Node, NewNode), not(member( NewNode, [Node | Path] )) ),
         NewPaths),
  !.

extend( _, [] ).

not(P) :- P, !, fail.
not(_).