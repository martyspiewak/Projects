remove_dups([], []).

remove_dups(L1, L2) :-
	qsort(L1, L),
	remove_dups1(L, L2).

remove_dups1([H | []], [H | []]).

remove_dups1([H | [H1 | T]], L) :- 
	H =:= H1,
	remove_dups1([H1 | T], L).

remove_dups1([H | [H1 | T]], [H | T1]) :-
	H =\= H1,
	remove_dups1([H1 | T], T1).

qsort([],[]).

qsort([H|T],R):-partition(T,H,R1,R2),
		qsort(R1,R11),
		qsort(R2,R22),
		append(R11,[H|R22],R).

partition([],_,[],[]).
partition([H1|T],H,[H1|T1],T2):-H1<H,!,partition(T,H,T1,T2).
partition([H1|T],H,T1,[H1|T2]):-        partition(T,H,T1,T2).