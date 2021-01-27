remdups([], []).

remdups([H | T], L) :-
	member(H, T),
	remdups(T, L).

remdups([H | T], [H | L]) :-
	\+member(H, T),
	remdups(T, L).

member1(X, L) :-
	remdups(L, Y),
	member(X, Y).

member2(H, [H | T]) :-
	\+member(H, T).

member2(X, [H | T]) :-
	member2(X, T).

mirror([], []).

union1([], L2, L2).

union1([H1 | T1], L2, [H1 | Return]) :-
	\+member(H1, L2), union1(T1, L2, Return).

union1([H1 | T1], L2, Return) :-
	member(H1, L2), union1(T1, L2, Return).

flatten([],[]).
flatten([H | T], [H | Rest]) :- 
	atom(H), flatten(T, Rest).
flatten([H | T], Sol) :- 
	is_list(H), flatten(H, H1), flatten(T, Rest), append(H1, Rest, Sol).

mid([A, B], A) :- !.

mid([_ | T], X) :-
	mid(T, X).

emptyIntersection(A, B) :- intersection(A , B, []).

intersection([], B, []).

intersection([H | T], B, [H | C]) :-
	member(H, B), intersection(T, B, C).

intersection([H | T], B, C) :-
	\+member(H, B), intersection(T, B, C).


testRemDups([], []).
testRemDups([H | []], [H | []]).
testRemDups([H | [H1 | T]], L) :-
	H =:= H1, testRemDups(T, L).
testRemDups([H | [H1 | T]], [L, H | []]) :-
	H =\= H1, testRemDups(T, L).



