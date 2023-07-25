colours:-([red,blue,yellow,black,green]).

check:-
    write("Enter Colour:"),
    read(X),
    memberchk(X,[red,blue,yellow,black,green]).


add:-append([pink],[red,blue,yellow,black,green],NewList),
write(NewList).

lengthcheck:- length([pink,red,blue,yellow,black,green],X),
    write("length is:"),
    write(X).

oderz:-sort(0,@>,[pink,red,blue,yellow,black,green],NewList),
    write(NewList).

odera:-sort(0,@<,[pink,red,blue,yellow,black,green],NewList),
    write(NewList).


student(name(["Saman","Perera"]),11021).
student(name(["Mohamed","Humaith"]),11022).
student(name(["Nimal","Silva"]),11023).
student(name(["Viji","Kumara"]),11024).
student(name(["Lasantha","Jayamanna"]),11025).
student(name(["Nimal","Silva"]),11026).
student(name(["Ponnambalam","Ramanadan"]),11027).

marks(11021,score([50,71,57,65,61])).
marks(11022,score([65,50,85,49,82])).
marks(11023,score([100,85,100,89,76])).
marks(11024,score([73,65,59,82,66])).
marks(11025,score([60,90,78,96,100])).



checkname:-student(name("Janaka","Silva"),_).


registered(X,Y,Z):-student(name([X,Y]),Z).


userinput(Fname,Lname):-student(name([Fname,Lname]),_)->
                 write("Registered");
                 write("Not Registered").

indexcheck(Lname):-student(name([Fname,Lname]),Index),
               write(Fname),
               write(' '),
               write(Lname),nl,
               write(Index).

satexam:-student(name([Fname,Lname]),Index),
               marks(Index,score(_)),
               write(Fname),write(" "),
               write(Lname),write(" "),
               write(Index).

missexam:-student(name([Fname,Lname]),Index),
              not(marks(Index,score(_))),
              write(Fname),write(" "),
              write(Lname),write(" "),
              write(Index).





