/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner; // read in string inputs for queries
import java.util.Random; // generate random nums

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class AirBooking
{
	// reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	static Scanner str_get = new Scanner(System.in); // create a scanner to get stuff for queries
	
	public AirBooking(String dbname, String dbport, String user, String passwd) throws SQLException 
	{
		System.out.print("Connecting to database...");
		try
		{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}
		catch(Exception e)
		{
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate(String sql) throws SQLException 
	{ 
		// creates a statement object
		Statement stmt = this._connection.createStatement();

		// issues the update instruction
		stmt.executeUpdate(sql);

		// close the instruction
	    stmt.close();
	}// end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult(String query) throws SQLException 
	{
		// creates a statement object
		Statement stmt = this._connection.createStatement();

		// issues the query instruction
		ResultSet rs = stmt.executeQuery(query);

		/*
		 *  obtains the metadata object for the returned result set.  
		 *  The metadata contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData();
		int numCol = rsmd.getColumnCount();
		int rowCount = 0;
		
		// iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while(rs.next())
		{
			if(outputHeader)
			{
				for(int i = 1; i <= numCol; i++)
				{
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for(int i=1; i<=numCol; ++i)
			{
				System.out.print(rs.getString(i) + "\t");
			}
			System.out.println();
			++rowCount;
		}// end while
		stmt.close();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult(String query) throws SQLException 
	{ 
		// creates a statement object 
		Statement stmt = this._connection.createStatement(); 
		
		// issues the query instruction 
		ResultSet rs = stmt.executeQuery(query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  
		 * The metadata contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData(); 
		int numCol = rsmd.getColumnCount(); 
		int rowCount = 0; 
	 
		// iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while(rs.next())
		{
			List<String> record = new ArrayList<String>(); 
			for(int i=1; i<=numCol; ++i)
			{ 
				record.add(rs.getString(i));
			} 
			result.add(record); 
		}// end while 
		stmt.close(); 
		return result; 
	}// end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery(String query) throws SQLException 
	{
		// creates a statement object
		Statement stmt = this._connection.createStatement();

		// issues the query instruction
		ResultSet rs = stmt.executeQuery(query);

		int rowCount = 0;

		// iterates through the result set and count nuber of results.
		if(rs.next())
		{
			rowCount++;
		}// end while
		stmt.close();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int getCurrSeqVal(String sequence) throws SQLException 
	{
		Statement stmt = this._connection.createStatement();
		
		ResultSet rs = stmt.executeQuery(String.format("Select currval('%s')", sequence));
		if(rs.next()) 
		{
			return rs.getInt(1);
		}
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup()
	{
		try
		{
			if(this._connection != null)
			{
				this._connection.close();
				str_get.close(); // close the scanner that we made for queries
			}// end if
		}
		catch(SQLException e)
		{
	         // ignored.
		}// end try
	}// end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	 
	public static void main(String[] args) 
	{
		if(args.length != 3) 
		{
			System.err.println(
				"Usage: " + "java [-classpath <classpath>] " + 
				AirBooking.class.getName() +
		        " <dbname> <port> <user>");
			return;
		}//end if
		
		AirBooking esql = null;
		
		try
		{
			try 
			{
				Class.forName("org.postgresql.Driver");
			}
			catch(Exception e)
			{
				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new AirBooking(dbname, dbport, user, "");
			
			boolean keepon = true;
			while(keepon)
			{
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add Passenger");
				System.out.println("2. Book Flight");
				System.out.println("3. Review Flight");
				System.out.println("4. Insert or Update Flight");
				System.out.println("5. List Flights From Origin to Destination");
				System.out.println("6. List Most Popular Destinations");
				System.out.println("7. List Highest Rated Destinations");
				System.out.println("8. List Flights to Destination in order of Duration");
				System.out.println("9. Find Number of Available Seats on a given Flight");
				System.out.println("10. < EXIT");
				
				switch(readChoice())
				{
					case 1:  AddPassenger(esql); break;
					case 2:  BookFlight(esql); break;
					case 3:  TakeCustomerReview(esql); break;
					case 4:  InsertOrUpdateRouteForAirline(esql); break;
					case 5:  ListAvailableFlightsBetweenOriginAndDestination(esql); break;
					case 6:  ListMostPopularDestinations(esql); break;
					case 7:  ListHighestRatedRoutes(esql); break;
					case 8:  ListFlightFromOriginToDestinationInOrderOfDuration(esql); break;
					case 9:  FindNumberOfAvailableSeatsForFlight(esql); break;
					case 10: keepon = false; break;
				}
			}
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}
		finally
		{
			try
			{
				if(esql != null) 
				{
					System.out.print("Disconnecting from database...");
					esql.cleanup();
					System.out.println("Done\n\nBye !");
				}// end if				
			}
			catch(Exception e)
			{
				// ignored.
			}
		}
	}

	public static int readChoice() 
	{
		int input;
		// returns only if a correct value is given.
		do{
			System.out.print("Please make your choice: ");
			try  // read the integer, parse it and break.
			{
				input = Integer.parseInt(in.readLine());
				break;
			}
			catch(Exception e) 
			{
				System.out.println("Your input is invalid!");
				continue;
			}// end try
		}while(true);
		return input;
	}// end readChoice
	
	//-----------START of our own helper functions--------------
	public boolean isNumeric(String s) {
		return s != null && s.matches("[-+]?\\d*\\.?\\d+");  
	}  // function from: https://stackoverflow.com/questions/14206768/how-to-check-if-a-string-is-numeric
	
	public boolean isLeapYear(int year) {
	  if (year % 4 != 0) {
		return false;
	  } else if (year % 400 == 0) {
		return true;
	  } else if (year % 100 == 0) {
		return false;
	  } else {
		return true;
	  }
	}//function from: https://stackoverflow.com/questions/1021324/java-code-for-calculating-leap-year/1021373#1021373
	
	public boolean pidIsValid(String pid){
		if(!isNumeric(pid)){
			System.out.println("Invalid pid");
			return false;
		}
		String trashql = "SELECT pid FROM passenger WHERE pid = " + pid;
		try
		{
			List<List<String>> returnval;
			returnval = executeQueryAndReturnResult(trashql);
			if(returnval.size() == 0)
			{
				System.out.println("Invalid pid");
				return false;
			}
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}
		
		return true;
	}

	public boolean DateIsValid(String date)
	{
		try
		{
			String[] box = date.split("-");
			if( box == null || box.length != 3 ){return false;}
			int x1 = Integer.parseInt(box[0]); // year
			int x2 = Integer.parseInt(box[1]); // month
			int x3 = Integer.parseInt(box[2]); // day
			
			if(x2 >12 || x2 < 1){return false;}
			
			if(x2 == 1 || x2 == 3 || x2 == 5 || x2 == 7 || x2 == 8 || x2 == 10 || x2 == 12)
			{
				if(x3 >31 || x3 < 1){return false;}
			}
			else if(x2 == 9 || x2 == 4 || x2 == 6 || x2 == 11){
				if(x3 > 30 || x3 < 1)
				{
					return false;
				}
			}
			else{
				if(isLeapYear(x1)){					
					if(x3 > 29 || x3 < 1)
					{
						return false;
					} 
				}
				else{				
					if(x3 > 28 || x3 < 1)
					{
						return false;
					} 
				}
					
				}
		}
		catch(Exception e)
		{
			return false;
		}
		return true;
	}
	
	public boolean scoreIsValid(String score){
		if(!isNumeric(score)){
			return false;
		}
		int score2 = Integer.parseInt(score);
		if(score2 < 0 || score2 > 5)
		{
			return false;
		}
		return true;
	}
	
	public boolean flightNumIsValid(String flightNum){
		String trashql = "SELECT * FROM flight f WHERE f.flightNum = '" + flightNum + "';";
		try
		{
			List<List<String>> returnval;
			returnval = executeQueryAndReturnResult(trashql);
			if(returnval.size() == 0)
			{
				return false;
			}
		}
		catch(Exception e)
		{
			return false;
		}
		return true;
	}
	
	public boolean isPassNumUnique(String passnum)
	{
		if(passnum.length() != 10){return false;}
		
		String trashql = "SELECT * FROM passenger p WHERE p.passnum = '" + passnum + "';";
		try
		{
			List<List<String>> returnval;
			returnval = executeQueryAndReturnResult(trashql);
			if(returnval.size() != 0)
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	public String getPid(String table)
	{	
		String trashql = "select max(pid) from " + table +";";
		String pid = "";
		try
		{
			List<List<String>> r2 = executeQueryAndReturnResult(trashql);
			pid = r2.get(0).get(0);
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}
		return Integer.toString(Integer.parseInt(pid) + 1);
	}
	
	public String getRid()
	{
		String trashql = "select max(rid) from ratings;";
		String pid = "";
		try
		{
			List<List<String>> r2 = executeQueryAndReturnResult(trashql);
			pid = r2.get(0).get(0);
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}
		return Integer.toString(Integer.parseInt(pid) + 1);
	}
	
	public String getBookRef()
	{
		String bookref = "";
		String randBookRef = "";
		
		do
		{
			randBookRef = "";
			Random rand = new Random();
			
			for(int i = 0; i < 10; i ++)
			{
				int n = rand.nextInt(25) + 65;
				char a = (char) n;
				if(n % 2 == 0)
				{
					n = rand.nextInt(9) + 48;
					a = (char) n;
				}
				randBookRef += a;
			}
			//System.out.println(randBookRef);
			
			String trashql = "SELECT bookref FROM booking WHERE bookref = '" + randBookRef + "';";
			try
			{
				List<List<String>> r2 = executeQueryAndReturnResult(trashql);
				bookref = r2.get(0).get(0);
			}
			catch(Exception e)
			{
				//System.out.println(randBookRef);
				return randBookRef;
			}
			//System.out.println("Randomly generated one exists!");
		}while(bookref.length() != 0);
			
		
		return randBookRef;
	}
	
	public boolean doesPassNumExist(String passNum)
	{
		if(passNum.length() != 10){return false;}
		
		String trashql = "SELECT * FROM passenger p WHERE p.passnum = '" + passNum + "';";
		try
		{
			List<List<String>> returnval;
			returnval = executeQueryAndReturnResult(trashql);
			if(returnval.size() != 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
			return false;
		}
	}
	
	public String getPidFromPassNum(String passport_number)
	{
		String trashql = "SELECT pid FROM passenger WHERE passNum = '" + passport_number + "';";
		String pid = "-1";
		try
		{
			List<List<String>> r2 = executeQueryAndReturnResult(trashql);
			pid = r2.get(0).get(0);
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}
		return pid;
	}
	
//------------------------------------------------------------------------------
	
	// 1.) Add a new passenger to the database
	public static void AddPassenger(AirBooking esql)
	{
		/* Passenger table */
		// pID INTEGER NOT NULL,
		// passNum CHAR(10) NOT NULL,
		// fullName CHAR(24) NOT NULL,
		// bdate DATE NOT NULL,
		// country CHAR(24) NOT NULL,
		
		String p_name = "";
		String p_dob = "";
		String p_country = "";
		String p_pass = "";
		String p_id = "";
		
		try
		{
			System.out.println("Enter Passenger's Full Name");
			p_name = str_get.nextLine();
			while(p_name.length() > 24 || p_name.length() == 0)
			{ 
				if(p_name.length() > 24)
				{
					System.out.println("Your Passenger name was more than 24 characters long. Try Again.");
					p_name = str_get.nextLine();
				}
				else
				{
					System.out.println("Your Passenger name was empty. Try Again.");
					p_name = str_get.nextLine();
				}
			}

			// NEED TO CHECK
			System.out.println("Enter Date of Birth (format: YYYY-MM-DD)");
			p_dob = str_get.nextLine();
			while(!esql.DateIsValid(p_dob)){
				System.out.println("Invalid date, please try again.");
				System.out.println("Enter Date of Birth (format: YYYY-MM-DD)");
				p_dob = str_get.nextLine();				
			}
			
			System.out.println("Enter Country");
			p_country = str_get.nextLine();
			while(p_country.length() > 24 || p_country.length() == 0)
			{ 
				if(p_country.length() > 24)
				{
					System.out.println("Your country name was more than 24 characters long. Try Again.");
					p_country = str_get.nextLine();
				}
				else
				{
					System.out.println("Your country name was empty. Try Again.");
					p_country = str_get.nextLine();
				}
			}
			
			System.out.println("Enter Passport number");
			p_pass = str_get.nextLine();
			while(!esql.isPassNumUnique(p_pass) || p_pass.length() != 10)
			{
				if(p_pass.length() != 10 )
				{ 
					System.out.println("Your Passport number was not the right length.");
					System.out.println("Enter Passport number");
					p_pass = str_get.nextLine();
				}
				else
				{
					System.out.println("Invalid Passport number");
					System.out.println("Enter Passport number");
					p_pass = str_get.nextLine();
				}
			}
			
			// this should be done by system automatically
			p_id = esql.getPid("passenger");
			//System.out.println(p_id);
			
			String trashql = "insert into passenger (pID,passNum,fullName,bdate,country)" +
			"values (" + 
			"'" + p_id + "'" + "," +
			"'" + p_pass + "'" + "," +
			"'" + p_name + "'" + "," + 
			"'" + p_dob  + "'" + "," + 
			"'" + p_country + "'" +
			");";
			
			esql.executeUpdate(trashql);
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}
	}
	
	// 2.) Book Flight for an existing customer
	public static void BookFlight(AirBooking esql)
	{
		/* Booking table */
		// bookRef CHAR(10) NOT NULL,
		// departure DATE NOT NULL,
		// flightNum CHAR(8) NOT NULL,
		// pID INTEGER NOT NULL,

		String bookref = "";
		String date = "";		
		String flightnum = "";
		String passport_number = "";
		String pid = "";
		
		try
		{		
			bookref = esql.getBookRef();
						
			// NEED TO CHECK
			System.out.println("Enter a departure date (format: YYYY-MM-DD)");
			date = str_get.nextLine();
			while(!esql.DateIsValid(date)){
				System.out.println("Invalid date, please try again.");
				System.out.println("Enter a departure date (format: YYYY-MM-DD)");
				date = str_get.nextLine();				
			}
			
			System.out.println("Enter a flight number");
			flightnum = str_get.nextLine();
			while(flightnum.length() > 8 || flightnum.length() == 0)
			{ 
				System.out.println("Your flight number was invalid length.");
				flightnum = str_get.nextLine();
			}
			while(!esql.flightNumIsValid(flightnum)){
				System.out.println("Invalid flight number, please try again.");
				System.out.println("Enter a flight number");
				flightnum = str_get.nextLine();
				while(flightnum.length() > 8 || flightnum.length() == 0)
				{ 
					System.out.println("Your flight number was invalid length.");
					flightnum = str_get.nextLine();
				}			
			}
			
			// this should be done by system automatically
			// THIS SHOULD BE A PASSPORT THEN FIND THE ASSOCIATED PID
			System.out.println("Enter a passport number");
			passport_number = str_get.nextLine();
			while(!esql.doesPassNumExist(passport_number))
			{
				System.out.println("Invalid passport number, please try again");
				System.out.println("Enter a passport number");
				passport_number = str_get.nextLine();
			}
			pid = esql.getPidFromPassNum(passport_number);
			System.out.println(pid);
			
			String trashql = "INSERT INTO booking(bookref, departure, flightnum, pid)" +
							 "VALUES('" + bookref + "', '"
								       + date + "', '"
								       + flightnum + "', "
								       + pid + ")";
			
			esql.executeUpdate(trashql);
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}
	}
	
	// 3.) Insert customer review into the ratings table
	public static void TakeCustomerReview(AirBooking esql)
	{
		/* Ratings table */
		// rID INTEGER NOT NULL,
		// pID INTEGER NOT NULL,
		// flightNum CHAR(8) NOT NULL,
		// score _SCORE NOT NULL,
		// comment TEXT,

		String rid = "";
		String passport_number = "";
		String pid = "";
		String flightnum = "";
		String score = "";
		String comment = "";
		
		try
		{

			// this should be done by system automatically
			rid = esql.getRid();

			// THIS SHOULD BE A PASSPORT THEN FIND THE ASSOCIATED PID
			System.out.println("Enter a passport number");
			passport_number = str_get.nextLine();
			while(!esql.doesPassNumExist(passport_number))
			{
				System.out.println("Invalid passport number, please try again");
				System.out.println("Enter a passport number");
				passport_number = str_get.nextLine();
			}
			pid = esql.getPidFromPassNum(passport_number);
			//System.out.println(pid);

			System.out.println("Enter a flight number");
			flightnum = str_get.nextLine();
			while(flightnum.length() > 8 || flightnum.length() == 0)
			{ 
				System.out.println("Your flight number was invalid length.");
				flightnum = str_get.nextLine();
			}
			while(!esql.flightNumIsValid(flightnum)){
				System.out.println("Invalid flight number, please try again.");
				System.out.println("Enter a flight number");
				flightnum = str_get.nextLine();
				while(flightnum.length() > 8 || flightnum.length() == 0)
				{ 
					System.out.println("Your flight number was invalid length.");
					flightnum = str_get.nextLine();
				}			
			}
			
			System.out.println("Enter a score");
			score = str_get.nextLine();
			while(!esql.scoreIsValid(score)){
				System.out.println("Invalid score, enter a score in the range 0-5.");
				System.out.println("Enter a score");
				score = str_get.nextLine();
			}
			
			System.out.println("Enter a comment (hit <Enter> with no text to skip)");
			comment = str_get.nextLine();
		
			String trashql = "INSERT INTO ratings(rid, pid, flightnum, score, comment) " + 
							 "VALUES( " + rid + ", "
										+ pid + ", '"
										+ flightnum + "', '"
										+ score + "', '"
										+ comment + "');";
			
			esql.executeUpdate(trashql);
		
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}
	}
	
	// 4.) Insert or Update a new route for the airline
	public static void InsertOrUpdateRouteForAirline(AirBooking esql) // !!!!!!!!!!!!!!!!!!!!!!!! OPTIONAL !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	{
		/* Flight table*/
		// airId INTEGER NOT NULL,
		// flightNum CHAR(8) NOT NULL,
		// origin CHAR(16) NOT NULL,
		// destination CHAR(16) NOT NULL,
		// plane CHAR(16) NOT NULL,
		// seats _SEATS NOT NULL,
		// duration _HOURS NOT NULL,
		
		try
		{
			// stuff
			
			String trashql = "";
						
			esql.executeQuery(trashql);
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}
	}
	
	// 5.) List all flights between origin and distination (i.e. flightNum,origin,destination,plane,duration) 
	public static void ListAvailableFlightsBetweenOriginAndDestination(AirBooking esql) throws Exception
	{
		try // print flight number, origin, destination, plane, and duration of flight.
		{
			System.out.println("Enter a flight origin");
			String origin = str_get.nextLine();
			origin = origin.trim();
			
			System.out.println("Enter a flight destination");
			String destination = str_get.nextLine();
			destination = destination.trim();
			
			String trashql = "select " +
			"f.flightNum, f.origin, f.destination, f.plane, f.duration " +
			"from flight f where "+
			"f.origin = " + "'" + origin +  "'" + "and " +
			"f.destination = " + "'" + destination +  "'";
			
			esql.executeQueryAndPrintResult(trashql);
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}
	}
	
	// 6.) Print the k most popular destinations based on the number of flights offered to them (i.e. destination, choices)
	public static void ListMostPopularDestinations(AirBooking esql) 
	{
		
		String dest_num = "";
		try
		{
			// NEED TO CHECK
			System.out.println("Enter number of most popular destinations to see ");
			dest_num = str_get.nextLine();
			while(!esql.isNumeric(dest_num) || Integer.parseInt(dest_num) < 0)
			{
				System.out.println("not valid number");
				System.out.println("Enter the number of desired records");
				dest_num = str_get.nextLine();
			}	
			
			String trashql = "SELECT f.destination, COUNT(f.destination)AS num_of " +
			" FROM flight f " +
			" GROUP BY f.destination " +
			" ORDER BY num_of DESC " +
			" LIMIT " + dest_num ;
			
			esql.executeQueryAndPrintResult(trashql);
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}
	}
	
	// 7.) List the k highest rated Routes (i.e. Airline Name, flightNum, Avg_Score)
	public static void ListHighestRatedRoutes(AirBooking esql)
	{	
			
		String k = "";
		try
		{
			// NEED TO CHECK
			System.out.println("How many highest rated routes would you like to see?: ");
			k = str_get.nextLine();
			while(!esql.isNumeric(k) || Integer.parseInt(k) < 0)
			{
				System.out.println("not valid number");
				System.out.println("Enter the number of desired records");
				k = str_get.nextLine();
			}	

			String trashql = "SELECT a.name, r.flightnum, f.origin, f.destination, f.plane, AVG(r.score) AS avg_score " + 
							 "FROM airline a, flight f, ratings r " +
							 "WHERE a.airid = f.airid AND f.flightnum = r.flightnum " +
							 "GROUP BY a.name, f.flightnum, r.flightnum " +
							 "ORDER BY avg_score DESC "+
							 "LIMIT " + k + ";";
			
			esql.executeQueryAndPrintResult(trashql);
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}
	}
	
	// 8.) List flight to destination in order of duration (i.e. Airline name, flightNum, origin, destination, duration, plane)
	public static void ListFlightFromOriginToDestinationInOrderOfDuration(AirBooking esql)
	{
		
		String numRecords = "";
		try
		{
			System.out.println("Enter a flight origin");
			String origin = str_get.nextLine();
			origin = origin.trim();
			
			System.out.println("Enter a flight destination");
			String destination = str_get.nextLine();
			destination = destination.trim();
			
			// NEED TO CHECK
			System.out.println("Enter the number of desired records");
			numRecords = str_get.nextLine();
			while(!esql.isNumeric(numRecords) || Integer.parseInt(numRecords) < 0)
			{
				System.out.println("not valid number");
				System.out.println("Enter the number of desired records");
				numRecords = str_get.nextLine();
			}			
			
			String trashql = "SELECT a.name, f.flightnum, f.origin, f.destination, f.plane, f.duration " +
							 "FROM airline a, flight f " +
							 "WHERE a.airid = f.airid AND f.origin = '" + origin + "' AND f.destination = '" + destination + "' " +
							 "ORDER BY f.duration " +
							 "LIMIT " + numRecords  + ";";
										
			esql.executeQueryAndPrintResult(trashql);
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}
	}
	
	// 9.) Find Number of Available Seats on a given Flight
	public static void FindNumberOfAvailableSeatsForFlight(AirBooking esql)
	{
		String flightnum = "";
		String date = "";
		
		try // print # of seats
		{
			// NEED TO CHECK
			System.out.println("Enter a flight number");
			flightnum = str_get.nextLine();
			while(flightnum.length() > 8 || flightnum.length() == 0)
			{ 
				System.out.println("Your flight number was invalid length.");
				flightnum = str_get.nextLine();
			}
			while(!esql.flightNumIsValid(flightnum)){
				System.out.println("Invalid flight number, please try again.");
				System.out.println("Enter a flight number");
				flightnum = str_get.nextLine();
				while(flightnum.length() > 8 || flightnum.length() == 0)
				{ 
					System.out.println("Your flight number was invalid length.");
					flightnum = str_get.nextLine();
				}			
			}
			
			// NEED TO CHECK
			System.out.println("Enter a flight date (YYYY-MM-DD)");;	
			date = str_get.nextLine();	
			while(!esql.DateIsValid(date)){
				System.out.println("Invalid date, please try again.");
				System.out.println("Enter a departure date (format: YYYY-MM-DD)");
				date = str_get.nextLine();				
			}	

			String trashql1 = "select seats from flight where flightnum = '" + flightnum + "';";

			// get seat number			
			List<List<String>> r1 = esql.executeQueryAndReturnResult(trashql1);
			
			String trashql2 = "select count(*) " +
			"from booking b, flight f " +
			"where f.flightnum = " + "'" + flightnum + "'" + " and " +
			"b.flightnum = " + "'" + flightnum + "'" + " and " +
			"b.departure = '" + date + "';";
			
			// get booked number
			List<List<String>> r2 = esql.executeQueryAndReturnResult(trashql2);
			
			int num1 = 0;
			int num2 = 0;
						
			if(!r1.isEmpty() && !r2.isEmpty())
			{
				num1 = Integer.parseInt(r1.get(0).get(0));
				num2 = Integer.parseInt(r2.get(0).get(0));
				int res = num1 - num2;
				System.out.print("Number of available seats: ");
				System.out.println(res);
			}
			else
			{
				System.out.println("Error could not find the flight");
			}			
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}
	}
}
