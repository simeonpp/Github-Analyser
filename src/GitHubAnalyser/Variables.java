package GitHubAnalyser;

import java.util.ArrayList;
import java.util.List;

public class Variables {
	
	// databaseCreator API
	protected static int nodeCounter = 0;						// Initialize counter to check nodes number
	protected final static int nodeLimit = 100;					// Node Limit Initialize
	protected final static String authToken = "a22ecd1b13fd684e0b5e018abe8a73d6290ef0f5";
	
	protected final static int Name1 = 0;
	protected final static int Name2 = 1;
	protected final static int RelString = 2;
	
	// GitHub Analyse API
	protected final static int MainDatabase = 0;
	protected final static int ModuleNetwork = 1;
	protected final static int CommitersNetwork = 2;
	protected static int nodeCounterAnalyse = 0;						// Initialize counter to check nodes number
	protected final static int DegreeOfAVertex = 0;
	protected final static int WeightedDegreeOfAVertex = 1;
		
		
	
	/**
	 * Method to increase the nodeCounter	
	 * @param int value to be increased with. Example: 1
	 */
	protected static void increaseNodeCounter(int increaseValueWith) {
		nodeCounter += increaseValueWith; }	
	/**
	 * Method to increase the nodeCounterAnalyse	
	 * @param int value to be increased with. Example: 1
	 */
	protected static  void increaseNodeCounterAnalyse(int increaseValueWith) {
		nodeCounterAnalyse += increaseValueWith; }	
	
	
	
	/**
	 * Method to convert Strings ArrayList to Integers ArrayList
	 * @param listOfStrings The ArrayList of Strings which will be converted
	 * @return The Integers ArrayList
	 */
	protected static ArrayList<Integer> convertStringListToIntegerList(ArrayList<String> listOfStrings) {
		ArrayList <Integer> intArray = new ArrayList <Integer>();
       
       for(int i = 0; i < listOfStrings.size(); i++)
       {
    	   intArray.add( Integer.parseInt(listOfStrings.get(i)) );
       }
       
       return intArray;
	}
	
	/**
	 * Method to convert Strings to Integers 
	 * @param string which will be converted
	 * @return the integer
	 */
	protected static Integer convertStringToInteger(String string) {
		
		int integer = Integer.parseInt(string);      
        return integer;
	}
	
	/**
	 * Method to convert Integer value to String value
	 * @param integer which will be converted
	 * @return the string value
	 */
	protected static String convertIntegerToString(int integer) {
		
		String string = String.valueOf( integer );
        return string;
	}
	
	/**
	 * 
	 * @return
	 */
	protected static boolean isNodeLimitReached() {
		if ( nodeCounter < nodeLimit ) { return false;}
		else { return true; }
	}
	
	
	
	/**
	 * Method to get the query which will be executed on the Main Database 
	 * @param networkIdentifier Integer value to identify if the database is Module or Commiters Network. Example: 0 - Module Network, 1 - Commiters Network
	 * @return the required query
	 */
	protected static String getNetworkQuery(int networkIdentifier) {
		String query = "";
		
		if ( networkIdentifier == Variables.ModuleNetwork ) {
			query = " MATCH (a:Repository)-[r:CONTRIBUTED_BY]->(b:User), (c:User)<-[r2:CONTRIBUTED_BY]-(d:Repository) "
					+ " WHERE  a.name < d.name AND  "
					 + " b.username = c.username "
				+ " RETURN  DISTINCT a.name, d.name, SUM(r.commits+r2.commits) as SUM; ";
			
		} else if ( networkIdentifier == Variables.CommitersNetwork ){
			query = " MATCH (a:User)<-[r:CONTRIBUTED_BY]-(b:Repository), (d:Repository)-[r2:CONTRIBUTED_BY]->(c:User) "
					+ " WHERE  a.username < c.username AND "
					 + " b.name = d.name   "
				+ " RETURN  DISTINCT  a.username, c.username, SUM(r.commits+r2.commits) as SUM; ";
			
		} else {
			System.out.println( "A match on Network Identifier was not found! Warning! No query will be return! " );
		}				
		return query;		
	}
	
	
	
	/**
	 * Method to get the query which will be executed on the neo4jDbpath Database 
	 * @param networkIdentifier Integer value to identify if the database is Module or Commiters Network. Example: 0 - Module Network, 1 - Commiters Network
	 * @return the required query
	 */
	protected static String getNetworkQuery2(int networkIdentifier) {
		String query = "";
		
		if ( networkIdentifier == Variables.ModuleNetwork ) {
			query = "MERGE ( n:Repository {repository: {Name}} ) RETURN n";
			
		} else if ( networkIdentifier == Variables.CommitersNetwork ){
			query = "MERGE ( n:User {username: {Name}} ) RETURN n";
			
		} else {
			System.out.println( "A match on Network Identifier was not found! Warning! No query will be return! " );
		}
				
		return query;		
	}
	
	
	
	protected static ArrayList<String> splitResultStringInParts(List<String> splitList, int listIdentifier) {
		ArrayList<String> arraylist = new ArrayList<String>();		
		for (int i = listIdentifier; i < splitList.size(); i++){
			arraylist.add(splitList.get(i));
			i += 2;
		}					
		return arraylist;
	}
}
