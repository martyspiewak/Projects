remove_dups([], []).

remove_dups(L1, L) :-
	qsort(L1, L2),
	remove_dups1(L2, L).

remove_dups1([First | Rest], NewRest) :- 
	member(First, Rest),
	remove_dups1(Rest, NewRest1).

remove_dups1([First | Rest], [First | NewRest]) :- 
	\+(member(First, Rest)),
	remove_dups1(Rest, NewRest). 

qsort([],[]).

qsort([H|T],R):-partition(T,H,R1,R2),
		qsort(R1,R11),
		qsort(R2,R22),
		append(R11,[H|R22],R).

partition([],_,[],[]).
partition([H1|T],H,[H1|T1],T2):-H1<H,!,partition(T,H,T1,T2).
partition([H1|T],H,T1,[H1|T2]):-        partition(T,H,T1,T2).