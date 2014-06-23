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
	protected final static int SNA_ModuleNetwork = 3;
	protected final static int SNA_CommitersNetwork = 4;
	protected static int nodeCounterAnalyse = 0;						
	protected final static int DegreeOfAVertex = 0;
	protected final static int WeightedDegreeOfAVertex = 1;
	
	// MySQL API
	protected final static String MySQL_host = "jdbc:mysql://localhost/githubanalyser";
	protected final static String MySQL_username = "root";
	protected final static String MySQL_password = "";
	protected static String TimeStamp = "2014-11-21";
	
		
		
	
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
	 * Method to convert Integers ArrayList to Strings ArrayList
	 * @param listOfIntegers The ArrayList of Integers which will be converted
	 * @return The Strings ArrayList
	 */
	protected static ArrayList<String> convertIntegerListToStringList(ArrayList<Integer> listOfIntegers) {
		ArrayList <String> stringArray = new ArrayList <String>();
       
       for(int i = 0; i < listOfIntegers.size(); i++)
       {
    	   stringArray.add( String.valueOf(listOfIntegers.get(i)) );
       }
       
       return stringArray;
	}
	
	/**
	 * Method to convert Double ArrayList to Strings ArrayList
	 * @param listOfDoubles The ArrayList of Doubles which will be converted
	 * @return The Strings ArrayList
	 */
	protected static ArrayList<String> convertDoubleListToStringList(ArrayList<Double> listOfDoubles) {
		ArrayList <String> stringArray = new ArrayList <String>();
       
       for(int i = 0; i < listOfDoubles.size(); i++)
       {
    	   stringArray.add( String.valueOf(listOfDoubles.get(i)) );
       }
       
       return stringArray;
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
		
		if ( networkIdentifier == ModuleNetwork ) {
			query = " MATCH (a:Repository)-[r:CONTRIBUTED_BY]->(b:User), (c:User)<-[r2:CONTRIBUTED_BY]-(d:Repository) "
					+ " WHERE  a.name < d.name AND  "
					 + " b.username = c.username "
				+ " RETURN  DISTINCT a.Id, d.Id, SUM(r.commits+r2.commits) as SUM; ";   // a.name, d.name (for repository name not ID)
			
		} else if ( networkIdentifier == CommitersNetwork ){
			query = " MATCH (a:User)<-[r:CONTRIBUTED_BY]-(b:Repository), (d:Repository)-[r2:CONTRIBUTED_BY]->(c:User) "
					+ " WHERE  a.username < c.username AND "
					 + " b.name = d.name   "
				+ " RETURN  DISTINCT  a.username, c.username, SUM(r.commits+r2.commits) as SUM; ";
			
		} else if ( networkIdentifier == SNA_ModuleNetwork ) {
			query = " MATCH (a:Repository) "
					+ "RETURN a.Id ";			
		} else if ( networkIdentifier == SNA_CommitersNetwork ) {
			query = " MATCH (a:User) "
					+ "RETURN a.username ";			
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
			query = "MERGE ( n:Repository {Id: {Name}} ) RETURN n";
			
		} else if ( networkIdentifier == Variables.CommitersNetwork ){
			query = "MERGE ( n:User {username: {Name}} ) RETURN n";
			
		} else {
			System.out.println( "A match on Network Identifier was not found! Warning! No query will be return! " );
		}				
		return query;		
	}
	
	
	protected static String getAnalyseQuery(int networkIdentifier) {
		String query = "";
		
		if ( networkIdentifier == ModuleNetwork ) {
			query = "MATCH (a:Repository) RETURN a.repository;";
			
		} else if ( networkIdentifier == CommitersNetwork ){
			query = "MATCH (a:User) RETURN a.username;";
			
		} else {
			System.out.println( "A match on Network Identifier was not found! Warning! No query will be return! " );
		}		
		return query;
	}
	
	
	protected static String getParametersQuery(int networkIdentifier,	int paremeter, String repoName) {
		String nodeLabel = "";
		String nodeProperty = "";
		String queryReturnString = "";
		
		if ( networkIdentifier == SNA_ModuleNetwork ) { nodeLabel = "Repository"; nodeProperty ="Id"; }
		else if ( networkIdentifier == SNA_CommitersNetwork ) { nodeLabel = "User"; nodeProperty ="username"; }
		else { }
		
		if ( paremeter == Variables.DegreeOfAVertex ) { queryReturnString = "count(r);"; }
		else if ( paremeter == Variables.WeightedDegreeOfAVertex ) { queryReturnString = "SUM(r.degree)"; }
		else { }
		
		String query = " MATCH (a:" + nodeLabel + ")<-[r:EDGE]->(b:" + nodeLabel + ") "
		   + " WHERE a." + nodeProperty + " = '" + repoName + "' "
		   + " RETURN " + queryReturnString;		
		return query;
	}	
	
	
	protected static ArrayList<String> splitResultStringInThreeParts(List<String> splitList, int listIdentifier) {
		ArrayList<String> arraylist = new ArrayList<String>();		
		for (int i = listIdentifier; i < splitList.size(); i++){
			arraylist.add(splitList.get(i));
			i += 2;
		}					
		return arraylist;
	}
	
	
	protected static ArrayList<String> splitResultStringInOnePart(List<String> splitList) {
		ArrayList<String> arraylist = new ArrayList<String>();			
		for (int i = 0; i < splitList.size(); i++){
			arraylist.add(splitList.get(i));
		}
		return arraylist;
	}
	
	
	public static ArrayList<String> splitResultStringInPart( List<String> splitList, int startPoint, int incrementer ) {
		ArrayList<String> arraylist = new ArrayList<String>();			
		for (int i = startPoint; i < splitList.size(); i++){
			arraylist.add(splitList.get(i));
			i += incrementer;
		}
		return arraylist;
	}
	
	
	/**
	 * Method to print the results of two ArrayLists
	 * @param repositoryNames ArrayList of Strings 
	 * @param metric ArrayList of Strings
	 * @param string 
	 * @param displayDatabaseName 
	 */
	protected static void printParameterResults(ArrayList<String> repositoryNames, ArrayList<Integer> metric, String displayDatabaseName, String metricName) {
		System.out.println( "\n\n" + metricName + " of " + displayDatabaseName + " Vertexe(s):" );
		for (int i = 0; i < repositoryNames.size(); i++ ){
			if(i == 0){
				System.out.printf("%s => %d", repositoryNames.get(i), metric.get(i) );
			} else {
				System.out.printf("   ||   %s => %d", repositoryNames.get(i), metric.get(i) );
			}
		}	
		System.out.println();
	}
	
	/**
	 * Method to increment String Integer value
	 * @param String value which will be incremented
	 * @param i Integer value which will be added to the string value. Example: 1;
	 * @return the icremented String Value
	 */
	protected static String incrementStringIntegerValue(String nextSqlString, int i) {
		String incrementedValue;
		
		int temp = convertStringToInteger(nextSqlString) + i; 
		incrementedValue = convertIntegerToString(temp);
		
		return incrementedValue;
	}
}
