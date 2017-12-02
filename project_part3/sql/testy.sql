-- qeurey # 1
-- Add a new passenger into the database
--insert into passenger (pID,passNum,fullName,bdate,country)
--values (251, 'ABCDEFGHIJ','That Guy','2017-11-27','USA');

-- qeurey # 2 ??
-- Book a flight for an existing passenger
--insert into booking (bookRef,departure,flightNum,pID)
--values ('','','',);

-- query # 5
--select f.flightNum, f.origin, f.destination, f.plane, f.duration
--from flights f
--where f.origin = '' and f.destination = ''

-- querey 9



-- Query 1
--INSERT INTO passenger(pid, passnum, fullname, bdate, country)
--VALUES(252, '252', 'acast050', '1996-07-01', 'USA');

--Query 2
--INSERT INTO booking(bookref, departure, flightnum, pid)
--VALUES('s' , '1996-07-01', 'KLM711', 0);

/*
--Query 7
SELECT a.name, f.flightnum, f.origin, f.destination, f.plane, AVG(r.score) AS avg_score
FROM airline a, flight f, ratings r
WHERE a.airid = f.airid
GROUP BY a.name, f.flightnum
ORDER BY avg_score
LIMIT 1000
;
*/

--https://www.google.com/search?q=sql+must+appear+in+the+group+by+clause&oq=sql+must+appear+&aqs=chrome.0.0j69i57j0l2.2711j0j7&sourceid=chrome&ie=UTF-8

--Query 8
--SELECT a.name, f.flightnum, f.origin, f.destination, f.plane, f.duration
--FROM airline a, flight f
--WHERE a.airid = f.airid AND f.origin = 'Moscow' AND f.destination = 'Sydney'
--ORDER BY f.duration
--LIMIT 1;

/*
-- q # 6
-- list of the k-most popular destinations depending on the number of flights offered to that specific destination number of flights offered to that specific destination.
SELECT f.destination, COUNT(f.destination)AS numbah
FROM flight f
GROUP BY f.destination
ORDER BY numbah DESC
LIMIT 5
;
*/


